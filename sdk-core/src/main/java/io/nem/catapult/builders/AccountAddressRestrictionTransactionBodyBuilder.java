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
import java.util.ArrayList;

/** Binary layout for an account address restriction transaction. */
public final class AccountAddressRestrictionTransactionBodyBuilder {
    /** Account restriction type. */
    private final AccountRestrictionTypeDto restrictionType;
    /** Account restriction modifications. */
    private final ArrayList<AccountAddressRestrictionModificationBuilder> modifications;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AccountAddressRestrictionTransactionBodyBuilder(final DataInput stream) {
        try {
            this.restrictionType = AccountRestrictionTypeDto.loadFromBinary(stream);
            final byte modificationsCount = stream.readByte();
            this.modifications = new java.util.ArrayList<>(modificationsCount);
            for (int i = 0; i < modificationsCount; i++) {
                modifications.add(AccountAddressRestrictionModificationBuilder.loadFromBinary(stream));
            }
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param restrictionType Account restriction type.
     * @param modifications Account restriction modifications.
     */
    protected AccountAddressRestrictionTransactionBodyBuilder(final AccountRestrictionTypeDto restrictionType, final ArrayList<AccountAddressRestrictionModificationBuilder> modifications) {
        GeneratorUtils.notNull(restrictionType, "restrictionType is null");
        GeneratorUtils.notNull(modifications, "modifications is null");
        this.restrictionType = restrictionType;
        this.modifications = modifications;
    }

    /**
     * Creates an instance of AccountAddressRestrictionTransactionBodyBuilder.
     *
     * @param restrictionType Account restriction type.
     * @param modifications Account restriction modifications.
     * @return Instance of AccountAddressRestrictionTransactionBodyBuilder.
     */
    public static AccountAddressRestrictionTransactionBodyBuilder create(final AccountRestrictionTypeDto restrictionType, final ArrayList<AccountAddressRestrictionModificationBuilder> modifications) {
        return new AccountAddressRestrictionTransactionBodyBuilder(restrictionType, modifications);
    }

    /**
     * Gets account restriction type.
     *
     * @return Account restriction type.
     */
    public AccountRestrictionTypeDto getRestrictionType() {
        return this.restrictionType;
    }

    /**
     * Gets account restriction modifications.
     *
     * @return Account restriction modifications.
     */
    public ArrayList<AccountAddressRestrictionModificationBuilder> getModifications() {
        return this.modifications;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.restrictionType.getSize();
        size += 1; // modificationsCount
        size += this.modifications.stream().mapToInt(o -> o.getSize()).sum();
        return size;
    }

    /**
     * Creates an instance of AccountAddressRestrictionTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AccountAddressRestrictionTransactionBodyBuilder.
     */
    public static AccountAddressRestrictionTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new AccountAddressRestrictionTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] restrictionTypeBytes = this.restrictionType.serialize();
            dataOutputStream.write(restrictionTypeBytes, 0, restrictionTypeBytes.length);
            dataOutputStream.writeByte((byte) this.modifications.size());
            for (int i = 0; i < this.modifications.size(); i++) {
                final byte[] modificationsBytes = this.modifications.get(i).serialize();
                dataOutputStream.write(modificationsBytes, 0, modificationsBytes.length);
            }
        });
    }
}
