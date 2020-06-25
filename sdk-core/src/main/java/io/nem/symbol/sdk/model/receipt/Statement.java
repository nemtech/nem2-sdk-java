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

package io.nem.symbol.sdk.model.receipt;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Statement {

    private final List<TransactionStatement> transactionStatements;
    private final List<AddressResolutionStatement> addressResolutionStatements;
    private final List<MosaicResolutionStatement> mosaicResolutionStatement;

    /**
     * Constructor
     *
     * @param transactionStatements Array of transaction statements.
     * @param addressResolutionStatements Array of address resolution statements.
     * @param mosaicResolutionStatement Array of mosaic resolution statements.
     */
    public Statement(
        List<TransactionStatement> transactionStatements,
        List<AddressResolutionStatement> addressResolutionStatements,
        List<MosaicResolutionStatement> mosaicResolutionStatement) {
        this.addressResolutionStatements = addressResolutionStatements;
        this.mosaicResolutionStatement = mosaicResolutionStatement;
        this.transactionStatements = transactionStatements;
    }

    /**
     * Returns transaction statements
     *
     * @return transaction statements
     */
    public List<TransactionStatement> getTransactionStatements() {
        return this.transactionStatements;
    }

    /**
     * Returns address resolution statements.
     *
     * @return address resolution statements.
     */
    public List<AddressResolutionStatement> getAddressResolutionStatements() {
        return this.addressResolutionStatements;
    }

    /**
     * Returns mosaic resolution statements.
     *
     * @return mosaic resolution statements.
     */
    public List<MosaicResolutionStatement> getMosaicResolutionStatement() {
        return this.mosaicResolutionStatement;
    }

    /**
     * This method tries to resolve the unresolved mosaic id using the the resolution entries.
     *
     * @param height the height of the transaction.
     * @param mosaicAlias the {@link UnresolvedMosaicId}
     * @param primaryId the primary id
     * @param secondaryId the secondary id
     * @return the {@link Optional} of the resolved {@link MosaicId}
     */
    public Optional<MosaicId> getResolvedMosaicId(BigInteger height,
        UnresolvedMosaicId mosaicAlias, long primaryId,
        long secondaryId) {
        if (mosaicAlias instanceof MosaicId) {
            return Optional.of((MosaicId) mosaicAlias);
        }
        return this.getMosaicResolutionStatement().stream()
            .filter(s -> height.equals(s.getHeight()))
            .filter(r -> r.getUnresolved().equals(mosaicAlias))
            .map(r -> r.getResolutionEntryById(primaryId, secondaryId)
                .map(ResolutionEntry::getResolved)).findFirst().flatMap(Function.identity());
    }

    /**
     * This method tries to resolve the unresolved address using the the resolution entries.
     *
     * @param height the height of the transaction.
     * @param unresolvedAddress the {@link UnresolvedAddress}
     * @param primaryId the primary id
     * @param secondaryId the secondary id
     * @return the {@link Optional} of the resolved {@link Address}
     */
    public Optional<Address> getResolvedAddress(BigInteger height,
        UnresolvedAddress unresolvedAddress, long primaryId,
        long secondaryId) {
        if (unresolvedAddress instanceof Address) {
            return Optional.of((Address) unresolvedAddress);
        }
        return this.getAddressResolutionStatements().stream()
            .filter(s -> height.equals(s.getHeight()))
            .filter(r -> r.getUnresolved().equals(unresolvedAddress))
            .map(r -> r.getResolutionEntryById(primaryId, secondaryId)
                .map(ResolutionEntry::getResolved)).findFirst().flatMap(Function.identity());
    }
}
