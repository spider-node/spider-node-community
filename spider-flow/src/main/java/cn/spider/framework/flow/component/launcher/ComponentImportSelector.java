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
import cn.spider.framework.flow.component.dynamic.KValueDynamicComponent;
import cn.spider.framework.flow.component.dynamic.ProcessDynamicComponent;
import cn.spider.framework.flow.component.dynamic.RoleDynamicComponent;
import cn.spider.framework.flow.component.dynamic.SubProcessDynamicComponent;
import cn.spider.framework.flow.component.expression.BasicExpressionAliasRegister;
import cn.spider.framework.flow.component.expression.ExpressionAliasParser;
import cn.spider.framework.flow.component.expression.ExpressionAliasRegister;
import cn.spider.framework.flow.component.instruct.JsScriptInstruct;
import cn.spider.framework.flow.engine.thread.hook.ThreadSwitchHook;
import cn.spider.framework.flow.engine.thread.hook.ThreadSwitchHookProcessor;
import cn.spider.framework.flow.engine.thread.hook.ThreadSwitchLogHook;
import cn.spider.framework.flow.container.processor.*;
import com.google.common.collect.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.Order;
import java.util.List;
import java.util.Map;

/**
 * Spring 容器中注册组件
 * <p>
 * Spring 4.2.x 之前的版本无法使用 @Import 导入一个普通的 Class
 *
 * @author lyakn
 */
@Import({ConfigResourceResolver.class, KstryContextResolver.class})
@Configuration
@Order(1)
public class ComponentImportSelector {

    @Bean
    public StartEventPostProcessor getImmutablePostProcessor() {
        return new ImmutablePostProcessor();
    }

    @Bean
    public StartEventPostProcessor getMarkIndexPostProcessor() {
        return new MarkIndexPostProcessor();
    }

    @Bean
    public StartEventPostProcessor getIterablePostProcessor() {
        return new IterablePostProcessor();
    }

    @Bean
    public StartEventPostProcessor getRearrangeFlowPostProcessor(ApplicationContext applicationContext) {
        return new RearrangeFlowPostProcessor(applicationContext);
    }

    @Bean
    public StartEventPostProcessor getVerifyFlowPostProcessor(ApplicationContext applicationContext) {
        return new VerifyFlowPostProcessor(applicationContext);
    }

    @Bean
    public StartEventPostProcessor getExpressionParserProcessor(ExpressionAliasParser expressionAliasParser) {
        return new ExpressionParserProcessor(expressionAliasParser);
    }

    @Bean
    public StartEventProcessor getStartEventProcessor(List<StartEventPostProcessor> processorList) {
        return new StartEventProcessor(processorList);
    }

    @Bean
    public SpringBpmnDiagramRegister springBpmnDiagramRegister(ApplicationContext applicationContext) {
        return new SpringBpmnDiagramRegister(applicationContext);
    }

    @Bean
    public RoleDynamicComponent roleDynamicComponent(ApplicationContext applicationContext) {
        return new RoleDynamicComponent(applicationContext);
    }

    @Bean
    public SubProcessDynamicComponent subProcessDynamicComponent(ApplicationContext applicationContext) {
        return new SubProcessDynamicComponent(applicationContext);
    }

    @Bean
    public ProcessDynamicComponent processDynamicComponent(ApplicationContext applicationContext,
                                                           StartEventProcessor startEventProcessor, SubProcessDynamicComponent subProcessDynamicComponent) {
        return new ProcessDynamicComponent(applicationContext, startEventProcessor, subProcessDynamicComponent);
    }

    @Bean
    public KValueDynamicComponent kValueDynamicComponent(ApplicationContext applicationContext) {
        return new KValueDynamicComponent(applicationContext);
    }

    @Bean
    @SuppressWarnings("all")
    public ThreadSwitchHookProcessor threadSwitchHookProcessor(ApplicationContext applicationContext) {
        List<ThreadSwitchHook<Object>> threadSwitchHookList = Lists.newArrayList();
        Map<String, ThreadSwitchHook> threadSwitchHookMap = applicationContext.getBeansOfType(ThreadSwitchHook.class);
        threadSwitchHookMap.values().forEach(hook -> threadSwitchHookList.add(hook));
        OrderComparator.sort(threadSwitchHookList);
        return new ThreadSwitchHookProcessor(threadSwitchHookList);
    }

    @Bean
    public ThreadSwitchLogHook logThreadSwitchHook() {
        return new ThreadSwitchLogHook();
    }

    @Bean
    public JsScriptInstruct jsScriptInstruct() {
        return new JsScriptInstruct();
    }

    @Bean
    public ExpressionAliasParser expressionAliasParser(List<ExpressionAliasRegister> registerList) {
        return new ExpressionAliasParser(registerList);
    }

    @Bean
    public BasicExpressionAliasRegister basicExpressionAliasRegister() {
        return new BasicExpressionAliasRegister();
    }
}
