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

/** Binary layout for a mosaic address restriction transaction. */
public final class MosaicAddressRestrictionTransactionBodyBuilder {
    /** Identifier of the mosaic to which the restriction applies. */
    private final UnresolvedMosaicIdDto mosaicId;
    /** Restriction key. */
    private final long restrictionKey;
    /** Address being restricted. */
    private final UnresolvedAddressDto targetAddress;
    /** Previous restriction value. */
    private final long previousRestrictionValue;
    /** New restriction value. */
    private final long newRestrictionValue;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicAddressRestrictionTransactionBodyBuilder(final DataInput stream) {
        try {
            this.mosaicId = UnresolvedMosaicIdDto.loadFromBinary(stream);
            this.restrictionKey = Long.reverseBytes(stream.readLong());
            this.targetAddress = UnresolvedAddressDto.loadFromBinary(stream);
            this.previousRestrictionValue = Long.reverseBytes(stream.readLong());
            this.newRestrictionValue = Long.reverseBytes(stream.readLong());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param mosaicId Identifier of the mosaic to which the restriction applies.
     * @param restrictionKey Restriction key.
     * @param targetAddress Address being restricted.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     */
    protected MosaicAddressRestrictionTransactionBodyBuilder(final UnresolvedMosaicIdDto mosaicId, final long restrictionKey, final UnresolvedAddressDto targetAddress, final long previousRestrictionValue, final long newRestrictionValue) {
        GeneratorUtils.notNull(mosaicId, "mosaicId is null");
        GeneratorUtils.notNull(targetAddress, "targetAddress is null");
        this.mosaicId = mosaicId;
        this.restrictionKey = restrictionKey;
        this.targetAddress = targetAddress;
        this.previousRestrictionValue = previousRestrictionValue;
        this.newRestrictionValue = newRestrictionValue;
    }

    /**
     * Creates an instance of MosaicAddressRestrictionTransactionBodyBuilder.
     *
     * @param mosaicId Identifier of the mosaic to which the restriction applies.
     * @param restrictionKey Restriction key.
     * @param targetAddress Address being restricted.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     * @return Instance of MosaicAddressRestrictionTransactionBodyBuilder.
     */
    public static MosaicAddressRestrictionTransactionBodyBuilder create(final UnresolvedMosaicIdDto mosaicId, final long restrictionKey, final UnresolvedAddressDto targetAddress, final long previousRestrictionValue, final long newRestrictionValue) {
        return new MosaicAddressRestrictionTransactionBodyBuilder(mosaicId, restrictionKey, targetAddress, previousRestrictionValue, newRestrictionValue);
    }

    /**
     * Gets identifier of the mosaic to which the restriction applies.
     *
     * @return Identifier of the mosaic to which the restriction applies.
     */
    public UnresolvedMosaicIdDto getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Gets restriction key.
     *
     * @return Restriction key.
     */
    public long getRestrictionKey() {
        return this.restrictionKey;
    }

    /**
     * Gets address being restricted.
     *
     * @return Address being restricted.
     */
    public UnresolvedAddressDto getTargetAddress() {
        return this.targetAddress;
    }

    /**
     * Gets previous restriction value.
     *
     * @return Previous restriction value.
     */
    public long getPreviousRestrictionValue() {
        return this.previousRestrictionValue;
    }

    /**
     * Gets new restriction value.
     *
     * @return New restriction value.
     */
    public long getNewRestrictionValue() {
        return this.newRestrictionValue;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.mosaicId.getSize();
        size += 8; // restrictionKey
        size += this.targetAddress.getSize();
        size += 8; // previousRestrictionValue
        size += 8; // newRestrictionValue
        return size;
    }

    /**
     * Creates an instance of MosaicAddressRestrictionTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicAddressRestrictionTransactionBodyBuilder.
     */
    public static MosaicAddressRestrictionTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new MosaicAddressRestrictionTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] mosaicIdBytes = this.mosaicId.serialize();
            dataOutputStream.write(mosaicIdBytes, 0, mosaicIdBytes.length);
            dataOutputStream.writeLong(Long.reverseBytes(this.getRestrictionKey()));
            final byte[] targetAddressBytes = this.targetAddress.serialize();
            dataOutputStream.write(targetAddressBytes, 0, targetAddressBytes.length);
            dataOutputStream.writeLong(Long.reverseBytes(this.getPreviousRestrictionValue()));
            dataOutputStream.writeLong(Long.reverseBytes(this.getNewRestrictionValue()));
        });
    }
}
