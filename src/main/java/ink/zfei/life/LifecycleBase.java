/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ink.zfei.life;

public abstract class LifecycleBase implements Lifecycle {

    /**
     * The current state of the source component.
     */
    private volatile LifecycleState state = LifecycleState.NEW;

    private final LifecycleSupport lifecycle = new LifecycleSupport(this);

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return lifecycle.findLifecycleListeners();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycle.removeLifecycleListener(listener);
    }


    /**
     * Allow sub classes to fire {@link Lifecycle} events.
     *
     * @param type Event type
     * @param data Data associated with event.
     */
    protected void fireLifecycleEvent(String type, Object data) {
        lifecycle.fireLifecycleEvent(type, data);
    }


    @Override
    public final synchronized void init() throws LifecycleException {
        if (!state.equals(LifecycleState.NEW)) {
            invalidTransition(Lifecycle.BEFORE_INIT_EVENT);
        }
        setStateInternal(LifecycleState.INITIALIZING, null, false);
        initInternal();
        setStateInternal(LifecycleState.INITIALIZED, null, false);
    }


    protected abstract void initInternal() throws LifecycleException;

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void start() throws LifecycleException {

        if (LifecycleState.STARTING_PREP.equals(state) ||
                LifecycleState.STARTING.equals(state) ||
                LifecycleState.STARTED.equals(state)) {
            return;
        }

        if (state.equals(LifecycleState.NEW)) {
            init();
        } else if (state.equals(LifecycleState.FAILED)) {
            stop();
        } else if (!state.equals(LifecycleState.INITIALIZED) &&
                !state.equals(LifecycleState.STOPPED)) {
            invalidTransition(Lifecycle.BEFORE_START_EVENT);
        }

        setStateInternal(LifecycleState.STARTING_PREP, null, false);

        try {
            startInternal();
        } catch (Throwable t) {
            throw new LifecycleException(
                    String.format("lifecycleBase.startFail", toString()), t);
        }

        if (state.equals(LifecycleState.FAILED) ||
                state.equals(LifecycleState.MUST_STOP)) {
            stop();
        } else {
            if (!state.equals(LifecycleState.STARTING)) {
                invalidTransition(Lifecycle.AFTER_START_EVENT);
            }

            setStateInternal(LifecycleState.STARTED, null, false);
        }
    }


   
    protected abstract void startInternal() throws LifecycleException;


    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void stop() throws LifecycleException {

        if (LifecycleState.STOPPING_PREP.equals(state) ||
                LifecycleState.STOPPING.equals(state) ||
                LifecycleState.STOPPED.equals(state)) {
            return;
        }

        if (state.equals(LifecycleState.NEW)) {
            state = LifecycleState.STOPPED;
            return;
        }

        if (!state.equals(LifecycleState.STARTED) &&
                !state.equals(LifecycleState.FAILED) &&
                !state.equals(LifecycleState.MUST_STOP)) {
            invalidTransition(Lifecycle.BEFORE_STOP_EVENT);
        }

        if (state.equals(LifecycleState.FAILED)) {
            // Don't transition to STOPPING_PREP as that would briefly mark the
            // component as available but do ensure the BEFORE_STOP_EVENT is
            // fired
            fireLifecycleEvent(BEFORE_STOP_EVENT, null);
        } else {
            setStateInternal(LifecycleState.STOPPING_PREP, null, false);
        }

        try {
            stopInternal();
        } catch (Throwable t) {
            setStateInternal(LifecycleState.FAILED, null, false);
            throw new LifecycleException(
                    String.format("lifecycleBase.stopFail", toString()), t);
        }

        if (state.equals(LifecycleState.MUST_DESTROY)) {
            // Complete stop process first
            setStateInternal(LifecycleState.STOPPED, null, false);

            destroy();
        } else if (!state.equals(LifecycleState.FAILED)) {
            // Shouldn't be necessary but acts as a check that sub-classes are
            // doing what they are supposed to.
            if (!state.equals(LifecycleState.STOPPING)) {
                invalidTransition(Lifecycle.AFTER_STOP_EVENT);
            }

            setStateInternal(LifecycleState.STOPPED, null, false);
        }
    }


    protected abstract void stopInternal() throws LifecycleException;


    @Override
    public final synchronized void destroy() throws LifecycleException {
        if (LifecycleState.FAILED.equals(state)) {
                // Triggers clean-up
                stop();
        }

        if (LifecycleState.DESTROYING.equals(state) ||
                LifecycleState.DESTROYED.equals(state)) {
            return;
        }

        if (!state.equals(LifecycleState.STOPPED) &&
                !state.equals(LifecycleState.FAILED) &&
                !state.equals(LifecycleState.NEW) &&
                !state.equals(LifecycleState.INITIALIZED)) {
            invalidTransition(Lifecycle.BEFORE_DESTROY_EVENT);
        }

        setStateInternal(LifecycleState.DESTROYING, null, false);

        try {
            destroyInternal();
        } catch (Throwable t) {
            setStateInternal(LifecycleState.FAILED, null, false);
            throw new LifecycleException(
                    String.format("lifecycleBase.destroyFail", toString()), t);
        }

        setStateInternal(LifecycleState.DESTROYED, null, false);
    }


    protected abstract void destroyInternal() throws LifecycleException;

    @Override
    public LifecycleState getState() {
        return state;
    }
    @Override
    public String getStateName() {
        return getState().toString();
    }

    protected synchronized void setState(LifecycleState state)
            throws LifecycleException {
        setStateInternal(state, null, true);
    }

    protected synchronized void setState(LifecycleState state, Object data)
            throws LifecycleException {
        setStateInternal(state, data, true);
    }

    private synchronized void setStateInternal(LifecycleState state,
                                               Object data, boolean check) throws LifecycleException {

        if (check) {
            if (state == null) {
                invalidTransition("null");
                return;
            }

            if (!(state == LifecycleState.FAILED ||
                    (this.state == LifecycleState.STARTING_PREP &&
                            state == LifecycleState.STARTING) ||
                    (this.state == LifecycleState.STOPPING_PREP &&
                            state == LifecycleState.STOPPING) ||
                    (this.state == LifecycleState.FAILED &&
                            state == LifecycleState.STOPPING))) {
                invalidTransition(state.name());
            }
        }

        this.state = state;
        String lifecycleEvent = state.getLifecycleEvent();
        if (lifecycleEvent != null) {
            fireLifecycleEvent(lifecycleEvent, data);
        }
    }

    private void invalidTransition(String type) throws LifecycleException {
        String msg = String.format("lifecycleBase.invalidTransition", type,
                toString(), state);
        throw new LifecycleException(msg);
    }
}
