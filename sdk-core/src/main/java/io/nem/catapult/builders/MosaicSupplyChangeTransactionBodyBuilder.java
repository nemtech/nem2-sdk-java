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

/** Binary layout for a mosaic supply change transaction. */
public final class MosaicSupplyChangeTransactionBodyBuilder {
    /** Affected mosaic identifier. */
    private final UnresolvedMosaicIdDto mosaicId;
    /** Supply change action. */
    private final MosaicSupplyChangeActionDto action;
    /** Change amount. */
    private final AmountDto delta;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicSupplyChangeTransactionBodyBuilder(final DataInput stream) {
        this.mosaicId = UnresolvedMosaicIdDto.loadFromBinary(stream);
        this.action = MosaicSupplyChangeActionDto.loadFromBinary(stream);
        this.delta = AmountDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param mosaicId Affected mosaic identifier.
     * @param action Supply change action.
     * @param delta Change amount.
     */
    protected MosaicSupplyChangeTransactionBodyBuilder(final UnresolvedMosaicIdDto mosaicId, final MosaicSupplyChangeActionDto action, final AmountDto delta) {
        GeneratorUtils.notNull(mosaicId, "mosaicId is null");
        GeneratorUtils.notNull(action, "action is null");
        GeneratorUtils.notNull(delta, "delta is null");
        this.mosaicId = mosaicId;
        this.action = action;
        this.delta = delta;
    }

    /**
     * Creates an instance of MosaicSupplyChangeTransactionBodyBuilder.
     *
     * @param mosaicId Affected mosaic identifier.
     * @param action Supply change action.
     * @param delta Change amount.
     * @return Instance of MosaicSupplyChangeTransactionBodyBuilder.
     */
    public static MosaicSupplyChangeTransactionBodyBuilder create(final UnresolvedMosaicIdDto mosaicId, final MosaicSupplyChangeActionDto action, final AmountDto delta) {
        return new MosaicSupplyChangeTransactionBodyBuilder(mosaicId, action, delta);
    }

    /**
     * Gets affected mosaic identifier.
     *
     * @return Affected mosaic identifier.
     */
    public UnresolvedMosaicIdDto getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Gets supply change action.
     *
     * @return Supply change action.
     */
    public MosaicSupplyChangeActionDto getAction() {
        return this.action;
    }

    /**
     * Gets change amount.
     *
     * @return Change amount.
     */
    public AmountDto getDelta() {
        return this.delta;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.mosaicId.getSize();
        size += this.action.getSize();
        size += this.delta.getSize();
        return size;
    }

    /**
     * Creates an instance of MosaicSupplyChangeTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicSupplyChangeTransactionBodyBuilder.
     */
    public static MosaicSupplyChangeTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new MosaicSupplyChangeTransactionBodyBuilder(stream);
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
            final byte[] actionBytes = this.action.serialize();
            dataOutputStream.write(actionBytes, 0, actionBytes.length);
            final byte[] deltaBytes = this.delta.serialize();
            dataOutputStream.write(deltaBytes, 0, deltaBytes.length);
        });
    }
}
