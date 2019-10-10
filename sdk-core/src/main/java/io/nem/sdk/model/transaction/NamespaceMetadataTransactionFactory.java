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
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link NamespaceMetadataTransaction}
 */
public class NamespaceMetadataTransactionFactory extends
    MetadataTransactionFactory<NamespaceMetadataTransaction> {

    /**
     * Metadata target Namespace id.
     */
    private final NamespaceId targetNamespaceId;

    private NamespaceMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetAccount,
        NamespaceId targetNamespaceId,
        BigInteger scopedMetadataKey,
        String value) {
        super(TransactionType.NAMESPACE_METADATA_TRANSACTION, networkType, targetAccount,
            scopedMetadataKey, value);
        Validate.notNull(targetNamespaceId, "TargetNamespaceId must not be null");
        this.targetNamespaceId = targetNamespaceId;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param targetAccount Target account.
     * @param targetNamespaceId Target namespace id.
     * @param scopedMetadataKey Scoped metadata key.
     * @param value Value.
     * @return Namespace metadata transaction.
     */
    public static NamespaceMetadataTransactionFactory create(NetworkType networkType,
        PublicAccount targetAccount, NamespaceId targetNamespaceId, BigInteger scopedMetadataKey, String value) {
        return new NamespaceMetadataTransactionFactory(networkType, targetAccount, targetNamespaceId, scopedMetadataKey, value);
    }

    public NamespaceId getTargetNamespaceId() {
        return targetNamespaceId;
    }

    @Override
    public NamespaceMetadataTransaction build() {
        return new NamespaceMetadataTransaction(this);
    }
}
