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

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedMosaicAddressRestrictionTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicAddressRestrictionTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedAddressDto;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Mosaic address restriction transaction.
 *
 * Enabling accounts to transact with the token is similar to the process of adding elevated
 * permissions to a user in a company computer network.
 *
 * The mosaic creator can modify the permissions of an account by sending a mosaic restriction
 * transaction targeting the account address.
 *
 * **MosaicAddressRestrictionTransaction can only be announced in with Aggregate Transaction
 *
 * @since 1.0
 */
public class MosaicAddressRestrictionTransaction extends Transaction {

    private final UnresolvedMosaicId mosaicId;
    private final BigInteger restrictionKey;
    private final UnresolvedAddress targetAddress;
    private final BigInteger previousRestrictionValue;
    private final BigInteger newRestrictionValue;

    /**
     * Constructor.
     *
     * @param factory {@link MosaicAddressRestrictionTransactionFactory}
     */
    MosaicAddressRestrictionTransaction(MosaicAddressRestrictionTransactionFactory factory) {
        super(factory);
        mosaicId = factory.getMosaicId();
        restrictionKey = factory.getRestrictionKey();
        targetAddress = factory.getTargetAddress();
        previousRestrictionValue = factory.getPreviousRestrictionValue();
        newRestrictionValue = factory.getNewRestrictionValue();
    }

    /**
     * Returns the mosaic id.
     *
     * @return {@link UnresolvedMosaicId}
     */
    public UnresolvedMosaicId getMosaicId() {
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
    public UnresolvedAddress getTargetAddress() {
        return targetAddress;
    }

    /**
     * Returns the previous restriction value.
     *
     * @return BigInteger previousRestrictionValue
     */
    public BigInteger getPreviousRestrictionValue() {
        return previousRestrictionValue;
    }

    /**
     * Returns the new restriction value.
     *
     * @return BigInteger newRestrictionValue
     */
    public BigInteger getNewRestrictionValue() {
        return newRestrictionValue;
    }

    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        MosaicAddressRestrictionTransactionBuilder txBuilder =
            MosaicAddressRestrictionTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new UnresolvedMosaicIdDto(getMosaicId().getIdAsLong()),
                getRestrictionKey().longValue(),
                new UnresolvedAddressDto(
                    SerializationUtils.fromUnresolvedAddressToByteBuffer(getTargetAddress())),
                getPreviousRestrictionValue().longValue(),
                getNewRestrictionValue().longValue()
            );
        return txBuilder.serialize();
    }

    @Override
    byte[] generateEmbeddedBytes() {

        EmbeddedMosaicAddressRestrictionTransactionBuilder txBuilder =
            EmbeddedMosaicAddressRestrictionTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                new UnresolvedMosaicIdDto(getMosaicId().getIdAsLong()),
                getRestrictionKey().longValue(),
                new UnresolvedAddressDto(
                    SerializationUtils.fromUnresolvedAddressToByteBuffer(getTargetAddress())),
                getPreviousRestrictionValue().longValue(),
                getNewRestrictionValue().longValue()
            );
        return txBuilder.serialize();
    }
}
