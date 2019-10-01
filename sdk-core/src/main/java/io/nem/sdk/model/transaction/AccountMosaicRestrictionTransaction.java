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

import io.nem.catapult.builders.AccountMosaicRestrictionModificationBuilder;
import io.nem.catapult.builders.AccountMosaicRestrictionTransactionBuilder;
import io.nem.catapult.builders.AccountRestrictionModificationActionDto;
import io.nem.catapult.builders.AccountRestrictionTypeDto;
import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedAccountMosaicRestrictionTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.mosaic.MosaicId;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AccountMosaicRestrictionTransaction extends Transaction {

    private final AccountRestrictionType restrictionType;
    private final List<AccountRestrictionModification<MosaicId>> modifications;

    public AccountMosaicRestrictionTransaction(
        AccountMosaicRestrictionTransactionFactory factory) {
        super(factory);
        this.restrictionType = factory.getRestrictionType();
        this.modifications = factory.getModifications();
    }

    /**
     * Get account restriction type
     *
     * @return {@link AccountRestrictionType}
     */
    public AccountRestrictionType getRestrictionType() {
        return this.restrictionType;
    }

    /**
     * Get account mosaic restriction modifications
     *
     * @return list of {@link AccountRestrictionModification} with {@link MosaicId}
     */
    public List<AccountRestrictionModification<MosaicId>> getModifications() {
        return this.modifications;
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

        AccountMosaicRestrictionTransactionBuilder txBuilder =
            AccountMosaicRestrictionTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                AccountRestrictionTypeDto.rawValueOf((byte) this.restrictionType.getValue()),
                getModificationBuilder());
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    byte[] generateEmbeddedBytes() {
        EmbeddedAccountMosaicRestrictionTransactionBuilder txBuilder =
            EmbeddedAccountMosaicRestrictionTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                AccountRestrictionTypeDto.rawValueOf((byte) this.restrictionType.getValue()),
                getModificationBuilder());
        return txBuilder.serialize();
    }

    /**
     * Gets account restriction modification.
     *
     * @return account restriction modification.
     */
    private ArrayList<AccountMosaicRestrictionModificationBuilder> getModificationBuilder() {
        final ArrayList<AccountMosaicRestrictionModificationBuilder> modificationBuilder =
            new ArrayList<>(modifications.size());
        for (AccountRestrictionModification<MosaicId> accountRestrictionModification : modifications) {
            final AccountMosaicRestrictionModificationBuilder builder =
                AccountMosaicRestrictionModificationBuilder.create(
                    AccountRestrictionModificationActionDto.rawValueOf(
                        accountRestrictionModification.getModificationAction().getValue()),
                    new UnresolvedMosaicIdDto(
                        accountRestrictionModification.getValue().getIdAsLong()));
            modificationBuilder.add(builder);
        }
        return modificationBuilder;
    }
}
