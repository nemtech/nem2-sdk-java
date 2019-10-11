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

/** Enumeration of mosaic restriction types. */
public enum MosaicRestrictionTypeDto {
    /** Uninitialized value indicating no restriction. */
    NONE((byte) 0),
    /** Allow if equal. */
    EQ((byte) 1),
    /** Allow if not equal. */
    NE((byte) 2),
    /** Allow if less than. */
    LT((byte) 3),
    /** Allow if less than or equal. */
    LE((byte) 4),
    /** Allow if greater than. */
    GT((byte) 5),
    /** Allow if greater than or equal. */
    GE((byte) 6);

    /** Enum value. */
    private final byte value;

    /**
     * Constructor.
     *
     * @param value Enum value.
     */
     MosaicRestrictionTypeDto(final byte value) {
        this.value = value;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static MosaicRestrictionTypeDto rawValueOf(final byte value) {
        for (MosaicRestrictionTypeDto current : MosaicRestrictionTypeDto.values()) {
            if (value == current.value) {
                return current;
            }
        }
        throw new IllegalArgumentException(value + " was not a backing value for MosaicRestrictionTypeDto.");
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return 1;
    }

    /**
     * Creates an instance of MosaicRestrictionTypeDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicRestrictionTypeDto.
     */
    public static MosaicRestrictionTypeDto loadFromBinary(final DataInput stream) {
        try {
            final byte streamValue = stream.readByte();
            return rawValueOf(streamValue);
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeByte(this.value);
        });
    }

    public byte getValue() {
        return value;
    }
}
