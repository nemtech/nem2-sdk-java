/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import java.math.BigInteger;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fernando on 06/08/19.
 *
 * @author Fernando Boucquez
 */
public class JsonHelperGson implements JsonHelper {

    private final Gson objectMapper;

    public JsonHelperGson(Gson objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public <T> T parse(final String string, final Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(string)) {
                return null;
            }
            return objectMapper.fromJson(string, clazz);
        } catch (Exception e) {
            throw handleException(e);
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


    private static IllegalArgumentException handleException(Exception e) {
        return new IllegalArgumentException(e.getMessage(), e);
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

    @Override
    public JsonObject toJsonObject(Transaction transaction) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", transaction.getType().getValue());
        jsonObject.addProperty("networkType", transaction.getNetworkType().getValue());
        jsonObject.addProperty("version", transaction.getVersion());
        jsonObject.addProperty("maxFee", transaction.getMaxFee());
        jsonObject.addProperty("deadline", transaction.getDeadline().getInstant());
        jsonObject.addProperty("signature", (transaction.getSignature().isPresent() ? transaction.getSignature().get() : ""));

        if (transaction.getSigner().isPresent()) jsonObject.addProperty("signerPublicKey", transaction.getSigner().get().getPublicKey().toString());



        return jsonObject;
    }

    @Override
    public String toJSON(Transaction transaction) {
        return toJsonObject(transaction).toString();
    }

    @Override
    public String toJSONPretty(Transaction transaction) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(toJsonObject(transaction));
    }

    @Override
    public String toJSONPretty(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }
}
