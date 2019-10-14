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

import io.nem.core.crypto.PrivateKey;
import io.nem.core.crypto.PublicKey;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.message.PersistentHarvestingDelegationMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link TransferTransaction}
 */
public class TransferTransactionFactory extends TransactionFactory<TransferTransaction> {

    private final UnresolvedAddress recipient;
    private final List<Mosaic> mosaics;
    private final Message message;

    private TransferTransactionFactory(
        final NetworkType networkType,
        final UnresolvedAddress recipient,
        final List<Mosaic> mosaics,
        final Message message) {
        super(TransactionType.TRANSFER, networkType);
        Validate.notNull(recipient, "Recipient must not be null");
        Validate.notNull(mosaics, "Mosaics must not be null");
        Validate.notNull(message, "Message must not be null");
        this.recipient = recipient;
        this.mosaics = mosaics;
        this.message = message;
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
        final UnresolvedAddress recipient,
        final List<Mosaic> mosaics,
        final Message message) {
        return new TransferTransactionFactory(networkType, recipient, mosaics, message);
    }

    /**
     * Creates a TransferTransactionFactory with special message payload for persistent harvesting
     * delegation unlocking
     *
     * @param networkType The network type.
     * @param remoteProxyPrivateKey the remote’s account proxy private key.
     * @param senderPrivateKey The sender's private key
     * @param harvesterPublicKey The harvester public key
     * @return {@link TransferTransactionFactory}
     */
    public static TransferTransactionFactory createPersistentDelegationRequestTransaction(
        NetworkType networkType,
        PrivateKey remoteProxyPrivateKey,
        PrivateKey senderPrivateKey,
        PublicKey harvesterPublicKey) {
        PersistentHarvestingDelegationMessage message = PersistentHarvestingDelegationMessage
            .create(remoteProxyPrivateKey, senderPrivateKey, harvesterPublicKey, networkType);
        return new TransferTransactionFactory(networkType,
            Address.createFromPublicKey(harvesterPublicKey.toHex(), networkType),
            Collections.emptyList(), message);
    }

    /**
     * Returns address of the recipient.
     *
     * @return recipient address
     */
    public UnresolvedAddress getRecipient() {
        return recipient;
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
