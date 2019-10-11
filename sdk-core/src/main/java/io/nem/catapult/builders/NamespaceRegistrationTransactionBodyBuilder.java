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
import java.nio.ByteBuffer;

/** Binary layout for a namespace registration transaction. */
public final class NamespaceRegistrationTransactionBodyBuilder {
    /** Namespace registration type. */
    private final NamespaceRegistrationTypeDto registrationType;
    /** Namespace duration. */
    private BlockDurationDto duration;
    /** Parent namespace identifier. */
    private NamespaceIdDto parentId;
    /** Namespace identifier. */
    private final NamespaceIdDto id;
    /** Namespace name. */
    private final ByteBuffer name;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected NamespaceRegistrationTransactionBodyBuilder(final DataInput stream) {
        try {
            this.registrationType = NamespaceRegistrationTypeDto.loadFromBinary(stream);
            if (this.registrationType == NamespaceRegistrationTypeDto.ROOT) {
                this.duration = BlockDurationDto.loadFromBinary(stream);
                this.parentId = null;
            }
            if (this.registrationType == NamespaceRegistrationTypeDto.CHILD) {
                this.parentId = NamespaceIdDto.loadFromBinary(stream);
                this.duration = null;
            }
            this.id = NamespaceIdDto.loadFromBinary(stream);
            final byte nameSize = stream.readByte();
            this.name = ByteBuffer.allocate(nameSize);
            stream.readFully(this.name.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param duration Namespace duration.
     * @param id Namespace identifier.
     * @param name Namespace name.
     */
    protected NamespaceRegistrationTransactionBodyBuilder(final BlockDurationDto duration, final NamespaceIdDto id, final ByteBuffer name) {
        GeneratorUtils.notNull(duration, "duration is null");
        GeneratorUtils.notNull(id, "id is null");
        GeneratorUtils.notNull(name, "name is null");
        this.duration = duration;
        this.id = id;
        this.name = name;
        this.registrationType = NamespaceRegistrationTypeDto.ROOT;
        this.parentId = null;
    }

    /**
     * Constructor.
     *
     * @param parentId Parent namespace identifier.
     * @param id Namespace identifier.
     * @param name Namespace name.
     */
    protected NamespaceRegistrationTransactionBodyBuilder(final NamespaceIdDto parentId, final NamespaceIdDto id, final ByteBuffer name) {
        GeneratorUtils.notNull(parentId, "parentId is null");
        GeneratorUtils.notNull(id, "id is null");
        GeneratorUtils.notNull(name, "name is null");
        this.parentId = parentId;
        this.id = id;
        this.name = name;
        this.registrationType = NamespaceRegistrationTypeDto.CHILD;
        this.duration = null;
    }

    /**
     * Creates an instance of NamespaceRegistrationTransactionBodyBuilder.
     *
     * @param duration Namespace duration.
     * @param id Namespace identifier.
     * @param name Namespace name.
     * @return Instance of NamespaceRegistrationTransactionBodyBuilder.
     */
    public static NamespaceRegistrationTransactionBodyBuilder create(final BlockDurationDto duration, final NamespaceIdDto id, final ByteBuffer name) {
        return new NamespaceRegistrationTransactionBodyBuilder(duration, id, name);
    }

    /**
     * Creates an instance of NamespaceRegistrationTransactionBodyBuilder.
     *
     * @param parentId Parent namespace identifier.
     * @param id Namespace identifier.
     * @param name Namespace name.
     * @return Instance of NamespaceRegistrationTransactionBodyBuilder.
     */
    public static NamespaceRegistrationTransactionBodyBuilder create(final NamespaceIdDto parentId, final NamespaceIdDto id, final ByteBuffer name) {
        return new NamespaceRegistrationTransactionBodyBuilder(parentId, id, name);
    }

    /**
     * Gets namespace registration type.
     *
     * @return Namespace registration type.
     */
    public NamespaceRegistrationTypeDto getRegistrationType() {
        return this.registrationType;
    }

    /**
     * Gets namespace duration.
     *
     * @return Namespace duration.
     */
    public BlockDurationDto getDuration() {
        if (this.registrationType != NamespaceRegistrationTypeDto.ROOT) {
            throw new java.lang.IllegalStateException("registrationType is not set to ROOT.");
        }
        return this.duration;
    }

    /**
     * Gets parent namespace identifier.
     *
     * @return Parent namespace identifier.
     */
    public NamespaceIdDto getParentId() {
        if (this.registrationType != NamespaceRegistrationTypeDto.CHILD) {
            throw new java.lang.IllegalStateException("registrationType is not set to CHILD.");
        }
        return this.parentId;
    }

    /**
     * Gets namespace identifier.
     *
     * @return Namespace identifier.
     */
    public NamespaceIdDto getId() {
        return this.id;
    }

    /**
     * Gets namespace name.
     *
     * @return Namespace name.
     */
    public ByteBuffer getName() {
        return this.name;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.registrationType.getSize();
        if (this.registrationType == NamespaceRegistrationTypeDto.ROOT) {
            size += this.duration.getSize();
        }
        if (this.registrationType == NamespaceRegistrationTypeDto.CHILD) {
            size += this.parentId.getSize();
        }
        size += this.id.getSize();
        size += 1; // nameSize
        size += this.name.array().length;
        return size;
    }

    /**
     * Creates an instance of NamespaceRegistrationTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of NamespaceRegistrationTransactionBodyBuilder.
     */
    public static NamespaceRegistrationTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new NamespaceRegistrationTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] registrationTypeBytes = this.registrationType.serialize();
            dataOutputStream.write(registrationTypeBytes, 0, registrationTypeBytes.length);
            if (this.registrationType == NamespaceRegistrationTypeDto.ROOT) {
                final byte[] durationBytes = this.duration.serialize();
                dataOutputStream.write(durationBytes, 0, durationBytes.length);
            }
            if (this.registrationType == NamespaceRegistrationTypeDto.CHILD) {
                final byte[] parentIdBytes = this.parentId.serialize();
                dataOutputStream.write(parentIdBytes, 0, parentIdBytes.length);
            }
            final byte[] idBytes = this.id.serialize();
            dataOutputStream.write(idBytes, 0, idBytes.length);
            dataOutputStream.writeByte((byte) this.name.array().length);
            dataOutputStream.write(this.name.array(), 0, this.name.array().length);
        });
    }
}
