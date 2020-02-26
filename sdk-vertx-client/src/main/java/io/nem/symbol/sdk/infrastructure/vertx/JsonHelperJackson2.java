/*
 * Copyright 2020 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.symbol.sdk.infrastructure.vertx;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by fernando on 03/08/19.
 *
 * @author Fernando Boucquez
 */
public class JsonHelperJackson2 implements JsonHelper {

    private final ObjectMapper objectMapper;

    public JsonHelperJackson2(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonHelperJackson2() {
        this(configureMapper(new ObjectMapper()));
    }


    @SuppressWarnings("squid:CallToDeprecatedMethod")
    public static ObjectMapper configureMapper(ObjectMapper objectMapper) {
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS,
            false); //I cannot annotate the generated classes like the alternative recommended by jackson
        objectMapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigInteger.class, new BigIntegerSerializer());
        objectMapper.registerModule(module);
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        objectMapper.registerModule(new Jdk8Module());
        return objectMapper;
    }

    @Override
    public Object parse(String string) {
        return parse(string, ObjectNode.class);
    }

    @Override
    public <T> T parse(final String string, final Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(string)) {
                return null;
            }
            return objectMapper.readValue(string, clazz);
        } catch (Exception e) {
            throw handleException(e, "Json payload: " + string);
        }
    }

    @Override
    public String print(final Object object) {
        try {
            if (object == null) {
                return null;
            }
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public String prettyPrint(Object object) {
        try {
            if (object == null) {
                return null;
            }

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            throw handleException(e, null);
        }
    }


    private static IllegalArgumentException handleException(Exception e, String extraMessage) {
        String message = ExceptionUtils.getMessage(e);
        if (StringUtils.isNotBlank(extraMessage)) {
            message += ". " + extraMessage;
        }
        return new IllegalArgumentException(message, e);
    }

    @Override
    public <T> T convert(Object object, Class<T> instanceClass) {
        if (object == null) {
            return null;
        }
        if (instanceClass.isInstance(object)) {
            return (T) object;
        }
        return parse(print(object), instanceClass);
    }

    @Override
    public Integer getInteger(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        if (child == null || child.isNull()) {
            return null;
        }
        if (child.isObject()) {
            throw new IllegalArgumentException("Cannot extract an Integer from an json object");
        }
        return child.asInt();
    }

    @Override
    public String getString(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        if (child == null || child.isNull()) {
            return null;
        }
        if (child.isObject()) {
            throw new IllegalArgumentException("Cannot extract a String from an json object");
        }
        return child.asText();
    }

    @Override
    public Long getLong(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        if (child == null || child.isNull()) {
            return null;
        }
        if (child.isObject()) {
            throw new IllegalArgumentException("Cannot extract a Long from an json object");
        }
        return child.asLong();
    }

    @Override
    @SuppressWarnings("squid:S2447")
    public Boolean getBoolean(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        if (child == null || child.isNull()) {
            return null;
        }
        if (child.isObject()) {
            throw new IllegalArgumentException("Cannot extract a Boolean from an json object");
        }
        return child.booleanValue();
    }

    @Override
    public BigInteger getBigInteger(Object object, String... path) {
        String string = getString(object, path);
        if (string == null) {
            return null;
        }
        return new BigInteger(string);
    }

    @Override
    public boolean contains(Object object, String... path) {
        JsonNode child = getNode(convert(object, JsonNode.class), path);
        return child != null && !child.isNull();
    }

    private JsonNode getNode(final JsonNode parent, final String... path) {
        JsonNode child = parent;
        if (child == null) {
            return null;
        }
        if (path.length == 0) {
            return child;
        }
        if (!child.isObject()) {
            return null;
        }
        int index = 0;
        for (String attribute : path) {
            child = child.get(attribute);
            if (child == null) {
                return null;
            }
            index++;
            if (index < path.length && !child.isObject()) {
                return null;
            }
        }
        return child;
    }

    /**
     *
     */
    public static class BigIntegerSerializer extends StdSerializer<BigInteger> {

        public BigIntegerSerializer() {
            super(BigInteger.class);
        }


        @Override
        public void serialize(BigInteger value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.toString());
            }
        }
    }

}
