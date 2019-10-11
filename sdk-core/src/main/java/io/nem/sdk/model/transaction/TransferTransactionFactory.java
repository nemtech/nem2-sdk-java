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

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.namespace.NamespaceId;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link TransferTransaction}
 */
public class TransferTransactionFactory extends TransactionFactory<TransferTransaction> {

    private final Optional<Address> recipient;
    private final List<Mosaic> mosaics;
    private final Message message;
    private Optional<NamespaceId> namespaceId;

    private TransferTransactionFactory(
        final NetworkType networkType,
        final Optional<Address> recipient,
        final Optional<NamespaceId> namespaceId,
        final List<Mosaic> mosaics,
        final Message message) {
        super(TransactionType.TRANSFER, networkType);
        Validate.notNull(recipient, "Recipient must not be null");
        Validate.notNull(mosaics, "Mosaics must not be null");
        Validate.notNull(message, "Message must not be null");
        Validate.notNull(namespaceId, "NamespaceId must not be null");
        this.recipient = recipient;
        this.mosaics = mosaics;
        this.message = message;
        this.namespaceId = namespaceId;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param recipient Recipient address.
     * @param mosaics List of mosaics.
     * @param message Message.
     * @return Transfer transaction.
     */
    public static TransferTransactionFactory create(
        final NetworkType networkType,
        final Address recipient,
        final List<Mosaic> mosaics,
        final Message message) {
        return new TransferTransactionFactory(networkType, Optional.of(recipient), Optional.empty(), mosaics, message);
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param namespaceId Namespace id.
     * @param mosaics List of mosaics.
     * @param message Message.
     * @return Transfer transaction.
     */
    public static TransferTransactionFactory createWithNamespaceId(
        final NetworkType networkType,
        final NamespaceId namespaceId,
        final List<Mosaic> mosaics,
        final Message message) {
        return new TransferTransactionFactory(networkType, Optional.empty(), Optional.of(namespaceId), mosaics, message);
    }

    /**
     * Builder method used to change the default namespaceId.
     *
     * @param namespaceId a new namespaceId
     * @return this factory to continue building the transaction.
     */
    public TransferTransactionFactory namespaceId(NamespaceId namespaceId) {
        Validate.notNull(namespaceId, "NamespaceId must not be null");
        this.namespaceId = Optional.of(namespaceId);
        return this;
    }

    /**
     * Returns address of the recipient.
     *
     * @return recipient address
     */
    public Optional<Address> getRecipient() {
        return recipient;
    }

    /**
     * Gets namespace id alias for the address of the recipient.
     *
     * @return Namespace id.
     */
    public Optional<NamespaceId> getNamespaceId() {
        return namespaceId;
    }

    /**
     * Returns list of mosaic objects.
     *
     * @return List of {@link Mosaic}
     */
    public List<Mosaic> getMosaics() {
        return mosaics;
    }

    /**
     * Returns transaction message.
     *
     * @return Message.
     */
    public Message getMessage() {
        return message;
    }


    @Override
    public TransferTransaction build() {
        return new TransferTransaction(this);
    }
}
