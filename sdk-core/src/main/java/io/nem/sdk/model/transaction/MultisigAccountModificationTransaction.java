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
import io.nem.catapult.builders.CosignatoryModificationActionDto;
import io.nem.catapult.builders.CosignatoryModificationBuilder;
import io.nem.catapult.builders.EmbeddedMultisigAccountModificationTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MultisigAccountModificationTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Modify multisig account transactions are part of the NEM's multisig account system. A modify
 * multisig account transaction holds an array of multisig cosignatory modifications, min number of
 * signatures to approve a transaction and a min number of signatures to remove a cosignatory.
 *
 * @since 1.0
 */
public class MultisigAccountModificationTransaction extends Transaction {

    private final int minApprovalDelta;
    private final int minRemovalDelta;
    private final List<MultisigCosignatoryModification> modifications;

    public MultisigAccountModificationTransaction(
        MultisigAccountModificationTransactionFactory factory) {
        super(factory);
        this.minApprovalDelta = factory.getMinApprovalDelta();
        this.minRemovalDelta = factory.getMinRemovalDelta();
        this.modifications = factory.getModifications();
    }

    /**
     * Return number of signatures needed to approve a transaction. If we are modifying and existing
     * multi-signature account this indicates the relative change of the minimum cosignatories.
     *
     * @return byte
     */
    public int getMinApprovalDelta() {
        return minApprovalDelta;
    }

    /**
     * Return number of signatures needed to remove a cosignatory. If we are modifying and existing
     * multi-signature account this indicates the relative change of the minimum cosignatories.
     *
     * @return byte
     */
    public int getMinRemovalDelta() {
        return minRemovalDelta;
    }

    /**
     * The List of cosigner accounts added or removed from the multi-signature account.
     *
     * @return {@link List} of { @ link MultisigCosignatoryModification }
     */
    public List<MultisigCosignatoryModification> getModifications() {
        return modifications;
    }

    /**
     * Serialized the transaction.
     *
     * @return bytes of the transaction.
     */
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        MultisigAccountModificationTransactionBuilder txBuilder =
            MultisigAccountModificationTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                (byte) getMinRemovalDelta(),
                (byte) getMinApprovalDelta(),
                getModificationBuilder());
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    byte[] generateEmbeddedBytes() {
        EmbeddedMultisigAccountModificationTransactionBuilder txBuilder =
            EmbeddedMultisigAccountModificationTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                (byte) getMinRemovalDelta(),
                (byte) getMinApprovalDelta(),
                getModificationBuilder());
        return txBuilder.serialize();
    }

    /**
     * Gets cosignatory modification.
     *
     * @return Cosignatory modification.
     */
    private ArrayList<CosignatoryModificationBuilder> getModificationBuilder() {
        final ArrayList<CosignatoryModificationBuilder> modificationBuilder =
            new ArrayList<>(modifications.size());
        for (MultisigCosignatoryModification multisigCosignatoryModification : modifications) {
            final byte[] byteCosignatoryPublicKey =
                multisigCosignatoryModification.getCosignatoryPublicAccount().getPublicKey()
                    .getBytes();
            final ByteBuffer keyBuffer = ByteBuffer.wrap(byteCosignatoryPublicKey);
            final CosignatoryModificationBuilder cosignatoryModificationBuilder =
                CosignatoryModificationBuilder.create(
                    CosignatoryModificationActionDto.rawValueOf(
                        (byte) multisigCosignatoryModification.getModificationAction().getValue()),
                    new KeyDto(keyBuffer));
            modificationBuilder.add(cosignatoryModificationBuilder);
        }
        return modificationBuilder;
    }
}
