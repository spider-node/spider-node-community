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
package cn.spider.framework.param.result.build;

import cn.spider.framework.annotation.enums.ScopeTypeEnum;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author dds
 */
public class ParamInjectDef {

    private boolean needInject;

    private Class<?> paramType;

    private final String fieldName;

    private String targetName;

    private List<ParamInjectDef> fieldInjectDefList;

    private ScopeTypeEnum scopeTypeEnum;

    public ParamInjectDef(boolean needInject, Class<?> paramType, String fieldName, MethodWrapper.TaskFieldProperty taskFieldProperty) {
        this.needInject = needInject;
        this.paramType = paramType;
        this.fieldName = fieldName;
        if (taskFieldProperty != null) {
            this.scopeTypeEnum = taskFieldProperty.getScopeDataEnum();
            if(this.scopeTypeEnum.equals(ScopeTypeEnum.REQUEST)){
                this.targetName = this.scopeTypeEnum.getKey()+"."+taskFieldProperty.getName();
            }else {
                this.targetName = taskFieldProperty.getName();
            }
        }
    }

    public ParamInjectDef(String fieldName, String targetName) {
        this.fieldName = fieldName;
        this.targetName = targetName;
    }

    public boolean notNeedInject() {
        return !needInject;
    }

    public Class<?> getParamType() {
        return paramType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTargetName() {
        return targetName;
    }

    public ScopeTypeEnum getScopeDataEnum() {
        return scopeTypeEnum;
    }

    public void setFieldInjectDefList(List<ParamInjectDef> fieldInjectDefList) {
        if (fieldInjectDefList != null) {
            this.fieldInjectDefList = Collections.unmodifiableList(fieldInjectDefList);
        }
    }

    public List<ParamInjectDef> getFieldInjectDefList() {
        if (fieldInjectDefList == null) {
            return null;
        }
        return fieldInjectDefList;
    }

}
