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

import io.nem.catapult.builders.AddressAliasTransactionBuilder;
import io.nem.catapult.builders.AddressDto;
import io.nem.catapult.builders.AliasActionDto;
import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedAddressAliasTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.NamespaceIdDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import java.nio.ByteBuffer;

/**
 * Address alias transaction.
 */
public class AddressAliasTransaction extends Transaction {

    private final AliasAction aliasAction;
    private final NamespaceId namespaceId;
    private final Address address;

    /**
     * Protected constructor. Use the factory to create a transaction.
     */
    AddressAliasTransaction(AddressAliasTransactionFactory factory) {
        super(factory);
        this.aliasAction = factory.getAliasAction();
        this.namespaceId = factory.getNamespaceId();
        this.address = factory.getAddress();
    }

    /**
     * Gets the alias action.
     *
     * @return Alias Action.
     */
    public AliasAction getAliasAction() {
        return this.aliasAction;
    }

    /**
     * Gets the namespace id.
     *
     * @return Namespace id.
     */
    public NamespaceId getNamespaceId() {
        return this.namespaceId;
    }

    /**
     * Gets the address.
     *
     * @return Address of the account.
     */
    public Address getAddress() {
        return this.address;
    }

    /**
     * Serializes the transaction using catbuffer.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        final AddressAliasTransactionBuilder txBuilder =
            AddressAliasTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                AliasActionDto.rawValueOf(getAliasAction().getValue()),
                new NamespaceIdDto(getNamespaceId().getIdAsLong()),
                new AddressDto(SerializationUtils.fromUnresolvedAddressToByteBuffer(getAddress())));
        return txBuilder.serialize();
    }

    /**
     * Serialized the transaction to embedded bytes.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateEmbeddedBytes() {
        final EmbeddedAddressAliasTransactionBuilder txBuilder =
            EmbeddedAddressAliasTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                AliasActionDto.rawValueOf(getAliasAction().getValue()),
                new NamespaceIdDto(getNamespaceId().getIdAsLong()),
                new AddressDto(SerializationUtils.fromUnresolvedAddressToByteBuffer(getAddress())));
        return txBuilder.serialize();
    }
}
