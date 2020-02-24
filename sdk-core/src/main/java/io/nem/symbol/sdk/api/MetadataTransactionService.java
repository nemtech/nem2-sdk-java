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

package io.nem.symbol.sdk.api;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;

/**
 * Metadata transaction service.
 *
 * @author Ravi Shanker
 */
public interface MetadataTransactionService {

    /**
     * Create an Account Metadata Transaction that knows how to set or update a value.
     *
     * @param targetPublicAccount the target public account
     * @param key the key of the metadata
     * @param value the value of the metadata.
     * @param senderPublicKey the sender (signer) public account.
     * @return an observable of AccountMetadataTransactionFactory of a transaction that can be
     * announced.
     */
    Observable<AccountMetadataTransactionFactory> createAccountMetadataTransactionFactory(
        PublicAccount targetPublicAccount,
        BigInteger key,
        String value,
        PublicKey senderPublicKey);

    /**
     * Create an Mosaic Metadata Transaction that knows how to set or update a value.
     *
     * @param targetPublicAccount the target public account
     * @param key the key of the metadata
     * @param value the value of the metadata.
     * @param senderPublicKey the sender (signer) public account.
     * @param targetId the mosaic id of the attached metadata.
     * @return an observable of AccountMetadataTransactionFactory of a transaction that can be
     * announced.
     */
    Observable<MosaicMetadataTransactionFactory> createMosaicMetadataTransactionFactory(
        PublicAccount targetPublicAccount,
        BigInteger key,
        String value,
        PublicKey senderPublicKey,
        UnresolvedMosaicId targetId);

    /**
     * Create an Namespace Metadata Transaction that knows how to set or update a value.
     *
     * @param targetPublicAccount the target public account
     * @param key the key of the metadata
     * @param value the value of the metadata.
     * @param senderPublicKey the sender (signer) public account.
     * @param targetId the namespace id of the attached metadata.
     * @return an observable of AccountMetadataTransactionFactory of a transaction that can be
     * announced.
     */
    Observable<NamespaceMetadataTransactionFactory> createNamespaceMetadataTransactionFactory(
        PublicAccount targetPublicAccount,
        BigInteger key,
        String value,
        PublicKey senderPublicKey,
        NamespaceId targetId);
}
