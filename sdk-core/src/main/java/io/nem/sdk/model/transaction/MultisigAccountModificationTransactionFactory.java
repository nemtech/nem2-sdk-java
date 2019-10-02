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
    private final List<MultisigCosignatoryModification> modifications;


    @SuppressWarnings("squid:S00107")
    public MultisigAccountModificationTransactionFactory(
        NetworkType networkType,
        byte minApprovalDelta,
        byte minRemovalDelta,
        List<MultisigCosignatoryModification> modifications) {
        super(TransactionType.MODIFY_MULTISIG_ACCOUNT, networkType);
        Validate.notNull(modifications, "Modifications must not be null");
        this.minApprovalDelta = minApprovalDelta;
        this.minRemovalDelta = minRemovalDelta;
        this.modifications = modifications;
    }

    public static MultisigAccountModificationTransactionFactory create(NetworkType networkType,
        int minApprovalDelta, int minRemovalDelta, List<MultisigCosignatoryModification> modifications) {
        return new MultisigAccountModificationTransactionFactory(networkType, (byte) minApprovalDelta, (byte) minRemovalDelta, modifications);
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
     * The List of cosigner accounts added or removed from the multi-signature account.
     *
     * @return {@link List} of { @ link MultisigCosignatoryModification }
     */
    public List<MultisigCosignatoryModification> getModifications() {
        return modifications;
    }

    @Override
    public MultisigAccountModificationTransaction build() {
        return new MultisigAccountModificationTransaction(this);
    }
}
