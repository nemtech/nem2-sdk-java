/*
 * Copyright 2019 NEM
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

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.BlockDurationDto;
import io.nem.catapult.builders.EmbeddedNamespaceRegistrationTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.NamespaceIdDto;
import io.nem.catapult.builders.NamespaceRegistrationTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.core.utils.StringEncoder;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Accounts can rent a namespace for an amount of blocks and after a this renew the contract. This
 * is done via a NamespaceRegistrationTransaction.
 *
 * @since 1.0
 */
public class NamespaceRegistrationTransaction extends Transaction {

    private final String namespaceName;
    private final NamespaceId namespaceId;
    private final Optional<BigInteger> duration;
    private final Optional<NamespaceId> parentId;
    private final NamespaceRegistrationType namespaceRegistrationType;

    /**
     * @param factory the factory with the configured fields.
     */
    NamespaceRegistrationTransaction(NamespaceRegistrationTransactionFactory factory) {
        super(factory);
        this.namespaceName = factory.getNamespaceName();
        this.namespaceRegistrationType = factory.getNamespaceRegistrationType();
        this.namespaceId = factory.getNamespaceId();
        this.duration = factory.getDuration();
        this.parentId = factory.getParentId();
    }

    /**
     * Returns namespace name.
     *
     * @return namespace name
     */
    public String getNamespaceName() {
        return namespaceName;
    }

    /**
     * Returns id of the namespace derived from namespaceName. When creating a sub namespace the
     * namespaceId is derived from namespaceName and parentId.
     *
     * @return namespace id
     */
    public NamespaceId getNamespaceId() {
        return namespaceId;
    }

    /**
     * Returns number of blocks a namespace is active.
     *
     * @return namespace renting duration
     */
    public Optional<BigInteger> getDuration() {
        return duration;
    }

    /**
     * The id of the parent sub namespace.
     *
     * @return sub namespace
     */
    public Optional<NamespaceId> getParentId() {
        return parentId;
    }

    /**
     * Returns namespace type either RootNamespace or SubNamespace.
     *
     * @return namespace type
     */
    public NamespaceRegistrationType getNamespaceRegistrationType() {
        return namespaceRegistrationType;
    }

    /**
     * Gets the serialized bytes.
     *
     * @return Serialized bytes
     */
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        NamespaceRegistrationTransactionBuilder txBuilder;
        if (namespaceRegistrationType == NamespaceRegistrationType.ROOT_NAMESPACE) {
            txBuilder =
                NamespaceRegistrationTransactionBuilder.create(
                    new SignatureDto(signatureBuffer),
                    new KeyDto(signerBuffer),
                    getNetworkVersion(),
                    getEntityTypeDto(),
                    new AmountDto(getMaxFee().longValue()),
                    new TimestampDto(getDeadline().getInstant()),
                    new BlockDurationDto(getDuration()
                        .orElseThrow(() -> new IllegalStateException("Duration is required"))
                        .longValue()),
                    new NamespaceIdDto(getNamespaceId().getId().longValue()),
                    getNameBuffer());

        } else {
            txBuilder =
                NamespaceRegistrationTransactionBuilder.create(
                    new SignatureDto(signatureBuffer),
                    new KeyDto(signerBuffer),
                    getNetworkVersion(),
                    getEntityTypeDto(),
                    new AmountDto(getMaxFee().longValue()),
                    new TimestampDto(getDeadline().getInstant()),
                    new NamespaceIdDto(getParentId()
                        .orElseThrow(() -> new IllegalStateException("ParentId is required"))
                        .getId().longValue()),
                    new NamespaceIdDto(getNamespaceId().getId().longValue()),
                    getNameBuffer());
        }
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    byte[] generateEmbeddedBytes() {
        EmbeddedNamespaceRegistrationTransactionBuilder txBuilder;
        if (namespaceRegistrationType == NamespaceRegistrationType.ROOT_NAMESPACE) {
            txBuilder =
                EmbeddedNamespaceRegistrationTransactionBuilder.create(
                    new KeyDto(getRequiredSignerBytes()),
                    getNetworkVersion(),
                    getEntityTypeDto(),
                    new BlockDurationDto(getDuration()
                        .orElseThrow(() -> new IllegalStateException("Duration is required"))
                        .longValue()),
                    new NamespaceIdDto(getNamespaceId().getId().longValue()),
                    getNameBuffer());

        } else {
            txBuilder =
                EmbeddedNamespaceRegistrationTransactionBuilder.create(
                    new KeyDto(getRequiredSignerBytes()),
                    getNetworkVersion(),
                    getEntityTypeDto(),
                    new NamespaceIdDto(getParentId()
                        .orElseThrow(() -> new IllegalStateException("ParentId is required"))
                        .getId().longValue()),
                    new NamespaceIdDto(getNamespaceId().getId().longValue()),
                    getNameBuffer());
        }
        return txBuilder.serialize();
    }

    /**
     * Gets namespace name buffer.
     *
     * @return Name buffer.
     */
    private ByteBuffer getNameBuffer() {
        return ByteBuffer.wrap(StringEncoder.getBytes(namespaceName));
    }
}
