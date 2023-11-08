package cn.spider.framework.flow.load.loader;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @BelongsProject: spider-node
 * @BelongsPackage: cn.spider.framework.flow.load.loader
 * @Author: dengdongsheng
 * @CreateTime: 2023-03-20  22:24
 * @Description: 指定名称进行加载class对象
 * @Version: 1.0
 */
public class AppointClassLoader extends HotClassLoader {

    private URL jar;
    private URLClassLoader classLoader;


    public AppointClassLoader(URL jar,ClassLoader parent) {
        super(parent);
        this.jar = jar;
        classLoader = new URLClassLoader(new URL[]{jar},parent);
    }

    /**
     * 在指定包路径下加载子类
     *
     * @param
     * @param
     * @return
     */
    public Set<Class> loadClassNew(String basePackage) {
        JarFile jarFile;
        try {
            jarFile = ((JarURLConnection) jar.openConnection()).getJarFile();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return loadClassFromJar(basePackage, jarFile);
    }

    private Set<Class> loadClassFromJar(String basePackage, JarFile jar) {
        Set<Class> classes = new HashSet<>();
        String pkgPath = basePackage.replace(".", "/");
        Enumeration<JarEntry> entries = jar.entries();
        Class<?> clazz;
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.charAt(0) == '/') {
                entryName = entryName.substring(1);
            }
            if (jarEntry.isDirectory() || !entryName.startsWith(pkgPath) || !entryName.endsWith(".class")) {
                continue;
            }
            String className = entryName.substring(0, entryName.length() - 6);
            clazz = loadClassByName(className.replace("/", "."));
            if (clazz != null) {
                classes.add(clazz);
            }
        }
        return classes;
    }

    private Class<?> loadClassByName(String name) {
        try {
            // step1: 查询class对于是否存在，不存在进行卸载
            // step2: 如果存在进行卸载class对象
            // step3: taskService的代理对象进行移除。
            // step3: 重新加载
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unloadJar() {
        try {
            classLoader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        classLoader = null;
    }

}
