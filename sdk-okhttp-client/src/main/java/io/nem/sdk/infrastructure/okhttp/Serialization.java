package io.nem.sdk.infrastructure.okhttp;

import com.google.gson.JsonObject;
import io.nem.sdk.model.transaction.Transaction;

;

public class Serialization {

    public static JsonObject toJsonObject(Object object) {
        JsonObject jsonObject;
        boolean isTransaction = Transaction.class.isAssignableFrom(object.getClass());
        // to be uncommented when TransactionSerialization is checked in
        /*if (isTransaction)
            jsonObject = (new TransactionSerialization()).toJSONObject(object);
        else*/
            throw new IllegalArgumentException("Cannot deserialize to json object: " + object.getClass().getName());

        //return jsonObject;
    }
}
