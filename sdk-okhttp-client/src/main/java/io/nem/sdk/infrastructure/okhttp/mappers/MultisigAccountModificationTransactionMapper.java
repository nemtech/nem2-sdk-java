/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.infrastructure.okhttp.mappers;

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.sdk.model.transaction.MultisigCosignatoryModification;
import io.nem.sdk.model.transaction.CosignatoryModificationActionType;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.MultisigAccountModificationTransactionDTO;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Multisig account modification transaction napper.
 */
class MultisigAccountModificationTransactionMapper extends
    AbstractTransactionMapper<MultisigAccountModificationTransactionDTO, MultisigAccountModificationTransaction> {

    public MultisigAccountModificationTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.MODIFY_MULTISIG_ACCOUNT,
            MultisigAccountModificationTransactionDTO.class);
    }

    @Override
    protected MultisigAccountModificationTransactionFactory createFactory(
        NetworkType networkType, MultisigAccountModificationTransactionDTO transaction) {

        List<MultisigCosignatoryModification> modifications =
            transaction.getModifications() == null ? Collections.emptyList()
                : transaction.getModifications().stream()
                    .map(
                        multisigModification ->
                            new MultisigCosignatoryModification(
                                CosignatoryModificationActionType.rawValueOf(
                                    multisigModification.getModificationAction().getValue()),
                                PublicAccount.createFromPublicKey(
                                    multisigModification.getCosignatoryPublicKey(),
                                    networkType)))
                    .collect(Collectors.toList());

        return MultisigAccountModificationTransactionFactory.create(
            networkType,
            transaction.getMinApprovalDelta().byteValue(),
            transaction.getMinRemovalDelta().byteValue(),
            modifications);
    }


}
