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

import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.List;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link MultisigAccountModificationTransaction}
 */
public class MultisigAccountModificationTransactionFactory extends
    TransactionFactory<MultisigAccountModificationTransaction> {

    private final byte minApprovalDelta;
    private final byte minRemovalDelta;
    private final List<UnresolvedAddress> addressAdditions;
    private final List<UnresolvedAddress> addressDeletions;

    private MultisigAccountModificationTransactionFactory(
        NetworkType networkType,
        byte minApprovalDelta,
        byte minRemovalDelta,
        List<UnresolvedAddress> addressAdditions,
        List<UnresolvedAddress> addressDeletions) {
        super(TransactionType.MULTISIG_ACCOUNT_MODIFICATION, networkType);
        Validate.notNull(addressAdditions, "AddressAdditions must not be null");
        Validate.notNull(addressDeletions, "AddressDeletions must not be null");
        this.minApprovalDelta = minApprovalDelta;
        this.minRemovalDelta = minRemovalDelta;
        this.addressAdditions = addressAdditions;
        this.addressDeletions = addressDeletions;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType Network type.
     * @param minApprovalDelta Minimum approval delta.
     * @param minRemovalDelta Minimum removal delta.
     * @param addressAdditions List of public accounts that are going to be added to the multisig
     * account.
     * @param addressDeletions List of public accounts that are going to be removed from the
     * multisig account.
     * @return Multisig account modification transaction.
     */
    public static MultisigAccountModificationTransactionFactory create(
        NetworkType networkType,
        byte minApprovalDelta,
        byte minRemovalDelta,
        List<UnresolvedAddress> addressAdditions,
        List<UnresolvedAddress> addressDeletions) {
        return new MultisigAccountModificationTransactionFactory(networkType, minApprovalDelta,
            minRemovalDelta,
            addressAdditions, addressDeletions);
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
     * @return List of addresses or aliasees that are going to be added to the multisig account.
     */
    public List<UnresolvedAddress> getAddressAdditions() {
        return addressAdditions;
    }

    /**
     * @return List of addresses or aliasees that are going to be removed from the multisig account.
     */
    public List<UnresolvedAddress> getAddressDeletions() {
        return addressDeletions;
    }


    @Override
    public MultisigAccountModificationTransaction build() {
        return new MultisigAccountModificationTransaction(this);
    }
}
