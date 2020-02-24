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

package io.nem.symbol.sdk.infrastructure.okhttp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.LinkedTreeMap;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.JSON;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.JSON.ByteArrayAdapter;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.JSON.DateTypeAdapter;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.JSON.SqlDateTypeAdapter;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by fernando on 06/08/19.
 *
 * @author Fernando Boucquez
 */
public class JsonHelperGson implements JsonHelper {

    private final Gson objectMapper;
    private final Gson prettyObjectMapper;

    public JsonHelperGson() {
        this(JsonHelperGson.creatGson(false), JsonHelperGson.creatGson(true));
    }

    public JsonHelperGson(Gson objectMapper) {
        this(objectMapper, JsonHelperGson.creatGson(true));
    }

    public JsonHelperGson(Gson objectMapper, Gson prettyObjectMapper) {
        this.objectMapper = objectMapper;
        this.prettyObjectMapper = prettyObjectMapper;
    }


    public static final Gson creatGson(boolean pretty) {
        JSON json = new JSON();
        DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();
        SqlDateTypeAdapter sqlDateTypeAdapter = new SqlDateTypeAdapter();
        ByteArrayAdapter byteArrayAdapter = json.new ByteArrayAdapter();
        GsonBuilder builder = JSON.createGson().registerTypeHierarchyAdapter(
            Collection.class, new CollectionAdapter())
            .registerTypeAdapter(LinkedTreeMap.class, new SortedJsonSerializer())
            .registerTypeAdapter(BigInteger.class, new BigIntegerJsonSerializer())
            .registerTypeAdapter(Double.class, new DoubleJsonSerializer())
            .registerTypeAdapter(Date.class, dateTypeAdapter)
            .registerTypeAdapter(java.sql.Date.class, sqlDateTypeAdapter)
            .registerTypeAdapter(byte[].class, byteArrayAdapter);
        if (pretty) {
            builder.setPrettyPrinting();
        }
        return builder.create();
    }

    @Override
    public <T> T parse(final String string, final Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(string)) {
                return null;
            }
            return objectMapper.fromJson(string, clazz);
        } catch (Exception e) {
            throw handleException(e, "Json payload: " + string);
        }
    }

    @Override
    public Object parse(String string) {
        return parse(string, JsonObject.class);
    }

    @Override
    public String print(final Object object) {
        try {
            if (object == null) {
                return null;
            }
            return objectMapper.toJson(object);
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
            return prettyObjectMapper.toJson(object);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
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
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        if (child == null || child.isJsonNull()) {
            return null;
        }
        if (child.isJsonObject()) {
            throw new IllegalArgumentException("Cannot extract a Integer from an json object");
        }
        return (int) child.getAsDouble();
    }

    @Override
    public String getString(Object object, String... path) {
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        if (child == null || child.isJsonNull()) {
            return null;
        }
        if (child.isJsonObject()) {
            throw new IllegalArgumentException("Cannot extract a String from an json object");
        }
        return child.getAsString();
    }

    @Override
    public Long getLong(Object object, String... path) {
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        if (child == null || child.isJsonNull()) {
            return null;
        }
        if (child.isJsonObject()) {
            throw new IllegalArgumentException("Cannot extract a Long from an json object");
        }
        return (long) child.getAsDouble();
    }

    @Override
    @SuppressWarnings("squid:S2447")
    public Boolean getBoolean(Object object, String... path) {
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        if (child == null || child.isJsonNull()) {
            return null;
        }
        if (child.isJsonObject()) {
            throw new IllegalArgumentException("Cannot extract a Boolean from an json object");
        }
        return child.getAsBoolean();
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
        JsonElement child = getNode(convert(object, JsonObject.class), path);
        return child != null && !child.isJsonNull();
    }


    private JsonElement getNode(final JsonObject parent, final String... path) {
        JsonElement child = parent;
        if (child == null) {
            return null;
        }
        if (path.length == 0) {
            return child;
        }
        if (!child.isJsonObject()) {
            return null;
        }
        int index = 0;
        for (String attribute : path) {
            child = ((JsonObject) child).get(attribute);
            if (child == null) {
                return null;
            }
            index++;
            if (index < path.length && !child.isJsonObject()) {
                return null;
            }
        }
        return child;
    }

    private static class BigIntegerJsonSerializer implements JsonSerializer<BigInteger> {

        @Override
        public JsonElement serialize(BigInteger src, Type typeOfSrc,
            JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    private static class DoubleJsonSerializer implements JsonSerializer<Double> {

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc,
            JsonSerializationContext context) {
            return new JsonPrimitive(src.longValue());
        }
    }


    private static class SortedJsonSerializer implements JsonSerializer<LinkedTreeMap> {

        @Override
        public JsonElement serialize(LinkedTreeMap foo, Type type,
            JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            TreeSet sorted = new TreeSet(foo.keySet());
            for (Object key : sorted) {
                object.add((String) key, context.serialize(foo.get(key)));
            }
            return object;
        }
    }
}
