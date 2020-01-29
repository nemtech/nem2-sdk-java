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
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link MultisigAccountModificationTransaction}
 */
public class MultisigAccountModificationTransactionFactory extends
    TransactionFactory<MultisigAccountModificationTransaction> {

    private final byte minApprovalDelta;
    private final byte minRemovalDelta;
    private final List<PublicAccount> publicKeyAdditions;
    private final List<PublicAccount> publicKeyDeletions;

    private MultisigAccountModificationTransactionFactory(
        NetworkType networkType,
        byte minApprovalDelta,
        byte minRemovalDelta,
        List<PublicAccount> publicKeyAdditions,
        List<PublicAccount> publicKeyDeletions) {
        super(TransactionType.MULTISIG_ACCOUNT_MODIFICATION, networkType);
        Validate.notNull(publicKeyAdditions, "PublicKeyAdditions must not be null");
        Validate.notNull(publicKeyDeletions, "PublicKeyDeletions must not be null");
        this.minApprovalDelta = minApprovalDelta;
        this.minRemovalDelta = minRemovalDelta;
        this.publicKeyAdditions = publicKeyAdditions;
        this.publicKeyDeletions = publicKeyDeletions;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param minApprovalDelta Minimum approval delta.
     * @param minRemovalDelta Minimum removal delta.
     * @param publicKeyAdditions List of public accounts that are going to be added to the multisig
     * account.
     * @param publicKeyDeletions List of public accounts that are going to be removed from the
     * multisig account.
     * @return Multisig account modification transaction.
     */
    public static MultisigAccountModificationTransactionFactory create(
        NetworkType networkType,
        byte minApprovalDelta,
        byte minRemovalDelta,
        List<PublicAccount> publicKeyAdditions,
        List<PublicAccount> publicKeyDeletions) {
        return new MultisigAccountModificationTransactionFactory(networkType, minApprovalDelta,
            minRemovalDelta,
            publicKeyAdditions, publicKeyDeletions);
    }

    /**
     * Return number of signatures needed to approve a transaction. If we are modifying and existing
     * multi-signature account this indicates the relative change of the minimum cosignatories.
     *
     * @return byte
     */
    public byte getMinApprovalDelta() {
        return minApprovalDelta;
    }

    /**
     * Return number of signatures needed to remove a cosignatory. If we are modifying and existing
     * multi-signature account this indicates the relative change of the minimum cosignatories.
     *
     * @return byte
     */
    public byte getMinRemovalDelta() {
        return minRemovalDelta;
    }

    /**
     * @return List of public accounts that are going to be added to the multisig account.
     */
    public List<PublicAccount> getPublicKeyAdditions() {
        return publicKeyAdditions;
    }

    /**
     * @return List of public accounts that are going to be removed from the multisig account.
     */
    public List<PublicAccount> getPublicKeyDeletions() {
        return publicKeyDeletions;
    }


    @Override
    public MultisigAccountModificationTransaction build() {
        return new MultisigAccountModificationTransaction(this);
    }
}
