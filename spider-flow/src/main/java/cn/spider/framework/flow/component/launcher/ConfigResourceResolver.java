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
import cn.spider.framework.domain.sdk.interfaces.VersionInterface;
import cn.spider.framework.flow.component.dynamic.ProcessDynamicComponent;
import cn.spider.framework.flow.resource.config.*;
import cn.spider.framework.flow.resource.factory.KValueFactory;
import cn.spider.framework.flow.resource.factory.StartEventFactory;
import cn.spider.framework.flow.util.GlobalUtil;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLPool;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.type.AnnotatedTypeMetadata;
import javax.annotation.Nonnull;
import java.util.Map;

/**
 * 配置资源加载
 *
 * @author dds
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
    public StartEventFactory getStartEventFactory(ProcessDynamicComponent processDynamicComponent, MySQLPool mySQLPool, VersionInterface versionInterface,Vertx vertx) {
        return new StartEventFactory(applicationContext, processDynamicComponent,mySQLPool,versionInterface,vertx);
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
