package cn.spider.framework.flow.load.loader;
import cn.spider.framework.flow.SpiderCoreVerticle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * <p>自定义类加载器，主要用来加载指定目录下的所有以.jar结尾的文件</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @github https://github.com/kobeyk
 * @date created on  下午9:16 2022/11/23
 */
@Slf4j
public class HotClassLoader extends URLClassLoader {

    /**设定插件默认放置的路径*/
    private static final String PLUGINS_DIR = "classpath:plugins";

    public HotClassLoader(ClassLoader parent) {
        super(new URL[0],parent);
    }


    private static File checkPluginDir() {
        File file; /** 首先先判断classpath下plugins是否存在，如果不存在，帮用户创建 */
        try{
            file = ResourceUtils.getFile(PLUGINS_DIR);
        }catch (Exception e){
            String classesPath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
            String pluginsDir = classesPath+"plugins";
            file = new File(pluginsDir);
            if (!file.exists()){
                /**不存在就创建*/
                file.mkdirs();
            }
        }
        return file;
    }

    /**
     * 使用指定的类加载加载单个jar文件中的所有class文件到JVM中，同时向Spring IOC容器中注入BD
     * @param jarPath jar类路径，格式如：classpath:plugins/xxxx.jar
     * @param classLoader 类加载器
     */
    public List<Class> loadJar(String jarPath,HotClassLoader classLoader) throws Exception{

        /**拿到jar文件对象*/
        File file = jarPath.startsWith("classpath:") ? ResourceUtils.getFile(jarPath) : new File(jarPath);

        /**获取新的类加载器*/
        if (classLoader == null){
            classLoader = SpiderCoreVerticle.factory.getBean(HotClassLoader.class);
        }
        try {
            if (jarPath.startsWith("classpath:")) {
                classLoader.addURL(new URI(jarPath).toURL());
            } else {
                classLoader.addURL(file.toURI().toURL());
            }

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("通过url 添加 jar 失败");
        }

        List<Class> classNameList = injectedBeans(classLoader, file);
        return classNameList;
    }


    private List<Class> injectedBeans(HotClassLoader classLoader, File file) {
        List<Class> classList = new ArrayList<>();
        /** 遍历 jar 包中的类 */
        try (JarFile jarFile = new JarFile(file.getAbsolutePath())) {
            List<JarEntry> jarEntryList = jarFile.stream().sequential().collect(Collectors.toList());
            for (JarEntry jarEntry : jarEntryList) {
                String jarName = jarEntry.getName();
                if (!jarName.endsWith(".class")) {
                    continue;
                }
                /**类的完全限定名处理*/
                String className = jarName.replace(".class", "").replace("/", ".");

                /**使用指定的类加载器加载该类*/
                Class<?> clz = classLoader.loadClass(className, false);

                classList.add(clz);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("jar包解析失败");
        }
        return classList;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if(name.startsWith("java.")){
            return ClassLoader.getSystemClassLoader().loadClass(name);
        }
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            if (resolve) {
                return loadClass(name);
            }
            return clazz;
        }
        return super.loadClass(name, resolve);
    }

}
