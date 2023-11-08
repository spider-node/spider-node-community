package cn.spider.framework.transaction.sdk.datasource.loader;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
/**
 * @program: flow-cloud
 * @description:
 * @author: dds
 * @create: 2022-07-26 21:08
 */
public class DruidIsolationClassLoader extends URLClassLoader {
    private final static String[] DRUID_CLASS_PREFIX = new String[]{"com.alibaba.druid.", "io.seata.sqlparser.druid."};

    private final static DruidIsolationClassLoader INSTANCE = new DruidIsolationClassLoader(DefaultDruidLoader.get());

    DruidIsolationClassLoader(DruidLoader druidLoader) {
        super(getDruidUrls(druidLoader), DruidIsolationClassLoader.class.getClassLoader());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (String prefix : DRUID_CLASS_PREFIX) {
            if (name.startsWith(prefix)) {
                return loadInternalClass(name, resolve);
            }
        }
        return super.loadClass(name, resolve);
    }

    private Class<?> loadInternalClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c;
        synchronized (getClassLoadingLock(name)) {
            c = findLoadedClass(name);
            if (c == null) {
                c = findClass(name);
            }
        }
        if (c == null) {
            throw new ClassNotFoundException(name);
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }

    private static URL[] getDruidUrls(DruidLoader druidLoader) {
        List<URL> urls = new ArrayList<>();
        urls.add(findClassLocation(DruidIsolationClassLoader.class));
        urls.add(druidLoader.getEmbeddedDruidLocation());
        return urls.toArray(new URL[0]);
    }

    private static URL findClassLocation(Class<?> clazz) {
        CodeSource cs = clazz.getProtectionDomain().getCodeSource();
        if (cs == null) {
            throw new IllegalStateException("Not a normal druid startup environment");
        }
        return cs.getLocation();
    }

    public static DruidIsolationClassLoader get() {
        return INSTANCE;
    }
}
