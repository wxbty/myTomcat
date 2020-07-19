package ink.zfei.core;

import ink.zfei.life.Lifecycle;
import ink.zfei.startup.Catalina;

public interface Server extends Lifecycle {

    public void setCatalina(Catalina catalina);

    public void addService(Service service);

    void await();
}
