/**
 * ** Copyright (c) 2016-present, ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights
 * reserved. ** ** This file is part of Catapult. ** ** Catapult is free software: you can
 * redistribute it and/or modify ** it under the terms of the GNU Lesser General Public License as
 * published by ** the Free Software Foundation, either version 3 of the License, or ** (at your
 * option) any later version. ** ** Catapult is distributed in the hope that it will be useful, **
 * but WITHOUT ANY WARRANTY; without even the implied warranty of ** MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the ** GNU Lesser General Public License for more details. ** ** You
 * should have received a copy of the GNU Lesser General Public License ** along with Catapult. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package io.nem.catapult.builders;

import java.io.DataInput;

/**
 * enumeration of entity types.
 */
public enum EntityTypeDto {
    /**
     * reserved entity type.
     */
    RESERVED((short) 0),
    /**
     * Account address restriction transaction.
     */
    ACCOUNT_ADDRESS_RESTRICTION_TRANSACTION((short) 0x4150),
    /**
     * Account mosaic restriction transaction.
     */
    ACCOUNT_MOSAIC_RESTRICTION_TRANSACTION((short) 0x4250),
    /**
     * Account operation restriction transaction.
     */
    ACCOUNT_OPERATION_RESTRICTION_TRANSACTION((short) 0x4350),
    /**
     * Account link transaction.
     */
    ACCOUNT_LINK_TRANSACTION((short) 0x414C),
    /**
     * Account metadata transaction version
     */
    ACCOUNT_METADATA_TRANSACTION((short) 0x4144),
    /**
     * Mosaic metadata transaction version
     */
    MOSAIC_METADATA_TRANSACTION((short) 0x4244),
    /**
     * Namespace metadata transaction version
     */
    NAMESPACE_METADATA_TRANSACTION((short) 0x4344),
    /**
     * Address alias transaction.
     */
    ADDRESS_ALIAS_TRANSACTION((short) 0x424E),
    /**
     * Aggregate bonded transaction.
     */
    AGGREGATE_BONDED_TRANSACTION((short) 0x4241),
    /**
     * Aggregate complete transaction.
     */
    AGGREGATE_COMPLETE_TRANSACTION((short) 0x4141),
    /**
     * Hash lock transaction.
     */
    HASH_LOCK_TRANSACTION((short) 0x4148),
    /**
     * Modify multisig account transaction.
     */
    MODIFY_MULTISIG_ACCOUNT_TRANSACTION((short) 0x4155),
    /**
     * Mosaic definition transaction.
     */
    MOSAIC_DEFINITION_TRANSACTION((short) 0x414D),
    /**
     * Mosaic supply change transaction.
     */
    MOSAIC_SUPPLY_CHANGE_TRANSACTION((short) 0x424D),
    /**
     * Mosaic alias transaction.
     */
    MOSAIC_ALIAS_TRANSACTION((short) 0x434E),
    /**
     * Register namespace transaction.
     */
    REGISTER_NAMESPACE_TRANSACTION((short) 0x414E),
    /**
     * Secret lock transaction.
     */
    SECRET_LOCK_TRANSACTION((short) 0x4152),
    /**
     * Secret Proof transaction.
     */
    SECRET_PROOF_TRANSACTION((short) 0x4252),
    /**
     * Transfer transaction.
     */
    TRANSFER_TRANSACTION((short) 0x4154),
    /**
     * Mosaic address restriction transaction
     */
    MOSAIC_ADDRESS_RESTRICTION((short) 0x4251),
    /**
     * Mosaic global restriction transaction
     */
    MOSAIC_GLOBAL_RESTRICTION((short) 0x4151);

    /**
     * Enum value.
     */
    private final short value;

    /**
     * Constructor.
     *
     * @param value Enum value.
     */
    EntityTypeDto(final short value) {
        this.value = value;
    }

    /**
     * Get enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static EntityTypeDto rawValueOf(final short value) {
        for (EntityTypeDto current : EntityTypeDto.values()) {
            if (value == current.value) {
                return current;
            }
        }
        throw new IllegalArgumentException(value + " was not a backing value for EntityTypeDto.");
    }

    /**
     * Create an instance of EntityTypeDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EntityTypeDto.
     */
    public static EntityTypeDto loadFromBinary(final DataInput stream) {
        try {
            final short streamValue = Short.reverseBytes(stream.readShort());
            return rawValueOf(streamValue);
        } catch (Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Get the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return 2;
    }

    /**
     * Serialize the object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(
            dataOutputStream -> {
                dataOutputStream.writeShort(Short.reverseBytes(this.value));
            });
    }

    public short getValue() {
        return value;
    }
}
