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

import static io.nem.core.utils.MapperUtils.toNamespaceId;

import io.nem.core.crypto.SignSchema;
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceRegistrationTransactionDTO;
import java.util.Optional;

class NamespaceRegistrationTransactionMapper extends
    AbstractTransactionMapper<NamespaceRegistrationTransactionDTO> {

    public NamespaceRegistrationTransactionMapper(JsonHelper jsonHelper,
        SignSchema signSchema) {
        super(jsonHelper, TransactionType.REGISTER_NAMESPACE,
            NamespaceRegistrationTransactionDTO.class, signSchema);
    }

    @Override
    protected Transaction basicMap(TransactionInfo transactionInfo,
        NamespaceRegistrationTransactionDTO transaction) {

        Deadline deadline = new Deadline(transaction.getDeadline());
        NamespaceType namespaceType = NamespaceType
            .rawValueOf(transaction.getRegistrationType().getValue());

        return new NamespaceRegistrationTransaction(
            extractNetworkType(transaction.getVersion()),
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            transaction.getMaxFee(),
            transaction.getName(),
            toNamespaceId(transaction.getId()),
            namespaceType,
            namespaceType == NamespaceType.ROOT_NAMESPACE
                ? Optional.of(transaction.getDuration())
                : Optional.empty(),
            namespaceType == NamespaceType.SUB_NAMESPACE
                ? Optional
                .of(MapperUtils.toNamespaceId(transaction.getParentId()))
                : Optional.empty(),
            transaction.getSignature(),
            new PublicAccount(
                transaction.getSignerPublicKey(),
                extractNetworkType(transaction.getVersion()), getSignSchema()),
            transactionInfo);
    }
}
