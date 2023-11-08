/*
 *
 *  * Copyright (c) 2020-2023, Lykan (jiashuomeng@gmail.com).
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package cn.spider.framework.flow.component.launcher;

import cn.spider.framework.flow.annotation.EnableKstry;
import cn.spider.framework.flow.component.dynamic.ProcessDynamicComponent;
import cn.spider.framework.flow.exception.ExceptionEnum;
import cn.spider.framework.flow.resource.config.*;
import cn.spider.framework.flow.resource.factory.KValueFactory;
import cn.spider.framework.flow.resource.factory.StartEventFactory;
import cn.spider.framework.flow.SpiderCoreVerticle;
import cn.spider.framework.flow.util.AssertUtil;
import cn.spider.framework.flow.util.GlobalUtil;
import cn.spider.framework.flow.util.ProxyUtil;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.mysqlclient.MySQLPool;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * 配置资源加载
 *
 * @author lykan
 */
public class ConfigResourceResolver implements ApplicationContextAware {

    private ConfigurableApplicationContext applicationContext;

    @Bean
    @Conditional(ConfigResourceResolver.ClassPathSourceCondition.class)
    public ConfigSource getBpmnClassPathConfigSource() {
        return new BpmnClassPathConfigSource();
    }


    @Bean
    public ConfigSource AppointBpmnClassPatchConfigSource() {
        return new AppointBpmnClassPatchConfigSource();
    }

    @Bean
    public ConfigSource getCodeConfigSource() {
        return new BpmnDiagramConfigSource(this.applicationContext);
    }

    @Bean
    @Conditional(ConfigResourceResolver.PropertiesSourceCondition.class)
    public ConfigSource getPropertiesClassPathConfigSource() {
        return new PropertiesClassPathConfigSource();
    }

    @Bean
    @DependsOn({"springUtil"})
    public StartEventFactory getStartEventFactory(ProcessDynamicComponent processDynamicComponent, MySQLPool mySQLPool) {
        return new StartEventFactory(applicationContext, processDynamicComponent,mySQLPool);
    }

    @Bean
    public KValueFactory getKValueFactory() {
        return new KValueFactory(applicationContext);
    }

    @Bean
    @Conditional(ConfigResourceResolver.MissingPropertySourcesPlaceholderConfigurer.class)
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = GlobalUtil.transferNotEmpty(applicationContext, ConfigurableApplicationContext.class);
    }

    /**
     * 配置远程的配置数据
     * @return
     */
    private static EnableKstry getEnableKstryAnn(ListableBeanFactory beanFactory) {
        Map<String, Object> enableKstryMap = beanFactory.getBeansWithAnnotation(EnableKstry.class);
        AssertUtil.oneSize(enableKstryMap.values(), ExceptionEnum.ENABLE_KSTRY_NUMBER_ERROR);

        Object target = enableKstryMap.values().iterator().next();
        Class<?> targetClass = ProxyUtil.noneProxyClass(target);
        return AnnotationUtils.findAnnotation(targetClass, EnableKstry.class);
    }

    private static class ClassPathSourceCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, @Nonnull AnnotatedTypeMetadata metadata) {

            return true;
        }
    }

    private static class PropertiesSourceCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, @Nonnull AnnotatedTypeMetadata metadata) {
            return true;
        }
    }

    private static class MissingPropertySourcesPlaceholderConfigurer implements Condition {

        @Override
        public boolean matches(ConditionContext context, @Nonnull AnnotatedTypeMetadata metadata) {
            if (context.getBeanFactory() == null) {
                return false;
            }
            Map<String, PropertySourcesPlaceholderConfigurer> beansOfType = context.getBeanFactory().getBeansOfType(PropertySourcesPlaceholderConfigurer.class);
            return MapUtils.isEmpty(beansOfType);
        }
    }
}
