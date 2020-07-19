package ink.zfei.startup;

import ink.zfei.core.Server;
import ink.zfei.life.LifecycleException;
import org.apache.commons.digester3.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class Catalina {

    protected ClassLoader parentClassLoader =
            Catalina.class.getClassLoader();

    protected String configFile = "conf/server.xml";

    protected boolean await = false;

    protected Server server = null;

    public void setParentClassLoader(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    public void setAwait(boolean b) {
        await = b;
    }

    public void load(String args[]) throws IOException, SAXException,LifecycleException {

        arguments(args);
        load();

    }

    protected void arguments(String args[]) {
        //如果第n个参数是-config,取n+1个参数作为config路径
        int n = 0;
        if (args[n].equals("-config")) {
            configFile = args[n + 1];
        }
    }

    public void load() throws IOException, SAXException, LifecycleException {
        initDirs();
        initNaming();
        Digester digester = new Digester();
        //把Catalina对象push进Digester的栈顶，重要！！！
        digester.push(this);

        /*addObjectCreate：遇到标签后创建对象并压入栈顶。
         *addSetProperties：遇到标签后设置栈顶元素对象的属性。
         *addSetNext：遇到标签后将栈顶元素（child）注入到栈顶下一个元素（parent）,即互相注入。
         */
        digester.addObjectCreate("Server",
                "org.apache.catalina.core.StandardServer",
                "className");
        digester.addSetProperties("Server");
        digester.addSetNext("Server", //前缀模式
                "setServer", //方法名
                "org.apache.catalina.Server"); //方法参数类型

        File file = new File(configFile);
        InputSource inputSource = new InputSource(file.toURI().toURL().toString());

        digester.parse(inputSource);
        getServer().setCatalina(this);
        getServer().init();
    }

    //设置catalina.useNaming与
    //java.naming.factory.url.pkgs与java.naming.factory.initial这三个系统属性
    private void initNaming() {
    }

    //检查java.io.tmpdir变量是否存在
    protected void initDirs() {

    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }


    protected boolean useShutdownHook = true;
    /**
     * Start a new server instance.
     */
    public void start() throws LifecycleException, SAXException, IOException {

        if (getServer() == null) {
            load();
        }

        if (getServer() == null) {
            System.out.println("Cannot start server. Server instance is not configured.");
            return;
        }

        long t1 = System.nanoTime();

        // Start the new server
        try {
            getServer().start();
        } catch (LifecycleException e) {
            getServer().destroy();
            return;
        }

        long t2 = System.nanoTime();
        System.out.println("Server startup in " + ((t2 - t1) / 1000000) + " ms");

        // Register shutdown hook

        if (await) {
            await();
            stop();
        }
    }

    private void stop() {
    }


    public void setUseShutdownHook(boolean useShutdownHook) {
        this.useShutdownHook = useShutdownHook;
    }

    public void await() {

        getServer().await();

    }
}
