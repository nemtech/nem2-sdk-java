/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp.mappers;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.AccountOperationRestrictionTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionFlagsEnum;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionTypeEnum;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO mapper of {@link AccountOperationRestrictionTransaction}.
 */
public class AccountOperationRestrictionTransactionMapper extends
    AbstractTransactionMapper<AccountOperationRestrictionTransactionDTO, AccountOperationRestrictionTransaction> {

    public AccountOperationRestrictionTransactionMapper(
        JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.ACCOUNT_OPERATION_RESTRICTION,
            AccountOperationRestrictionTransactionDTO.class);
    }

    @Override
    protected AccountOperationRestrictionTransactionFactory createFactory(
        NetworkType networkType, AccountOperationRestrictionTransactionDTO transaction) {
        AccountRestrictionType restrictionType = AccountRestrictionType
            .rawValueOf(transaction.getRestrictionType().getValue());

        List<TransactionType> additions = transaction.getRestrictionAdditions().stream()
            .map(transactionTypeEnum -> TransactionType.rawValueOf(transactionTypeEnum.getValue())).collect(
                Collectors.toList());

        List<TransactionType> deletions = transaction.getRestrictionDeletions().stream()
            .map(transactionTypeEnum -> TransactionType.rawValueOf(transactionTypeEnum.getValue())).collect(
                Collectors.toList());
        return AccountOperationRestrictionTransactionFactory.create(networkType, restrictionType,
            additions, deletions);
    }

    @Override
    protected void copyToDto(AccountOperationRestrictionTransaction transaction,
        AccountOperationRestrictionTransactionDTO dto) {
        dto.setRestrictionType(
            AccountRestrictionFlagsEnum.fromValue(transaction.getRestrictionType().getValue()));

        List<TransactionTypeEnum> additions = transaction.getRestrictionAdditions().stream()
            .map(transactionType -> TransactionTypeEnum.fromValue(transactionType.getValue())).collect(
                Collectors.toList());

        List<TransactionTypeEnum> deletions = transaction.getRestrictionDeletions().stream()
            .map(transactionType -> TransactionTypeEnum.fromValue(transactionType.getValue())).collect(
                Collectors.toList());

        dto.setRestrictionAdditions(additions);
        dto.setRestrictionDeletions(deletions);
    }
}
