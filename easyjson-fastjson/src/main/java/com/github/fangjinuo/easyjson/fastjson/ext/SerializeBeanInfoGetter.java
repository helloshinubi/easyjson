/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the LGPL, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.gnu.org/licenses/lgpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fangjinuo.easyjson.fastjson.ext;

import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializeBeanInfo;
import com.alibaba.fastjson.util.FieldInfo;
import com.github.fangjinuo.easyjson.core.util.Reflects;

import java.lang.reflect.Field;

public class SerializeBeanInfoGetter {
    private SerializeBeanInfo beanInfo;

    public SerializeBeanInfoGetter(SerializeBeanInfo beanInfo) {
        this.beanInfo = beanInfo;
    }

    public Class getBeanType() {
        Field field = Reflects.getDeclaredField(beanInfo.getClass(), "beanType");
        if (field != null) {
            field.setAccessible(true);
            try {
                return (Class) field.get(beanInfo);
            } catch (IllegalAccessException e) {
                // ignore it
            }
        }
        return null;
    }

    public String getTypeName() {
        return Reflects.getDeclaredFieldValue(beanInfo, "typeName");
    }

    public String getTypeKey() {
        return Reflects.getDeclaredFieldValue(beanInfo, "typeKey");
    }

    public JSONType getJsonType() {
        return Reflects.getDeclaredFieldValue(beanInfo, "jsonType");
    }

    public FieldInfo[] getFields() {
        return Reflects.getDeclaredFieldValue(beanInfo, "fields");
    }

    public FieldInfo[] getSortedFields() {
        return Reflects.getDeclaredFieldValue(beanInfo, "sortedFields");
    }

    public Integer getFeatures() {
        return Reflects.getDeclaredFieldValue(beanInfo, "features");
    }

}