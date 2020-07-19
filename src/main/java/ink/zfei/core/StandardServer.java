package ink.zfei.core;

import ink.zfei.life.LifecycleBase;
import ink.zfei.life.LifecycleException;
import ink.zfei.startup.Catalina;

public class StandardServer extends LifecycleBase implements Server {

    private Catalina catalina = null;

    private Service services[] = new Service[0];

    @Override
    public void setCatalina(Catalina catalina) {
        this.catalina = catalina;
    }

    @Override
    public void addService(Service service) {
        service.setServer(this);
        services[0] = service;
    }

    private int port = 8005;

    @Override
    public void await() {
        if( port == -2 ) {
            return;
        }
    //监听8005端口，主线程阻塞，接收到命令后匹配shuadown，继续stop

    }

    @Override
    protected void initInternal() throws LifecycleException {
        for (int i = 0; i < services.length; i++) {
            services[i].init();
        }
    }

    @Override
    protected void startInternal() throws LifecycleException {

    }

    @Override
    protected void stopInternal() throws LifecycleException {

    }

    @Override
    protected void destroyInternal() throws LifecycleException {

    }
}
