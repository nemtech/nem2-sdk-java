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
import java.math.BigInteger;

/**
 * Voting key link transaction.
 */
public class VotingKeyLinkTransaction extends Transaction {

    /**
     * The voting key.
     */
    private final VotingKey linkedPublicKey;

    /**
     * Start finalization point.
     */
    private final BigInteger startPoint;

    /**
     * End finalization point.
     */
    private final BigInteger endPoint;

    /**
     * The link action.
     */
    private final LinkAction linkAction;

    /**
     * Constructor
     *
     * @param factory the factory.
     */
    VotingKeyLinkTransaction(VotingKeyLinkTransactionFactory factory) {
        super(factory);
        this.linkedPublicKey = factory.getLinkedPublicKey();
        this.startPoint  = factory.getStartPoint();
        this.endPoint = factory.getEndPoint();
        this.linkAction = factory.getLinkAction();

    }

    public VotingKey getLinkedPublicKey() {
        return linkedPublicKey;
    }

    public LinkAction getLinkAction() {
        return linkAction;
    }

    public BigInteger getStartPoint() {
        return startPoint;
    }

    public BigInteger getEndPoint() {
        return endPoint;
    }
}
