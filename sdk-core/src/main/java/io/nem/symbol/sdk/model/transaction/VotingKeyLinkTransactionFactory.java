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

import io.nem.symbol.core.crypto.VotingKey;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.apache.commons.lang3.Validate;

/**
 * Vrf key link transaction factory.
 */
public class VotingKeyLinkTransactionFactory extends TransactionFactory<VotingKeyLinkTransaction> {

    /**
     * The voting key.
     */
    private final VotingKey linkedPublicKey;

    /**
     * The link action.
     */
    private final LinkAction linkAction;

    /**
     * The factory constructor for {@link VotingKeyLinkTransactionFactory}
     *
     * @param networkType the network type of this transaction.
     * @param linkedPublicKey the voting key.
     * @param linkAction the link action.
     */
    private VotingKeyLinkTransactionFactory(final NetworkType networkType, final VotingKey linkedPublicKey,
        final LinkAction linkAction) {
        super(TransactionType.VOTING_KEY_LINK, networkType);
        Validate.notNull(linkedPublicKey, "linkedPublicKey must not be null");
        Validate.notNull(linkAction, "linkAction must not be null");
        this.linkedPublicKey = linkedPublicKey;
        this.linkAction = linkAction;
    }
    /**
     * The factory constructor for {@link VotingKeyLinkTransactionFactory}
     *
     * @param networkType the network type of this transaction.
     * @param linkedPublicKey the voting key.
     * @param linkAction the link action.
     */
    public static VotingKeyLinkTransactionFactory create(final NetworkType networkType, final VotingKey linkedPublicKey,
        final LinkAction linkAction) {
        return new VotingKeyLinkTransactionFactory(networkType, linkedPublicKey, linkAction);
    }


    @Override
    public VotingKeyLinkTransaction build() {
        return new VotingKeyLinkTransaction(this);
    }

    public VotingKey getLinkedPublicKey() {
        return linkedPublicKey;
    }

    public LinkAction getLinkAction() {
        return linkAction;
    }
}
