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

/**
 * Factory of {@link AccountMetadataTransaction}
 */
public class AccountMetadataTransactionFactory extends
    MetadataTransactionFactory<AccountMetadataTransaction> {

    private AccountMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetAccount,
        BigInteger scopedMetadataKey,
        int valueSizeDelta,
        int valueSize,
        String value) {
        super(TransactionType.ACCOUNT_METADATA_TRANSACTION, networkType, targetAccount,
            scopedMetadataKey, valueSizeDelta, valueSize, value);
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param targetAccount Target account.
     * @param scopedMetadataKey Scoped metadata key.
     * @param valueSizeDelta Value size delta.
     * @param valueSize Value size.
     * @param value Value.
     * @return Account metadata transaction.
     */
    public static AccountMetadataTransactionFactory create(NetworkType networkType,
        PublicAccount targetAccount, BigInteger scopedMetadataKey, int valueSizeDelta, int valueSize, String value) {
        return new AccountMetadataTransactionFactory(networkType, targetAccount, scopedMetadataKey, valueSizeDelta, valueSize, value);
    }

    @Override
    public AccountMetadataTransaction build() {
        return new AccountMetadataTransaction(this);
    }
}
