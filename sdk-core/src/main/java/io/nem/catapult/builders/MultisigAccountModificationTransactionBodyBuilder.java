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

/** Binary layout for a multisig account modification transaction. */
public final class MultisigAccountModificationTransactionBodyBuilder {
    /** Relative change of the minimal number of cosignatories required when removing an account. */
    private final byte minRemovalDelta;
    /** Relative change of the minimal number of cosignatories required when approving a transaction. */
    private final byte minApprovalDelta;
    /** Attached cosignatory modifications. */
    private final ArrayList<CosignatoryModificationBuilder> modifications;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MultisigAccountModificationTransactionBodyBuilder(final DataInput stream) {
        try {
            this.minRemovalDelta = stream.readByte();
            this.minApprovalDelta = stream.readByte();
            final byte modificationsCount = stream.readByte();
            this.modifications = new java.util.ArrayList<>(modificationsCount);
            for (int i = 0; i < modificationsCount; i++) {
                modifications.add(CosignatoryModificationBuilder.loadFromBinary(stream));
            }
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param minRemovalDelta Relative change of the minimal number of cosignatories required when removing an account.
     * @param minApprovalDelta Relative change of the minimal number of cosignatories required when approving a transaction.
     * @param modifications Attached cosignatory modifications.
     */
    protected MultisigAccountModificationTransactionBodyBuilder(final byte minRemovalDelta, final byte minApprovalDelta, final ArrayList<CosignatoryModificationBuilder> modifications) {
        GeneratorUtils.notNull(modifications, "modifications is null");
        this.minRemovalDelta = minRemovalDelta;
        this.minApprovalDelta = minApprovalDelta;
        this.modifications = modifications;
    }

    /**
     * Creates an instance of MultisigAccountModificationTransactionBodyBuilder.
     *
     * @param minRemovalDelta Relative change of the minimal number of cosignatories required when removing an account.
     * @param minApprovalDelta Relative change of the minimal number of cosignatories required when approving a transaction.
     * @param modifications Attached cosignatory modifications.
     * @return Instance of MultisigAccountModificationTransactionBodyBuilder.
     */
    public static MultisigAccountModificationTransactionBodyBuilder create(final byte minRemovalDelta, final byte minApprovalDelta, final ArrayList<CosignatoryModificationBuilder> modifications) {
        return new MultisigAccountModificationTransactionBodyBuilder(minRemovalDelta, minApprovalDelta, modifications);
    }

    /**
     * Gets relative change of the minimal number of cosignatories required when removing an account.
     *
     * @return Relative change of the minimal number of cosignatories required when removing an account.
     */
    public byte getMinRemovalDelta() {
        return this.minRemovalDelta;
    }

    /**
     * Gets relative change of the minimal number of cosignatories required when approving a transaction.
     *
     * @return Relative change of the minimal number of cosignatories required when approving a transaction.
     */
    public byte getMinApprovalDelta() {
        return this.minApprovalDelta;
    }

    /**
     * Gets attached cosignatory modifications.
     *
     * @return Attached cosignatory modifications.
     */
    public ArrayList<CosignatoryModificationBuilder> getModifications() {
        return this.modifications;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += 1; // minRemovalDelta
        size += 1; // minApprovalDelta
        size += 1; // modificationsCount
        size += this.modifications.stream().mapToInt(o -> o.getSize()).sum();
        return size;
    }

    /**
     * Creates an instance of MultisigAccountModificationTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MultisigAccountModificationTransactionBodyBuilder.
     */
    public static MultisigAccountModificationTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new MultisigAccountModificationTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeByte(this.getMinRemovalDelta());
            dataOutputStream.writeByte(this.getMinApprovalDelta());
            dataOutputStream.writeByte((byte) this.modifications.size());
            for (int i = 0; i < this.modifications.size(); i++) {
                final byte[] modificationsBytes = this.modifications.get(i).serialize();
                dataOutputStream.write(modificationsBytes, 0, modificationsBytes.length);
            }
        });
    }
}
