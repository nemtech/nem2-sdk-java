/*
 * Copyright 2020 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.symbol.sdk.model.receipt;

import io.nem.symbol.catapult.builders.MosaicExpiryReceiptBuilder;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.NamespaceExpiryReceiptBuilder;
import io.nem.symbol.catapult.builders.NamespaceIdDto;
import io.nem.symbol.catapult.builders.ReceiptTypeDto;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.util.Optional;

public class ArtifactExpiryReceipt<T> extends Receipt {

    private final T artifactId;

    /**
     * Constructor ArtifactExpiryReceipt
     *
     * @param artifactId (MosaicId or NamespaceId)
     * @param type Receipt Type
     * @param version Receipt Version
     * @param size Receipt Size
     */
    public ArtifactExpiryReceipt(
        T artifactId, ReceiptType type, ReceiptVersion version, Optional<Integer> size) {
        super(type, version, size);
        this.artifactId = artifactId;
        this.validateArtifactType();
        this.validateReceiptType(type);
    }

    /**
     * Constructor ArtifactExpiryReceipt
     *
     * @param artifactId (MosaicId or NamespaceId)
     * @param type Receipt Type
     * @param version Receipt Version
     */
    public ArtifactExpiryReceipt(T artifactId, ReceiptType type, ReceiptVersion version) {
        super(type, version, Optional.empty());
        this.artifactId = artifactId;
        this.validateArtifactType();
        this.validateReceiptType(type);
    }

    /**
     * Returns the artifact id
     *
     * @return artifact id (MosaicId | NamespaceId)
     */
    public T getArtifactId() {
        return this.artifactId;
    }

    /**
     * Validate artifact type (MosaicId | NamespaceId)
     *
     * @return void
     */
    private void validateArtifactType() {
        Class artifactClass = this.artifactId.getClass();
        if (!MosaicId.class.isAssignableFrom(artifactClass)
            && !NamespaceId.class.isAssignableFrom(artifactClass)) {
            throw new IllegalArgumentException(
                "Artifact type: ["
                    + artifactClass.getName()
                    + "] is not valid for ArtifactExpiryReceipt");
        }
    }

    /**
     * Serialize receipt and returns receipt bytes
     *
     * @return receipt bytes
     */
    public byte[] serialize() {
        short version = (short) getVersion().getValue();
        ReceiptTypeDto type = ReceiptTypeDto.rawValueOf((short) getType().getValue());
        if (this.artifactId instanceof MosaicId) {
            return MosaicExpiryReceiptBuilder
                .create(version, type, new MosaicIdDto(getArtifactIdValue().longValue()))
                .serialize();
        } else {
            return NamespaceExpiryReceiptBuilder
                .create(version, type, new NamespaceIdDto(getArtifactIdValue().longValue()))
                .serialize();
        }
    }

    /**
     * Validate receipt type
     *
     * @return void
     */
    private void validateReceiptType(ReceiptType type) {
        if (!ReceiptType.ARTIFACT_EXPIRY.contains(type)) {
            throw new IllegalArgumentException("Receipt type: [" + type.name() + "] is not valid.");
        }
    }

    /**
     * Return typed artifact Id value
     *
     * @return typed artifact Id value
     */
    private BigInteger getArtifactIdValue() {
        Class artifactClass = this.artifactId.getClass();
        if (MosaicId.class.isAssignableFrom(artifactClass)) {
            return ((MosaicId) this.artifactId).getId();
        }
        return ((NamespaceId) this.artifactId).getId();
    }
}
