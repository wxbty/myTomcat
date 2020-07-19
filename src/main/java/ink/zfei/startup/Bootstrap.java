package ink.zfei.startup;

import ink.zfei.life.LifecycleException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Bootstrap {

    private static Bootstrap daemon = null;
    private Catalina catalinaDaemon = null; //这个是对Catalina对象的引用


    protected ClassLoader sharedLoader = null;

    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.init();
        daemon = bootstrap;

        String command = "start";
        if (args.length > 0) {
            //通过sh startup.sh启动，args默认传入start
            command = args[args.length - 1];
        }
        if (command.equals("start")) {
            daemon.setAwait(true);
            daemon.load(args);
            daemon.start();
        }

    }

    private void start() throws IOException, SAXException, LifecycleException {
        catalinaDaemon.start();
    }

    private void setAwait(boolean b) {
        catalinaDaemon.setAwait(b);
    }

    public void init() throws MalformedURLException {
        initClassLoaders();
        Catalina startupInstance = new Catalina();
        catalinaDaemon = startupInstance;
    }

    private void initClassLoaders() throws MalformedURLException {

        String value = "shared.loader";
        value = replace(value); //替换其中的环境变量，如${catalina.base}、${catalina.home}
        String[] repositoryPaths = getPaths(value);

        List<ClassLoaderFactory.Repository> repositories = new ArrayList<>();
        for (String repository : repositoryPaths) {
            repositories.add(
                    new ClassLoaderFactory.Repository(repository));
        }
        ClassLoader parent = null;
        sharedLoader = ClassLoaderFactory.createClassLoader(repositories, parent);
    }

    //按逗号分隔，去掉每一项开头和结尾的双引号
    private String[] getPaths(String value) {
        return value.split(",");
    }

    private String replace(String value) {
        return value;
    }

    private void load(String[] arguments) //通过反射，调用Catalina.load(String[])
            throws Exception {

        catalinaDaemon.load(arguments);

    }
}
