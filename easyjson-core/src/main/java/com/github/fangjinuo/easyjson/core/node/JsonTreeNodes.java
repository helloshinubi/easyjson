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

package com.github.fangjinuo.easyjson.core.node;

import com.github.fangjinuo.easyjson.core.JSON;
import com.github.fangjinuo.easyjson.core.JSONBuilderProvider;
import com.github.fangjinuo.easyjson.core.JsonTreeNode;
import com.github.fangjinuo.easyjson.core.util.type.Primitives;

import java.util.*;

public class JsonTreeNodes {
    public static JsonTreeNode fromJavaObject(Object object) {
        return fromJavaObject(object, null);
    }

    public static JsonTreeNode fromJavaObject(Object object, MappingToJsonTreeNode mapper) {
        if (object == null) {
            return JsonNullNode.INSTANCE;
        }
        if (object instanceof JsonTreeNode) {
            return (JsonTreeNode) object;
        }

        if (Primitives.isPrimitive(object.getClass())) {
            return new JsonPrimitiveNode(object);
        }

        if (object instanceof Collection) {
            JsonArrayNode arrayNode = new JsonArrayNode();
            Collection c = (Collection) object;
            for (Object e : c) {
                JsonTreeNode element = fromJavaObject(e, mapper);
                arrayNode.add(element);
            }
            return arrayNode;
        }

        if (object.getClass().isArray()) {
            Object[] array = (Object[]) object;
            JsonArrayNode arrayNode = new JsonArrayNode();
            for (int i = 0; i < array.length; i++) {
                Object e = array[i];
                JsonTreeNode element = fromJavaObject(e, mapper);
                arrayNode.add(element);
            }
            return arrayNode;
        }

        if (object instanceof Map) {
            JsonObjectNode objectNode = new JsonObjectNode();
            Map map = (Map) object;
            Set keySet = map.keySet();
            for (Object key : keySet) {
                objectNode.addProperty(key.toString(), fromJavaObject(map.get(key), mapper));
            }
            return objectNode;
        }

        JsonTreeNode node = null;
        if (mapper != null) {
            node = mapper.mapping(object);
        }
        if (node != null) {
            return node;
        }
        JSON json = JSONBuilderProvider.create().build();
        String jsonString = json.toJson(object);
        return json.fromJson(jsonString);
    }

    public static Object toJavaObject(JsonTreeNode node) {
        return toJavaObject(node, null);
    }

    public static Object toJavaObject(JsonTreeNode node, MappingToJavaObject mapping) {
        if (node == null || JsonNullNode.INSTANCE == node) {
            if (mapping != null) {
                return mapping.mappingNull(JsonNullNode.INSTANCE);
            }
            return null;
        }
        if (node.isJsonPrimitiveNode()) {
            if (mapping != null) {
                return mapping.mappingPrimitive(node.getAsJsonPrimitiveNode());
            }
            return node.getAsJsonPrimitiveNode().getValue();
        }
        if (node.isJsonArrayNode()) {
            if (mapping != null) {
                return mapping.mappingArray(node.getAsJsonArrayNode());
            }
            JsonArrayNode arrayNode = node.getAsJsonArrayNode();
            List<Object> array = new ArrayList<Object>();
            for (int i = 0; i < arrayNode.size(); i++) {
                array.add(toJavaObject(arrayNode.get(i)));
            }
            return array;
        }
        if (node.isJsonObjectNode()) {
            if (mapping != null) {
                return mapping.mappingObject(node.getAsJsonObjectNode());
            }
            JsonObjectNode objectNode = node.getAsJsonObjectNode();
            Iterator<Map.Entry<String, JsonTreeNode>> iter = objectNode.propertySet().iterator();
            Map<String, Object> map = new HashMap<String, Object>();
            while (iter.hasNext()) {
                Map.Entry<String, JsonTreeNode> entry = iter.next();
                map.put(entry.getKey(), toJavaObject(entry.getValue()));
            }
            return map;
        }
        return node;
    }

    /**
     * merge node2 to node1
     *
     * @return an new node
     */
    public static JsonTreeNode combine(JsonTreeNode node1, JsonTreeNode node2) {
        if (node1.isJsonNullNode()) {
            return node2.deepCopy();
        }
        if (node2.isJsonNullNode()) {
            return node1.deepCopy();
        }

        if (node1.isJsonPrimitiveNode()) {
            if (node2.isJsonPrimitiveNode() || node2.isJsonObjectNode()) {
                return node1.deepCopy();
            }
        }

        if (node1.isJsonArrayNode() || node2.isJsonArrayNode()) {
            JsonArrayNode arrayNode = node1.isJsonArrayNode() ? (JsonArrayNode) node1.deepCopy() : (JsonArrayNode) node2.deepCopy();
            node2 = node1.isJsonArrayNode() ? node2.deepCopy() : node1.deepCopy();
            if (!node2.isJsonArrayNode()) {
                arrayNode.add(node2);
            } else {
                arrayNode.addAll(((JsonArrayNode) node2));
            }
            return node1;
        }

        if (node1.isJsonObjectNode() && node2.isJsonObjectNode()) {
            JsonObjectNode obj1 = (JsonObjectNode) node1.deepCopy();
            JsonObjectNode obj2 = (JsonObjectNode) node2.deepCopy();
            for (Map.Entry<String, JsonTreeNode> entry : obj2.propertySet()) {
                obj1.addProperty(entry.getKey(), entry.getValue());
            }
            return obj1;
        }

        return node1.deepCopy();

    }
}
