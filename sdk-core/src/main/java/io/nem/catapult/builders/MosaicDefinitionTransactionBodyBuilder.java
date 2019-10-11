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
import java.util.EnumSet;

/** Binary layout for a mosaic definition transaction. */
public final class MosaicDefinitionTransactionBodyBuilder {
    /** Mosaic nonce. */
    private final MosaicNonceDto nonce;
    /** Mosaic identifier. */
    private final MosaicIdDto id;
    /** Mosaic flags. */
    private final EnumSet<MosaicFlagsDto> flags;
    /** Mosaic divisibility. */
    private final byte divisibility;
    /** Mosaic duration. */
    private final BlockDurationDto duration;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicDefinitionTransactionBodyBuilder(final DataInput stream) {
        try {
            this.nonce = MosaicNonceDto.loadFromBinary(stream);
            this.id = MosaicIdDto.loadFromBinary(stream);
            this.flags = GeneratorUtils.toSet(MosaicFlagsDto.class, stream.readByte());
            this.divisibility = stream.readByte();
            this.duration = BlockDurationDto.loadFromBinary(stream);
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param nonce Mosaic nonce.
     * @param id Mosaic identifier.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     * @param duration Mosaic duration.
     */
    protected MosaicDefinitionTransactionBodyBuilder(final MosaicNonceDto nonce, final MosaicIdDto id, final EnumSet<MosaicFlagsDto> flags, final byte divisibility, final BlockDurationDto duration) {
        GeneratorUtils.notNull(nonce, "nonce is null");
        GeneratorUtils.notNull(id, "id is null");
        GeneratorUtils.notNull(flags, "flags is null");
        GeneratorUtils.notNull(duration, "duration is null");
        this.nonce = nonce;
        this.id = id;
        this.flags = flags;
        this.divisibility = divisibility;
        this.duration = duration;
    }

    /**
     * Creates an instance of MosaicDefinitionTransactionBodyBuilder.
     *
     * @param nonce Mosaic nonce.
     * @param id Mosaic identifier.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     * @param duration Mosaic duration.
     * @return Instance of MosaicDefinitionTransactionBodyBuilder.
     */
    public static MosaicDefinitionTransactionBodyBuilder create(final MosaicNonceDto nonce, final MosaicIdDto id, final EnumSet<MosaicFlagsDto> flags, final byte divisibility, final BlockDurationDto duration) {
        return new MosaicDefinitionTransactionBodyBuilder(nonce, id, flags, divisibility, duration);
    }

    /**
     * Gets mosaic nonce.
     *
     * @return Mosaic nonce.
     */
    public MosaicNonceDto getNonce() {
        return this.nonce;
    }

    /**
     * Gets mosaic identifier.
     *
     * @return Mosaic identifier.
     */
    public MosaicIdDto getId() {
        return this.id;
    }

    /**
     * Gets mosaic flags.
     *
     * @return Mosaic flags.
     */
    public EnumSet<MosaicFlagsDto> getFlags() {
        return this.flags;
    }

    /**
     * Gets mosaic divisibility.
     *
     * @return Mosaic divisibility.
     */
    public byte getDivisibility() {
        return this.divisibility;
    }

    /**
     * Gets mosaic duration.
     *
     * @return Mosaic duration.
     */
    public BlockDurationDto getDuration() {
        return this.duration;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.nonce.getSize();
        size += this.id.getSize();
        size += MosaicFlagsDto.values()[0].getSize(); // flags
        size += 1; // divisibility
        size += this.duration.getSize();
        return size;
    }

    /**
     * Creates an instance of MosaicDefinitionTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicDefinitionTransactionBodyBuilder.
     */
    public static MosaicDefinitionTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new MosaicDefinitionTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] nonceBytes = this.nonce.serialize();
            dataOutputStream.write(nonceBytes, 0, nonceBytes.length);
            final byte[] idBytes = this.id.serialize();
            dataOutputStream.write(idBytes, 0, idBytes.length);
            final byte bitMask = (byte) GeneratorUtils.toLong(MosaicFlagsDto.class, this.flags);
            dataOutputStream.writeByte(bitMask);
            dataOutputStream.writeByte(this.getDivisibility());
            final byte[] durationBytes = this.duration.serialize();
            dataOutputStream.write(durationBytes, 0, durationBytes.length);
        });
    }
}
