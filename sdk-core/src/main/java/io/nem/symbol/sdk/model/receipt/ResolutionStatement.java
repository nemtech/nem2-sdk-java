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

import io.nem.symbol.catapult.builders.AddressResolutionEntryBuilder;
import io.nem.symbol.catapult.builders.AddressResolutionStatementBuilder;
import io.nem.symbol.catapult.builders.MosaicResolutionEntryBuilder;
import io.nem.symbol.catapult.builders.MosaicResolutionStatementBuilder;
import io.nem.symbol.catapult.builders.ReceiptSourceBuilder;
import io.nem.symbol.catapult.builders.ReceiptTypeDto;
import io.nem.symbol.catapult.builders.Serializer;
import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @param <U> the unresolved type {@link UnresolvedAddress} or  {@link UnresolvedMosaicId}
 * @param <R> the resolved type {@link Address} or {@link MosaicId}
 */
public abstract class ResolutionStatement<U, R> implements Stored {

    private final Optional<String> recordId;
    private final ResolutionType resolutionType;
    private final BigInteger height;
    private final U unresolved;
    private final List<ResolutionEntry<R>> resolutionEntries;

    /**
     * Constructor
     *
     * @param recordId the stored database id if known
     * @param resolutionType the ResolutionType
     * @param height Height
     * @param unresolved An unresolved address or unresolved mosaicId ({@link UnresolvedAddress} | {@link
* UnresolvedMosaicId}).
     * @param resolutionEntries Array of resolution entries ({@link Address}, or {@link MosaicId}).
     */
    public ResolutionStatement(String recordId, ResolutionType resolutionType, BigInteger height, U unresolved, List<ResolutionEntry<R>> resolutionEntries) {
        this.recordId = Optional.ofNullable(recordId);
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
        this.resolutionEntries.forEach(entry -> {
            validateType(ResolutionType.ADDRESS, ReceiptType.ADDRESS_ALIAS_RESOLUTION, entry.getType());
            validateType(ResolutionType.MOSAIC, ReceiptType.MOSAIC_ALIAS_RESOLUTION, entry.getType());
        });
    }


    /**
     * Validate resolved type ({@link UnresolvedMosaicId} | {@link UnresolvedAddress})
     */
    private void validateType(ResolutionType givenResolutionType, Class<?> expectedType) {
        if (!expectedType.isAssignableFrom(this.unresolved.getClass()) && getResolutionType() == givenResolutionType) {
            throw new IllegalArgumentException(
                "Unresolved Type: [" + expectedType.getName() + "] is not valid for this ResolutionEntry type "
                    + getResolutionType());
        }
    }


    private void validateType(ResolutionType givenResolutionType, ReceiptType expectedReceiptType,
        ReceiptType currentRecipientType) {
        if (getResolutionType() == givenResolutionType && currentRecipientType != expectedReceiptType) {
            throw new IllegalArgumentException(
                "Resolution Type: [" + getResolutionType() + "] does not match ResolutionEntry's type: ["
                    + currentRecipientType + "] for this ResolutionStatement");
        }
    }


    /**
     * Find resolution entry for given primaryId and secondaryId.
     *
     * @param primaryId Primary id
     * @param secondaryId Secondary id
     * @return Optional of {@link ResolutionEntry}
     */
    public Optional<ResolutionEntry<R>> getResolutionEntryById(long primaryId, long secondaryId) {
        /*
        Primary id and secondary id do not specifically map to the exact transaction index on the same block.
        The ids are just the order of the resolution reflecting on the order of transactions (ordered by index).
        E.g 1 - Bob -> 1 random.token -> Alice
            2 - Carol -> 1 random.token > Denis
        Based on above example, 2 transactions (index 0 & 1) are created on the same block, however, only 1
        resolution entry get generated for both.
        */
        long resolvedPrimaryId = getMaxAvailablePrimaryId(primaryId);

        /*
        If no primaryId found, it means there's no resolution entry available for the process. Invalid entry.

        e.g. Given:
        Entries: [{P:2, S:0}, {P:5, S:6}]
        Transaction: [Inx:1(0+1), AggInx:0]
        It should return Entry: undefined
        */
        if (resolvedPrimaryId == 0) {
            return Optional.empty();
        } else if (primaryId > resolvedPrimaryId) {
            /*
            If the transaction index is greater than the overall most recent source primary id.
            Use the most recent resolution entry (Max.PrimaryId + Max.SecondaryId)

            e.g. Given:
            Entries: [{P:1, S:0}, {P:2, S:0}, {P:4, S:2}, {P:4, S:4} {P:7, S:6}]
            Transaction: [Inx:5(4+1), AggInx:0]
            It should return Entry: {P:4, S:4}

            e.g. Given:
            Entries: [{P:1, S:0}, {P:2, S:0}, {P:4, S:2}, {P:4, S:4}, {P:7, S:6}]
            Transaction: [Inx:3(2+1), AggInx:0]
            It should return Entry: {P:2, S:0}
            */
            return this.resolutionEntries.stream().filter(
                entry -> entry.getReceiptSource().getPrimaryId() == resolvedPrimaryId
                    && entry.getReceiptSource().getSecondaryId() == this
                    .getMaxSecondaryIdByPrimaryId(resolvedPrimaryId)).findFirst();
        }

        // When transaction index matches a primaryId, get the most recent secondaryId (resolvedPrimaryId can only <= primaryId)
        long resolvedSecondaryId = this.getMaxSecondaryIdByPrimaryIdAndSecondaryId(resolvedPrimaryId, secondaryId);

        /*
        If no most recent secondaryId matched transaction index, find previous resolution entry (most recent).
        This means the resolution entry for the specific inner transaction (inside Aggregate) /
        was generated previously outside the aggregate. It should return the previous entry (previous primaryId)

        e.g. Given:
        Entries: [{P:1, S:0}, {P:2, S:0}, {P:5, S:6}]
        Transaction: [Inx:5(4+1), AggInx:3(2+1)]
        It should return Entry: {P:2, S:0}
        */
        if (resolvedSecondaryId == 0 && resolvedSecondaryId != secondaryId) {
            long lastPrimaryId = this.getMaxAvailablePrimaryId(resolvedPrimaryId - 1);
            return this.resolutionEntries.stream().filter(
                entry -> entry.getReceiptSource().getPrimaryId() == lastPrimaryId
                    && entry.getReceiptSource().getSecondaryId() == this.getMaxSecondaryIdByPrimaryId(lastPrimaryId))
                .findFirst();
        }

        /*
        Found a matched resolution entry on both primaryId and secondaryId

        e.g. Given:
        Entries: [{P:1, S:0}, {P:2, S:0}, {P:5, S:6}]
        Transaction: [Inx:5(4+1), AggInx:6(2+1)]
        It should return Entry: {P:5, S:6}
        */
        return this.resolutionEntries.stream()
            .filter(entry -> entry.getReceiptSource().getPrimaryId() == resolvedPrimaryId)
            .filter(entry -> entry.getReceiptSource().getSecondaryId() == resolvedSecondaryId).findFirst();
    }

