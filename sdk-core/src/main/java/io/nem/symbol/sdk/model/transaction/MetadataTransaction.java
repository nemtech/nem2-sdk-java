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

package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import java.math.BigInteger;

/**
 * Abstract transaction for all the metadata transactions.
 */
public abstract class MetadataTransaction extends Transaction {

    /**
     * Metadata target public key.
     */
    private final UnresolvedAddress targetAddress;

    /**
     * Metadata key scoped to source, target and type.
     */
    private final BigInteger scopedMetadataKey;
    /**
     * Change in value size in bytes.
     */
    private final int valueSizeDelta;

    /**
     * The value size.
     */
    private final int valueSize;


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
        this.targetAddress = factory.getTargetAddress();
        this.scopedMetadataKey = factory.getScopedMetadataKey();
        this.valueSizeDelta = factory.getValueSizeDelta();
        this.value = factory.getValue();
        this.valueSize = factory.getValueSize();
    }


    public UnresolvedAddress getTargetAddress() {
        return targetAddress;
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

    public int getValueSize() {
        return valueSize;
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
