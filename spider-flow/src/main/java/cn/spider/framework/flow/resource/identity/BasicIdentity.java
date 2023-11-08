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
package cn.spider.framework.flow.resource.identity;

import cn.spider.framework.flow.enums.IdentityTypeEnum;
import cn.spider.framework.flow.util.AssertUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * BasicIdentity
 *
 * @author lykan
 */
public abstract class BasicIdentity implements Identity {

    /**
     * 资源ID
     */
    private final String identityId;

    /**
     * 资源类型
     */
    private final IdentityTypeEnum identityType;

    public BasicIdentity(String identityId, IdentityTypeEnum identityType) {
        AssertUtil.notBlank(identityId);
        AssertUtil.notNull(identityType);

        this.identityId = identityId;
        this.identityType = identityType;
    }

    @Nonnull
    @Override
    public String getIdentityId() {
        return this.identityId;
    }

    @Nonnull
    @Override
    public IdentityTypeEnum getIdentityType() {
        return this.identityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        BasicIdentity that = (BasicIdentity) o;
        return identityId.equals(that.identityId) && identityType == that.identityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityId, identityType);
    }
}
