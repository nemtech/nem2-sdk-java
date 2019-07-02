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

import io.nem.core.utils.HexEncoder;
import io.nem.core.utils.StringEncoder;

import java.io.DataInput;
import java.nio.ByteBuffer;

/** Signature. */
public final class SignatureDto {
    /** Signature. */
    public final static int SIZE = 64;
    private final ByteBuffer signature;

    /**
     * Constructor.
     *
     * @param signature Signature.
     */
    public SignatureDto(final ByteBuffer signature) {
        GeneratorUtils.notNull(signature, "signature is null");
        GeneratorUtils.isTrue(signature.array().length == SIZE, "signature should be " + SIZE + " bytes");
        this.signature = signature;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public SignatureDto(final DataInput stream) {
        try {
            this.signature = ByteBuffer.allocate(SIZE);
            stream.readFully(this.signature.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Create a SignatureDto from a signature string.
     *
     * @param signature String.
     */
    public static SignatureDto create(final String signature) {
        return new SignatureDto(StringEncoder.getByteBuffer(signature, SIZE));
    }

    /**
     * Gets Signature.
     *
     * @return Signature.
     */
    public ByteBuffer getSignature() {
        return this.signature;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return SIZE;
    }

    /**
     * Creates an instance of SignatureDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of SignatureDto.
     */
    public static SignatureDto loadFromBinary(final DataInput stream) {
        return new SignatureDto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.write(this.signature.array(), 0, this.signature.array().length);
        });
    }

    /**
     * Returns the object data as a string value
     *
     * @return String
     */
    public String asString() {
        return HexEncoder.getString(this.getSignature().array());
    }
}