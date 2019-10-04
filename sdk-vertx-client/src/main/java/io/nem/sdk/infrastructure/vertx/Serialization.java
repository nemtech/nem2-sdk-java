package io.nem.sdk.infrastructure.vertx;

import io.nem.sdk.model.transaction.Transaction;
import io.vertx.core.json.JsonObject;

public class Serialization {

    public static JsonObject toJsonObject(Object object) {
        JsonObject jsonObject;
        boolean isTransaction = Transaction.class.isAssignableFrom(object.getClass());
        if (isTransaction)
            jsonObject = (new TransactionSerialization()).toJSONObject(object);
        else
            throw new IllegalArgumentException("Cannot deserialize to json object: " + object.getClass().getName());

        return jsonObject;
    }
}
