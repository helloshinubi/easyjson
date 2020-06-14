/*
 * Copyright 2020 the original author or authors.
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

package com.alibaba.fastjson.easyjson;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jn.easyjson.core.codec.dialect.BeanPropertyAnnotatedCodecConfigurationParser;
import com.jn.easyjson.core.codec.dialect.BeanPropertyIdGenerator;
import com.jn.easyjson.core.codec.dialect.PropertyCodecConfiguration;
import com.jn.langx.util.reflect.Reflects;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class JsonFieldParser implements BeanPropertyAnnotatedCodecConfigurationParser {
    private BeanPropertyIdGenerator idGenerator = new BeanPropertyIdGenerator();
    @Override
    public PropertyCodecConfiguration parse(AnnotatedElement annotatedElement) {
        if (annotatedElement instanceof Field || annotatedElement instanceof Method) {
            Member member = (Member) annotatedElement;
            Class beanClass = member.getDeclaringClass();

            JSONField jsonField = Reflects.getAnnotation(beanClass, JSONField.class);
            if (jsonField == null) {
                return null;
            }

            PropertyCodecConfiguration configuration = new PropertyCodecConfiguration();
            configuration.setClazz(beanClass);

            String propertyName = null;

            configuration.setId(idGenerator.withBeanClass(beanClass).withPropertyName(propertyName).get());

            // serializeFeatures
            SerializerFeature[] serializeFeatures = jsonField.serialzeFeatures();
            configuration.set("serializeFeatures", serializeFeatures);

            // deserializeFeatures
            Feature[] parseFeatures = jsonField.parseFeatures();
            configuration.set("deserializeFeatures", parseFeatures);

            return configuration;
        }
        return null;
    }
}