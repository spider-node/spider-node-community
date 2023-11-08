package cn.spider.framework.flow.load;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * <p>bean工具类</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @github https://github.com/kobeyk
 * @date created on  下午9:00 2022/11/23
 */
@Component
public class SpringBeanUtils implements ApplicationContextAware {

    private static ConfigurableApplicationContext context;
    private static DefaultListableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = (ConfigurableApplicationContext)applicationContext;
        beanFactory = (DefaultListableBeanFactory)context.getBeanFactory();
    }

    /**往ioc容器中注册指定的beanDefinition*/
    public static void registerBean(String beanName,Class<?> clz){
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clz);
        AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();
        beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
        beanFactory.registerBeanDefinition(beanName,beanDefinition);
    }

    /**移除指定bean*/
    public static void removeBean(String beanName){
        beanFactory.removeBeanDefinition(beanName);
    }

    /**判断beanName是否存在*/
    public static boolean contains(String beanName){
        return context.containsBean(beanName);
    }

    /**通过beanName和class类型获取对应的bean*/
    public static <T> T getBean(String beanName,Class<T> clazz){
        return beanFactory.getBean(beanName,clazz);
    }

    /**通过class获取对应的bean*/
    public static <T> T getBean(Class<T> clazz){
        return beanFactory.getBean(clazz);
    }

    /**获取指定注解的beanMap*/
    public static Map<String, Object> getBeanMap(Class<? extends Annotation> clazz){
        return context.getBeansWithAnnotation(clazz);
    }

}
