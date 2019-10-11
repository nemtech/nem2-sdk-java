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

/** Enumeration of lock hash algorithms. */
public enum LockHashAlgorithmDto {
    /** Input is hashed using sha-3 256. */
    SHA3_256((byte) 0),
    /** Input is hashed using keccak 256. */
    KECCAK_256((byte) 1),
    /** Input is hashed twice: first with sha-256 and then with ripemd-160 (bitcoin's OP_HASH160). */
    HASH_160((byte) 2),
    /** Input is hashed twice with sha-256 (bitcoin's OP_HASH256). */
    HASH_256((byte) 3);

    /** Enum value. */
    private final byte value;

    /**
     * Constructor.
     *
     * @param value Enum value.
     */
     LockHashAlgorithmDto(final byte value) {
        this.value = value;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static LockHashAlgorithmDto rawValueOf(final byte value) {
        for (LockHashAlgorithmDto current : LockHashAlgorithmDto.values()) {
            if (value == current.value) {
                return current;
            }
        }
        throw new IllegalArgumentException(value + " was not a backing value for LockHashAlgorithmDto.");
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
     * Creates an instance of LockHashAlgorithmDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of LockHashAlgorithmDto.
     */
    public static LockHashAlgorithmDto loadFromBinary(final DataInput stream) {
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
