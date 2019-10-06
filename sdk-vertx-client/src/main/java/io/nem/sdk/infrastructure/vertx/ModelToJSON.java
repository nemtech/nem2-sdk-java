package io.nem.sdk.infrastructure.vertx;

import io.nem.sdk.model.transaction.Transaction;
import io.vertx.core.json.JsonObject;

/**
 * Helper class to convert model object to JSON.
 *
 * @author Ravi Shanker
 */
class ModelToJSON {

    /**
     * Static method to delegate the conversion.
     *
     * @param object
     * @return JsonObject
     */
    static JsonObject convert(Object object) {
        JsonObject jsonObject;
        boolean isTransaction = Transaction.class.isAssignableFrom(object.getClass());
        if (isTransaction)
            jsonObject = (new TransactionModelToJSON()).convert(object);
        else
            throw new IllegalArgumentException("Convert to json object not implemented for: " + object.getClass().getName());

        return jsonObject;
    }
}
