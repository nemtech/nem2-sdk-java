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
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link MosaicAddressRestrictionTransaction}
 */
public class MosaicAddressRestrictionTransactionFactory
    extends TransactionFactory<MosaicAddressRestrictionTransaction> {

    private final MosaicId mosaicId;
    private final BigInteger restrictionKey;
    private final Address targetAddress;
    private BigInteger previousRestrictionValue = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    private final BigInteger newRestrictionValue;

    /**
     * Create a mosaic address restriction transaction object with factory build and modifier
     * methods.
     *
     * @param networkType {@link NetworkType}
     * @param mosaicId {@link MosaicId}
     * @param restrictionKey BigInteger
     * @param targetAddress {@link Address}
     * @param newRestrictionValue BigInteger
     */
    private MosaicAddressRestrictionTransactionFactory(
        NetworkType networkType,
        MosaicId mosaicId,
        BigInteger restrictionKey,
        Address targetAddress,
        BigInteger newRestrictionValue) {
        super(TransactionType.MOSAIC_ADDRESS_RESTRICTION, networkType);
        Validate.notNull(mosaicId, "UnresolvedMosaicId must not be null");
        Validate.notNull(restrictionKey, "RestrictionKey must not be null");
        Validate.notNull(targetAddress, "TargetAddress must not be null");
        Validate.notNull(newRestrictionValue, "NewRestrictionValue must not be null");
        this.mosaicId = mosaicId;
        this.restrictionKey = restrictionKey;
        this.targetAddress = targetAddress;
        this.newRestrictionValue = newRestrictionValue;
    }

    /**
     * Static create method for factory.
     *
     * @param networkType {@link NetworkType}
     * @param mosaicId {@link MosaicId}
     * @param restrictionKey Restriction key.
     * @param targetAddress {@link Address}
     * @param newRestrictionValue New restriction value.
     * @return Mosaic address restriction transaction.
     */
    public static MosaicAddressRestrictionTransactionFactory create(NetworkType networkType,
        MosaicId mosaicId,
        BigInteger restrictionKey,
        Address targetAddress,
        BigInteger newRestrictionValue) {
        return new MosaicAddressRestrictionTransactionFactory(networkType, mosaicId, restrictionKey, targetAddress, newRestrictionValue);
    }

    @Override
    public MosaicAddressRestrictionTransaction build() {
        return new MosaicAddressRestrictionTransaction(this);
    }

    /**
     * Returns the mosaic id.
     *
     * @return {@link MosaicId}
     */
    public MosaicId getMosaicId() {
        return mosaicId;
    }

    /**
     * Returns the restriction key.
     *
     * @return BigInteger restrictionKey
     */
    public BigInteger getRestrictionKey() {
        return restrictionKey;
    }

    /**
     * Returns the target address.
     *
     * @return {@link Address}
     */
    public Address getTargetAddress() {
        return targetAddress;
    }

    /**
     * Returns previous restriction value.
     *
     * @return {@link BigInteger}
     */
    public BigInteger getPreviousRestrictionValue() {
        return previousRestrictionValue;
    }

    /**
     * It sets the previoudRestrictionValue when necessary.
     * @param previousRestrictionValue
     * @return
     */
    public MosaicAddressRestrictionTransactionFactory previousRestrictionValue(
        BigInteger previousRestrictionValue) {
        this.previousRestrictionValue = previousRestrictionValue;
        Validate.notNull(previousRestrictionValue, "PreviousRestrictionValue must not be null");
        return this;
    }

    /**
     * Returns the new restriction value.
     *
     * @return BigInteger newRestrictionValue
     */
    public BigInteger getNewRestrictionValue() {
        return newRestrictionValue;
    }
}
