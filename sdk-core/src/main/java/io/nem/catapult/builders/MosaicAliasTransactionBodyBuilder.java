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

/** Binary layout for an mosaic alias transaction. */
public final class MosaicAliasTransactionBodyBuilder {
    /** Alias action. */
    private final AliasActionDto aliasAction;
    /** Identifier of the namespace that will become an alias. */
    private final NamespaceIdDto namespaceId;
    /** Aliased mosaic identifier. */
    private final MosaicIdDto mosaicId;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicAliasTransactionBodyBuilder(final DataInput stream) {
        this.aliasAction = AliasActionDto.loadFromBinary(stream);
        this.namespaceId = NamespaceIdDto.loadFromBinary(stream);
        this.mosaicId = MosaicIdDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param aliasAction Alias action.
     * @param namespaceId Identifier of the namespace that will become an alias.
     * @param mosaicId Aliased mosaic identifier.
     */
    protected MosaicAliasTransactionBodyBuilder(final AliasActionDto aliasAction, final NamespaceIdDto namespaceId, final MosaicIdDto mosaicId) {
        GeneratorUtils.notNull(aliasAction, "aliasAction is null");
        GeneratorUtils.notNull(namespaceId, "namespaceId is null");
        GeneratorUtils.notNull(mosaicId, "mosaicId is null");
        this.aliasAction = aliasAction;
        this.namespaceId = namespaceId;
        this.mosaicId = mosaicId;
    }

    /**
     * Creates an instance of MosaicAliasTransactionBodyBuilder.
     *
     * @param aliasAction Alias action.
     * @param namespaceId Identifier of the namespace that will become an alias.
     * @param mosaicId Aliased mosaic identifier.
     * @return Instance of MosaicAliasTransactionBodyBuilder.
     */
    public static MosaicAliasTransactionBodyBuilder create(final AliasActionDto aliasAction, final NamespaceIdDto namespaceId, final MosaicIdDto mosaicId) {
        return new MosaicAliasTransactionBodyBuilder(aliasAction, namespaceId, mosaicId);
    }

    /**
     * Gets alias action.
     *
     * @return Alias action.
     */
    public AliasActionDto getAliasAction() {
        return this.aliasAction;
    }

    /**
     * Gets identifier of the namespace that will become an alias.
     *
     * @return Identifier of the namespace that will become an alias.
     */
    public NamespaceIdDto getNamespaceId() {
        return this.namespaceId;
    }

    /**
     * Gets aliased mosaic identifier.
     *
     * @return Aliased mosaic identifier.
     */
    public MosaicIdDto getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.aliasAction.getSize();
        size += this.namespaceId.getSize();
        size += this.mosaicId.getSize();
        return size;
    }

    /**
     * Creates an instance of MosaicAliasTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicAliasTransactionBodyBuilder.
     */
    public static MosaicAliasTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new MosaicAliasTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] aliasActionBytes = this.aliasAction.serialize();
            dataOutputStream.write(aliasActionBytes, 0, aliasActionBytes.length);
            final byte[] namespaceIdBytes = this.namespaceId.serialize();
            dataOutputStream.write(namespaceIdBytes, 0, namespaceIdBytes.length);
            final byte[] mosaicIdBytes = this.mosaicId.serialize();
            dataOutputStream.write(mosaicIdBytes, 0, mosaicIdBytes.length);
        });
    }
}
