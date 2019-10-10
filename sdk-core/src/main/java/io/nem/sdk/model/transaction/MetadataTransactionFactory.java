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

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/**
 * Abstract factory of {@link MetadataTransaction}
 */
public abstract class MetadataTransactionFactory<T extends MetadataTransaction> extends
    TransactionFactory<T> {

    /**
     * Metadata target public key.
     */
    private final PublicAccount targetAccount;

    /**
     * Metadata key scoped to source, target and type.
     */
    private final BigInteger scopedMetadataKey;
    /**
     * Change in value size in bytes. Defaulted to the size of the encoded value.
     */
    private int valueSizeDelta;

    /**
     * When there is an existing value, the new value is calculated as xor(previous-value, value).
     * It can be a plain text.
     */
    private final String value;

    MetadataTransactionFactory(
        TransactionType transactionType,
        NetworkType networkType,
        PublicAccount targetAccount,
        BigInteger scopedMetadataKey,
        String value) {
        super(transactionType, networkType);

        Validate.notNull(targetAccount, "TargetAccount must not be null");
        Validate.notNull(scopedMetadataKey, "ScopedMetadataKey must not be null");
        Validate.notNull(value, "Value must not be null");

        this.targetAccount = targetAccount;
        this.scopedMetadataKey = scopedMetadataKey;
        this.value = value;
        int defaultSize = MetadataTransaction.toByteArray(value).length;
        this.valueSizeDelta = defaultSize;
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

    /**
     * Use this method when you want to update/modify a metadata. The value size delta needs to be
     * provided in order to update the existing metadata correctly.
     *
     * @param valueSizeDelta the new value size delta
     * @return this factory.
     */
    public MetadataTransactionFactory<T> valueSizeDelta(int valueSizeDelta) {
        this.valueSizeDelta = valueSizeDelta;
        return this;
    }


    public String getValue() {
        return value;
    }
}
