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

package io.nem.sdk.infrastructure.vertx.mappers;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigCosignatoryModification;
import io.nem.sdk.model.transaction.MultisigCosignatoryModificationType;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.MultisigAccountModificationTransactionDTO;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class MultisigAccountModificationTransactionMapper extends
    AbstractTransactionMapper<MultisigAccountModificationTransactionDTO> {

    public MultisigAccountModificationTransactionMapper(JsonHelper jsonHelper,
        SignSchema signSchema) {
        super(jsonHelper, TransactionType.MODIFY_MULTISIG_ACCOUNT,
            MultisigAccountModificationTransactionDTO.class, signSchema
        );
    }

    @Override
    protected Transaction basicMap(TransactionInfo transactionInfo,
        MultisigAccountModificationTransactionDTO transaction) {
        Deadline deadline = new Deadline(transaction.getDeadline());
        NetworkType networkType = extractNetworkType(transaction.getVersion());

        List<MultisigCosignatoryModification> modifications =
            transaction.getModifications() == null ? Collections.emptyList()
                : transaction.getModifications().stream()
                    .map(
                        multisigModification ->
                            new MultisigCosignatoryModification(
                                MultisigCosignatoryModificationType.rawValueOf(
                                    multisigModification.getModificationAction().getValue()),
                                PublicAccount.createFromPublicKey(
                                    multisigModification.getCosignatoryPublicKey(),
                                    networkType, getSignSchema())))
                    .collect(Collectors.toList());

        return new MultisigAccountModificationTransaction(
            networkType,
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            transaction.getMaxFee(),
            transaction.getMinApprovalDelta().byteValue(),
            transaction.getMinRemovalDelta().byteValue(),
            modifications,
            transaction.getSignature(),
            new PublicAccount(transaction.getSignerPublicKey(), networkType, getSignSchema()),
            transactionInfo);
    }
}
