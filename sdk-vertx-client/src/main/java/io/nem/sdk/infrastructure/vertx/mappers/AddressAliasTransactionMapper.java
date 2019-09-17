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
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.AddressAliasTransactionDTO;
import java.util.Optional;

class AddressAliasTransactionMapper extends
    AbstractTransactionMapper<AddressAliasTransactionDTO> {


    public AddressAliasTransactionMapper(JsonHelper jsonHelper, SignSchema signSchema) {
        super(jsonHelper, TransactionType.ADDRESS_ALIAS, AddressAliasTransactionDTO.class,
            signSchema
        );
    }

    @Override
    protected Transaction basicMap(TransactionInfo transactionInfo,
        AddressAliasTransactionDTO transaction) {
        NamespaceId namespaceId = MapperUtils.toNamespaceId(transaction.getNamespaceId());
        Deadline deadline = new Deadline(transaction.getDeadline());
        NetworkType networkType = extractNetworkType(transaction.getVersion());
        AliasAction aliasAction = AliasAction
            .rawValueOf(transaction.getAliasAction().getValue().byteValue());
        return new AddressAliasTransaction(
            networkType,
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            transaction.getMaxFee(),
            aliasAction,
            namespaceId,
            MapperUtils.toAddressFromUnresolved(transaction.getAddress()),
            Optional.ofNullable(transaction.getSignature()),
            Optional.of(new PublicAccount(transaction.getSignerPublicKey(), networkType,
                getSignSchema())),
            Optional.of(transactionInfo));
    }
}
