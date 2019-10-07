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

package io.nem.sdk.infrastructure.okhttp.mappers;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceMetadataTransactionDTO;
import java.math.BigInteger;

/**
 * Namespace metadata transaction mapper
 */
class NamespaceMetadataTransactionMapper extends
    AbstractTransactionMapper<NamespaceMetadataTransactionDTO, NamespaceMetadataTransaction> {

    public NamespaceMetadataTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.NAMESPACE_METADATA_TRANSACTION,
            NamespaceMetadataTransactionDTO.class);
    }

    @Override
    protected NamespaceMetadataTransactionFactory createFactory(NetworkType networkType,
        NamespaceMetadataTransactionDTO transaction) {
        PublicAccount targetAccount = PublicAccount
            .createFromPublicKey(transaction.getTargetPublicKey(), networkType);
        Integer valueSizeDelta = transaction.getValueSizeDelta();
        BigInteger scopedMetaDataKey = new BigInteger(transaction.getScopedMetadataKey());
        Integer valueSize = transaction.getValueSize();
        String value = transaction.getValue();
        NamespaceId targetNamespace = MapperUtils.toNamespaceId(transaction.getTargetNamespaceId());
        return NamespaceMetadataTransactionFactory.create(networkType,
            targetAccount,
            targetNamespace,
            scopedMetaDataKey,
            valueSizeDelta,
            valueSize,
            value);
    }
}
