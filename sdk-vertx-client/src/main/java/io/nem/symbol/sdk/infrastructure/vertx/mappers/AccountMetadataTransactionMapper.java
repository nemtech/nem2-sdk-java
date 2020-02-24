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

package io.nem.symbol.sdk.infrastructure.vertx.mappers;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.AccountMetadataTransactionDTO;
import java.math.BigInteger;

/**
 * Account metadata transaction mapper.
 */
class AccountMetadataTransactionMapper extends
    AbstractTransactionMapper<AccountMetadataTransactionDTO, AccountMetadataTransaction> {

    public AccountMetadataTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.ACCOUNT_METADATA,
            AccountMetadataTransactionDTO.class);
    }

    @Override
    protected AccountMetadataTransactionFactory createFactory(NetworkType networkType,
        AccountMetadataTransactionDTO transaction) {
        PublicAccount targetAccount = PublicAccount
            .createFromPublicKey(transaction.getTargetPublicKey(), networkType);
        Integer valueSizeDelta = transaction.getValueSizeDelta();
        BigInteger scopedMetaDataKey = new BigInteger(transaction.getScopedMetadataKey(), 16);
        String value = ConvertUtils.fromHexToString(transaction.getValue());
        AccountMetadataTransactionFactory factory = AccountMetadataTransactionFactory.create(
            networkType,
            targetAccount,
            scopedMetaDataKey,
            value);
        factory.valueSizeDelta(valueSizeDelta);
        Integer valueSize = transaction.getValueSize();
        if (valueSize != null) {
            factory.valueSize(valueSize);
        }
        return factory;
    }

    @Override
    protected void copyToDto(AccountMetadataTransaction transaction,
        AccountMetadataTransactionDTO dto) {

        dto.setTargetPublicKey(transaction.getTargetAccount().getPublicKey().toHex());
        dto.setValueSizeDelta(transaction.getValueSizeDelta());
        dto.setScopedMetadataKey(transaction.getScopedMetadataKey().toString(16));
        dto.setValue(ConvertUtils.fromStringToHex(transaction.getValue()));
        dto.setValueSize(transaction.getValueSize());
    }
}