    /**
     * Get max secondary id by a given primaryId
     *
     * @param primaryId Primary source id
     * @return Get max secondary id by a given primaryId
     */
    private long getMaxSecondaryIdByPrimaryId(long primaryId) {
        return this.resolutionEntries.stream().filter(entry -> entry.getReceiptSource().getPrimaryId() == primaryId)
            .mapToLong(entry -> entry.getReceiptSource().getSecondaryId()).max().orElseThrow(
                () -> new IllegalArgumentException(
                    "resolutionEntries is empty when calculating getMaxSecondaryIdByPrimaryId"));
    }


    /**
     * Get most `recent` available secondary id by a given primaryId
     *
     * @param primaryId Primary source id
     * @param secondaryId Secondary source id
     * @return the expected max available.
     */
    private long getMaxSecondaryIdByPrimaryIdAndSecondaryId(long primaryId, long secondaryId) {

        return this.resolutionEntries.stream().filter(entry -> entry.getReceiptSource().getPrimaryId() == primaryId)
            .mapToLong(entry -> secondaryId >= entry.getReceiptSource().getSecondaryId() ? entry.getReceiptSource()
                .getSecondaryId() : 0).max().orElseThrow(() -> new IllegalArgumentException(
                "resolutionEntries is empty when calculating getMaxSecondaryIdByPrimaryIdAndSecondaryId"));
    }

    /**
     * Get most `recent` primary source id by a given id (transaction index) as PrimaryId might not be the same as block
     * transaction index.
     *
     * @param primaryId Primary source id
     * @return the expected max available.
     */
    private long getMaxAvailablePrimaryId(long primaryId) {

        return this.resolutionEntries.stream().mapToLong(
            entry -> primaryId >= entry.getReceiptSource().getPrimaryId() ? entry.getReceiptSource().getPrimaryId() : 0)
            .max().orElseThrow(() -> new IllegalArgumentException(
                "resolutionEntries is empty when calculating getMaxAvailablePrimaryId"));
    }

    /**
     * Serialize resolution statement and generate hash
     *
     * @param networkType networkType
     * @return resolution statement hash
     */
    public String generateHash(NetworkType networkType) {
        byte[] serialized = serialize(networkType);
        byte[] hash = Hashes.sha3_256(serialized);
        return ConvertUtils.toHex(hash);
    }

    /**
     * Serializes the statement using the catuffer builders
     *
     * @param networkType the network type.
     * @return the serialized content.
     */
    private byte[] serialize(NetworkType networkType) {
        ReceiptType type = this.resolutionType == ResolutionType.ADDRESS ? ReceiptType.ADDRESS_ALIAS_RESOLUTION
            : ReceiptType.MOSAIC_ALIAS_RESOLUTION;
        ReceiptTypeDto recipientTypeDto = ReceiptTypeDto.rawValueOf((short) type.getValue());
        short version = (short) ReceiptVersion.RESOLUTION_STATEMENT.getValue();
        Serializer serializer = this.resolutionType == ResolutionType.ADDRESS ? AddressResolutionStatementBuilder
            .create(version, recipientTypeDto,
                SerializationUtils.toUnresolvedAddress((UnresolvedAddress) this.unresolved, networkType),
                this.resolutionEntries.stream().map((entry) -> AddressResolutionEntryBuilder.create(ReceiptSourceBuilder
                        .create((int) entry.getReceiptSource().getPrimaryId(),
                            (int) entry.getReceiptSource().getSecondaryId()),
                    SerializationUtils.toAddressDto((Address) entry.getResolved()))).collect(Collectors.toList()))

            : MosaicResolutionStatementBuilder.create(version, recipientTypeDto,
                SerializationUtils.toUnresolvedMosaicIdDto((UnresolvedMosaicId) this.unresolved),
                this.resolutionEntries.stream().map((entry) -> MosaicResolutionEntryBuilder.create(ReceiptSourceBuilder
                        .create((int) entry.getReceiptSource().getPrimaryId(),(int)  entry.getReceiptSource().getSecondaryId()),
                    SerializationUtils.toMosaicIdDto((MosaicId) entry.getResolved()))).collect(Collectors.toList()));
        return serializer.serialize();
    }

    @Override
    public Optional<String> getRecordId() {
        return recordId;
    }
}
