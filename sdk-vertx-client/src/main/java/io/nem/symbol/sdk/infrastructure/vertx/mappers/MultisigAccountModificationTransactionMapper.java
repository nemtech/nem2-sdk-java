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

package io.nem.symbol.sdk.infrastructure.vertx.mappers;

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.MultisigAccountModificationTransactionDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Multisig account modification transaction mapper.
 */
class MultisigAccountModificationTransactionMapper extends
    AbstractTransactionMapper<MultisigAccountModificationTransactionDTO, MultisigAccountModificationTransaction> {

    public MultisigAccountModificationTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.MULTISIG_ACCOUNT_MODIFICATION,
            MultisigAccountModificationTransactionDTO.class);
    }

    @Override
    protected MultisigAccountModificationTransactionFactory createFactory(
        NetworkType networkType, MultisigAccountModificationTransactionDTO transaction) {
        List<PublicAccount> additions = transaction.getPublicKeyAdditions().stream()
            .map(publicKey -> PublicAccount.createFromPublicKey(
                publicKey,
                networkType)).collect(Collectors.toList());
        List<PublicAccount> deletions = transaction.getPublicKeyDeletions().stream()
            .map(publicKey -> PublicAccount.createFromPublicKey(
                publicKey,
                networkType)).collect(Collectors.toList());
        return MultisigAccountModificationTransactionFactory.create(
            networkType,
            transaction.getMinApprovalDelta().byteValue(),
            transaction.getMinRemovalDelta().byteValue(),
            additions, deletions);
    }

    @Override
    protected void copyToDto(MultisigAccountModificationTransaction transaction,
        MultisigAccountModificationTransactionDTO dto) {
        dto.setMinApprovalDelta((int) transaction.getMinApprovalDelta());
        dto.setMinRemovalDelta((int) transaction.getMinRemovalDelta());
        dto.setPublicKeyAdditions(
            transaction.getPublicKeyAdditions().stream().map(p -> p.getPublicKey().toHex())
                .collect(Collectors.toList()));

        dto.setPublicKeyDeletions(
            transaction.getPublicKeyDeletions().stream().map(p -> p.getPublicKey().toHex())
                .collect(Collectors.toList()));
    }


}
