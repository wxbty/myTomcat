package ink.zfei.startup;

import java.util.List;

public class ClassLoaderFactory {


    public static ClassLoader createClassLoader(List<Repository> repositories, ClassLoader parent) {
        return parent;
    }

    public static class Repository {
        private final String location;

        public Repository(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

    }
}
