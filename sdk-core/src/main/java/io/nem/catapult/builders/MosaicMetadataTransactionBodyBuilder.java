/**
*** Copyright (c) 2016-present,
*** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
***
*** This file is part of Catapult.
***
*** Catapult is free software: you can redistribute it and/or modify
*** it under the terms of the GNU Lesser General Public License as published by
*** the Free Software Foundation, either version 3 of the License, or
*** (at your option) any later version.
***
*** Catapult is distributed in the hope that it will be useful,
*** but WITHOUT ANY WARRANTY; without even the implied warranty of
*** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*** GNU Lesser General Public License for more details.
***
*** You should have received a copy of the GNU Lesser General Public License
*** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
**/

package io.nem.catapult.builders;

import java.io.DataInput;
import java.nio.ByteBuffer;

/** Binary layout for a mosaic metadata transaction. */
public final class MosaicMetadataTransactionBodyBuilder {
    /** Metadata target public key. */
    private final KeyDto targetPublicKey;
    /** Metadata key scoped to source, target and type. */
    private final long scopedMetadataKey;
    /** Target mosaic identifier. */
    private final UnresolvedMosaicIdDto targetMosaicId;
    /** Change in value size in bytes. */
    private final short valueSizeDelta;
    /** Difference between existing value and new value \note when there is no existing value, new value is same this value \note when there is an existing value, new value is calculated as xor(previous-value, value). */
    private final ByteBuffer value;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicMetadataTransactionBodyBuilder(final DataInput stream) {
        try {
            this.targetPublicKey = KeyDto.loadFromBinary(stream);
            this.scopedMetadataKey = Long.reverseBytes(stream.readLong());
            this.targetMosaicId = UnresolvedMosaicIdDto.loadFromBinary(stream);
            this.valueSizeDelta = Short.reverseBytes(stream.readShort());
            final short valueSize = Short.reverseBytes(stream.readShort());
            this.value = ByteBuffer.allocate(valueSize);
            stream.readFully(this.value.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param targetPublicKey Metadata target public key.
     * @param scopedMetadataKey Metadata key scoped to source, target and type.
     * @param targetMosaicId Target mosaic identifier.
     * @param valueSizeDelta Change in value size in bytes.
     * @param value Difference between existing value and new value \note when there is no existing value, new value is same this value \note when there is an existing value, new value is calculated as xor(previous-value, value).
     */
    protected MosaicMetadataTransactionBodyBuilder(final KeyDto targetPublicKey, final long scopedMetadataKey, final UnresolvedMosaicIdDto targetMosaicId, final short valueSizeDelta, final ByteBuffer value) {
        GeneratorUtils.notNull(targetPublicKey, "targetPublicKey is null");
        GeneratorUtils.notNull(targetMosaicId, "targetMosaicId is null");
        GeneratorUtils.notNull(value, "value is null");
        this.targetPublicKey = targetPublicKey;
        this.scopedMetadataKey = scopedMetadataKey;
        this.targetMosaicId = targetMosaicId;
        this.valueSizeDelta = valueSizeDelta;
        this.value = value;
    }

    /**
     * Creates an instance of MosaicMetadataTransactionBodyBuilder.
     *
     * @param targetPublicKey Metadata target public key.
     * @param scopedMetadataKey Metadata key scoped to source, target and type.
     * @param targetMosaicId Target mosaic identifier.
     * @param valueSizeDelta Change in value size in bytes.
     * @param value Difference between existing value and new value \note when there is no existing value, new value is same this value \note when there is an existing value, new value is calculated as xor(previous-value, value).
     * @return Instance of MosaicMetadataTransactionBodyBuilder.
     */
    public static MosaicMetadataTransactionBodyBuilder create(final KeyDto targetPublicKey, final long scopedMetadataKey, final UnresolvedMosaicIdDto targetMosaicId, final short valueSizeDelta, final ByteBuffer value) {
        return new MosaicMetadataTransactionBodyBuilder(targetPublicKey, scopedMetadataKey, targetMosaicId, valueSizeDelta, value);
    }

    /**
     * Gets metadata target public key.
     *
     * @return Metadata target public key.
     */
    public KeyDto getTargetPublicKey() {
        return this.targetPublicKey;
    }

    /**
     * Gets metadata key scoped to source, target and type.
     *
     * @return Metadata key scoped to source, target and type.
     */
    public long getScopedMetadataKey() {
        return this.scopedMetadataKey;
    }

    /**
     * Gets target mosaic identifier.
     *
     * @return Target mosaic identifier.
     */
    public UnresolvedMosaicIdDto getTargetMosaicId() {
        return this.targetMosaicId;
    }

    /**
     * Gets change in value size in bytes.
     *
     * @return Change in value size in bytes.
     */
    public short getValueSizeDelta() {
        return this.valueSizeDelta;
    }

    /**
     * Gets difference between existing value and new value \note when there is no existing value, new value is same this value \note when there is an existing value, new value is calculated as xor(previous-value, value).
     *
     * @return Difference between existing value and new value \note when there is no existing value, new value is same this value \note when there is an existing value, new value is calculated as xor(previous-value, value).
     */
    public ByteBuffer getValue() {
        return this.value;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.targetPublicKey.getSize();
        size += 8; // scopedMetadataKey
        size += this.targetMosaicId.getSize();
        size += 2; // valueSizeDelta
        size += 2; // valueSize
        size += this.value.array().length;
        return size;
    }

    /**
     * Creates an instance of MosaicMetadataTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicMetadataTransactionBodyBuilder.
     */
    public static MosaicMetadataTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new MosaicMetadataTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] targetPublicKeyBytes = this.targetPublicKey.serialize();
            dataOutputStream.write(targetPublicKeyBytes, 0, targetPublicKeyBytes.length);
            dataOutputStream.writeLong(Long.reverseBytes(this.getScopedMetadataKey()));
            final byte[] targetMosaicIdBytes = this.targetMosaicId.serialize();
            dataOutputStream.write(targetMosaicIdBytes, 0, targetMosaicIdBytes.length);
            dataOutputStream.writeShort(Short.reverseBytes(this.getValueSizeDelta()));
            dataOutputStream.writeShort(Short.reverseBytes((short) this.value.array().length));
            dataOutputStream.write(this.value.array(), 0, this.value.array().length);
        });
    }
}
