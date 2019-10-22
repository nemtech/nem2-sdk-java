/*
 * Copyright 2019 NEM
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

package io.nem.sdk.model.receipt;

import io.nem.core.crypto.Hashes;
import io.nem.core.utils.ArrayUtils;
import io.nem.core.utils.ByteUtils;
import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import java.math.BigInteger;
import java.util.List;
import org.bouncycastle.util.encoders.Hex;

/**
 * @param <U> the unresolved type {@link UnresolvedAddress} or  {@link UnresolvedMosaicId}
 * @param <R> the resolved type {@link io.nem.sdk.model.account.Address} or {@link
 * io.nem.sdk.model.mosaic.MosaicId}
 */
public abstract class ResolutionStatement<U, R> {

    private final ResolutionType resolutionType;
    private final BigInteger height;
    private final U unresolved;
    private final List<ResolutionEntry<R>> resolutionEntries;

    /**
     * Constructor
     *
     * @param height Height
     * @param unresolved An unresolved address or unresolved mosaicId ({@link UnresolvedAddress} |
     * {@link UnresolvedMosaicId}).
     * @param resolutionEntries Array of resolution entries ({@link io.nem.sdk.model.account.Address},
     * or {@link io.nem.sdk.model.mosaic.MosaicId}).
     */
    public ResolutionStatement(
        ResolutionType resolutionType, BigInteger height, U unresolved,
        List<ResolutionEntry<R>> resolutionEntries) {
        this.height = height;
        this.unresolved = unresolved;
        this.resolutionEntries = resolutionEntries;
        this.resolutionType = resolutionType;
        this.validateType();
    }

    /**
     * Returns An unresolved address or unresolved mosaicId (UnresolvedAddress | UnresolvedMosaic).
     *
     * @return An unresolved address or unresolved mosaicId (UnresolvedAddress | UnresolvedMosaic).
     */
    public U getUnresolved() {
        return this.unresolved;
    }

    /**
     * Returns block height
     *
     * @return block height
     */
    public BigInteger getHeight() {
        return this.height;
    }

    /**
     * Returns Array of resolution entries.
     *
     * @return Array of resolution entries.
     */
    public List<ResolutionEntry<R>> getResolutionEntries() {
        return this.resolutionEntries;
    }

    /**
     * Returns resolution type
     *
     * @return resolution type
     */
    public ResolutionType getResolutionType() {
        return this.resolutionType;
    }

    /**
     * Validate unresolved type against resolutionEntry
     *
     * @return void
     */
    private void validateType() {
        validateType(ResolutionType.ADDRESS, UnresolvedAddress.class);
        validateType(ResolutionType.MOSAIC, UnresolvedMosaicId.class);
        this.resolutionEntries.forEach(
            entry -> {
                validateType(ResolutionType.ADDRESS, ReceiptType.ADDRESS_ALIAS_RESOLUTION,
                    entry.getType());
                validateType(ResolutionType.MOSAIC, ReceiptType.MOSAIC_ALIAS_RESOLUTION,
                    entry.getType());
            });
    }


    /**
     * Validate resolved type ({@link UnresolvedMosaicId} | {@link UnresolvedAddress})
     */
    private void validateType(ResolutionType givenResolutionType, Class<?> expectedType) {
        if (!expectedType.isAssignableFrom(this.unresolved.getClass())
            && getResolutionType() == givenResolutionType) {
            throw new IllegalArgumentException(
                "Unresolved Type: ["
                    + expectedType.getName()
                    + "] is not valid for this ResolutionEntry type " + getResolutionType());
        }
    }


    private void validateType(ResolutionType givenResolutionType, ReceiptType expectedReceiptType,
        ReceiptType currentRecipientType
    ) {
        if (getResolutionType() == givenResolutionType
            && currentRecipientType != expectedReceiptType) {
            throw new IllegalArgumentException(
                "Resolution Type: ["
                    + getResolutionType()
                    + "] does not match ResolutionEntry's type: ["
                    + currentRecipientType
                    + "] for this ResolutionStatement");
        }
    }

    /**
     * Serialize resolution statement and generate hash
     *
     * @param networkType networkType
     * @return resolution statement hash
     */
    public String generateHash(NetworkType networkType) {

        final byte[] versionByte = ByteUtils.shortToBytes(
            Short.reverseBytes((short) ReceiptVersion.RESOLUTION_STATEMENT.getValue()));
        final byte[] typeByte = getResolutionType() == ResolutionType.ADDRESS ?
            ByteUtils.shortToBytes(
                Short.reverseBytes((short) ReceiptType.ADDRESS_ALIAS_RESOLUTION.getValue())) :
            ByteUtils.shortToBytes(
                Short.reverseBytes((short) ReceiptType.MOSAIC_ALIAS_RESOLUTION.getValue()));
        final byte[] unresolvedBytes = serializeUnresolved(networkType);

        byte[] results = ArrayUtils.concat(versionByte, typeByte, unresolvedBytes);

        for (final ResolutionEntry entry : resolutionEntries) {
            results = ArrayUtils.concat(results, entry.serialize());
        }

        byte[] hash = Hashes.sha3_256(results);
        return Hex.toHexString(hash).toUpperCase();
    }

    /**
     * Generate unresolved bytes
     *
     * @param networkType the network type.
     * @return unresolved bytes
     */
    private byte[] serializeUnresolved(NetworkType networkType) {
        if (getResolutionType() == ResolutionType.ADDRESS) {
            return SerializationUtils
                .fromUnresolvedAddressToByteBuffer((UnresolvedAddress) getUnresolved(), networkType)
                .array();
        }
        return ByteUtils
            .reverseCopy(ByteUtils.bigIntToBytes(((UnresolvedMosaicId) getUnresolved()).getId()));
    }

}
