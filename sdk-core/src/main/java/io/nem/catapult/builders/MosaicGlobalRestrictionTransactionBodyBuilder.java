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

/** Binary layout for a mosaic global restriction transaction. */
public final class MosaicGlobalRestrictionTransactionBodyBuilder {
    /** Identifier of the mosaic being restricted. */
    private final UnresolvedMosaicIdDto mosaicId;
    /** Identifier of the mosaic providing the restriction key. */
    private final UnresolvedMosaicIdDto referenceMosaicId;
    /** Restriction key relative to the reference mosaic identifier. */
    private final long restrictionKey;
    /** Previous restriction value. */
    private final long previousRestrictionValue;
    /** Previous restriction type. */
    private final MosaicRestrictionTypeDto previousRestrictionType;
    /** New restriction value. */
    private final long newRestrictionValue;
    /** New restriction type. */
    private final MosaicRestrictionTypeDto newRestrictionType;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicGlobalRestrictionTransactionBodyBuilder(final DataInput stream) {
        try {
            this.mosaicId = UnresolvedMosaicIdDto.loadFromBinary(stream);
            this.referenceMosaicId = UnresolvedMosaicIdDto.loadFromBinary(stream);
            this.restrictionKey = Long.reverseBytes(stream.readLong());
            this.previousRestrictionValue = Long.reverseBytes(stream.readLong());
            this.previousRestrictionType = MosaicRestrictionTypeDto.loadFromBinary(stream);
            this.newRestrictionValue = Long.reverseBytes(stream.readLong());
            this.newRestrictionType = MosaicRestrictionTypeDto.loadFromBinary(stream);
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param mosaicId Identifier of the mosaic being restricted.
     * @param referenceMosaicId Identifier of the mosaic providing the restriction key.
     * @param restrictionKey Restriction key relative to the reference mosaic identifier.
     * @param previousRestrictionValue Previous restriction value.
     * @param previousRestrictionType Previous restriction type.
     * @param newRestrictionValue New restriction value.
     * @param newRestrictionType New restriction type.
     */
    protected MosaicGlobalRestrictionTransactionBodyBuilder(final UnresolvedMosaicIdDto mosaicId, final UnresolvedMosaicIdDto referenceMosaicId, final long restrictionKey, final long previousRestrictionValue, final MosaicRestrictionTypeDto previousRestrictionType, final long newRestrictionValue, final MosaicRestrictionTypeDto newRestrictionType) {
        GeneratorUtils.notNull(mosaicId, "mosaicId is null");
        GeneratorUtils.notNull(referenceMosaicId, "referenceMosaicId is null");
        GeneratorUtils.notNull(previousRestrictionType, "previousRestrictionType is null");
        GeneratorUtils.notNull(newRestrictionType, "newRestrictionType is null");
        this.mosaicId = mosaicId;
        this.referenceMosaicId = referenceMosaicId;
        this.restrictionKey = restrictionKey;
        this.previousRestrictionValue = previousRestrictionValue;
        this.previousRestrictionType = previousRestrictionType;
        this.newRestrictionValue = newRestrictionValue;
        this.newRestrictionType = newRestrictionType;
    }

    /**
     * Creates an instance of MosaicGlobalRestrictionTransactionBodyBuilder.
     *
     * @param mosaicId Identifier of the mosaic being restricted.
     * @param referenceMosaicId Identifier of the mosaic providing the restriction key.
     * @param restrictionKey Restriction key relative to the reference mosaic identifier.
     * @param previousRestrictionValue Previous restriction value.
     * @param previousRestrictionType Previous restriction type.
     * @param newRestrictionValue New restriction value.
     * @param newRestrictionType New restriction type.
     * @return Instance of MosaicGlobalRestrictionTransactionBodyBuilder.
     */
    public static MosaicGlobalRestrictionTransactionBodyBuilder create(final UnresolvedMosaicIdDto mosaicId, final UnresolvedMosaicIdDto referenceMosaicId, final long restrictionKey, final long previousRestrictionValue, final MosaicRestrictionTypeDto previousRestrictionType, final long newRestrictionValue, final MosaicRestrictionTypeDto newRestrictionType) {
        return new MosaicGlobalRestrictionTransactionBodyBuilder(mosaicId, referenceMosaicId, restrictionKey, previousRestrictionValue, previousRestrictionType, newRestrictionValue, newRestrictionType);
    }

    /**
     * Gets identifier of the mosaic being restricted.
     *
     * @return Identifier of the mosaic being restricted.
     */
    public UnresolvedMosaicIdDto getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Gets identifier of the mosaic providing the restriction key.
     *
     * @return Identifier of the mosaic providing the restriction key.
     */
    public UnresolvedMosaicIdDto getReferenceMosaicId() {
        return this.referenceMosaicId;
    }

    /**
     * Gets restriction key relative to the reference mosaic identifier.
     *
     * @return Restriction key relative to the reference mosaic identifier.
     */
    public long getRestrictionKey() {
        return this.restrictionKey;
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
     * Gets previous restriction type.
     *
     * @return Previous restriction type.
     */
    public MosaicRestrictionTypeDto getPreviousRestrictionType() {
        return this.previousRestrictionType;
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
     * Gets new restriction type.
     *
     * @return New restriction type.
     */
    public MosaicRestrictionTypeDto getNewRestrictionType() {
        return this.newRestrictionType;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.mosaicId.getSize();
        size += this.referenceMosaicId.getSize();
        size += 8; // restrictionKey
        size += 8; // previousRestrictionValue
        size += this.previousRestrictionType.getSize();
        size += 8; // newRestrictionValue
        size += this.newRestrictionType.getSize();
        return size;
    }

    /**
     * Creates an instance of MosaicGlobalRestrictionTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicGlobalRestrictionTransactionBodyBuilder.
     */
    public static MosaicGlobalRestrictionTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new MosaicGlobalRestrictionTransactionBodyBuilder(stream);
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
            final byte[] referenceMosaicIdBytes = this.referenceMosaicId.serialize();
            dataOutputStream.write(referenceMosaicIdBytes, 0, referenceMosaicIdBytes.length);
            dataOutputStream.writeLong(Long.reverseBytes(this.getRestrictionKey()));
            dataOutputStream.writeLong(Long.reverseBytes(this.getPreviousRestrictionValue()));
            final byte[] previousRestrictionTypeBytes = this.previousRestrictionType.serialize();
            dataOutputStream.write(previousRestrictionTypeBytes, 0, previousRestrictionTypeBytes.length);
            dataOutputStream.writeLong(Long.reverseBytes(this.getNewRestrictionValue()));
            final byte[] newRestrictionTypeBytes = this.newRestrictionType.serialize();
            dataOutputStream.write(newRestrictionTypeBytes, 0, newRestrictionTypeBytes.length);
        });
    }
}
