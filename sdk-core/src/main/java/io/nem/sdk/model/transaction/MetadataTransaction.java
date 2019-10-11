/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.model.transaction;

import io.nem.core.utils.StringEncoder;
import io.nem.sdk.model.account.PublicAccount;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Abstract transaction for all the metadata transactions.
 */
public abstract class MetadataTransaction extends Transaction {

    /**
     * Metadata target public key.
     */
    private final PublicAccount targetAccount;

    /**
     * Metadata key scoped to source, target and type.
     */
    private final BigInteger scopedMetadataKey;
    /**
     * Change in value size in bytes.
     */
    private final int valueSizeDelta;


    /**
     * When there is an existing value, the new value is calculated as xor(previous-value, value).
     * The value is an hex string as it comes from the rest objects. Value is converted to byte
     * array when serialized to Catbuffer.
     */
    private final String value;

    /**
     * Constructor
     *
     * @param factory the factory with the configured data.
     */
    MetadataTransaction(MetadataTransactionFactory<?> factory) {
        super(factory);
        this.targetAccount = factory.getTargetAccount();
        this.scopedMetadataKey = factory.getScopedMetadataKey();
        this.valueSizeDelta = factory.getValueSizeDelta();
        this.value = factory.getValue();
    }


    public PublicAccount getTargetAccount() {
        return targetAccount;
    }

    public BigInteger getScopedMetadataKey() {
        return scopedMetadataKey;
    }

    public int getValueSizeDelta() {
        return valueSizeDelta;
    }

    public String getValue() {
        return value;
    }

    /**
     * Gets value buffer
     *
     * @return Value buffer.
     */
    protected ByteBuffer getValueBuffer() {
        return ByteBuffer.wrap(toByteArray(value));
    }

    /**
     * Converts a metadata value to the byte array representation.
     *
     * @param value the plain text
     * @return the array representation.
     */
    public static byte[] toByteArray(String value) {
        return StringEncoder.getBytes(value);
    }
}
