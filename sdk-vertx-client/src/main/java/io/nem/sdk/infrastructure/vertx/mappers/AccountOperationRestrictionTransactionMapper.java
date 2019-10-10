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

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionModification;
import io.nem.sdk.model.transaction.AccountRestrictionModificationAction;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.AccountOperationRestrictionModificationDTO;
import io.nem.sdk.openapi.vertx.model.AccountOperationRestrictionTransactionDTO;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionModificationActionEnum;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionTypeEnum;
import io.nem.sdk.openapi.vertx.model.TransactionTypeEnum;
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
        List<AccountRestrictionModification<TransactionType>> modifications = transaction
            .getModifications().stream().map(this::toModification).collect(Collectors.toList());
        return AccountOperationRestrictionTransactionFactory.create(networkType, restrictionType,
            modifications);
    }


    private AccountRestrictionModification<TransactionType> toModification(
        AccountOperationRestrictionModificationDTO dto) {
        AccountRestrictionModificationAction modificationAction = AccountRestrictionModificationAction
            .rawValueOf(dto.getModificationAction().getValue().byteValue());
        return AccountRestrictionModification
            .createForTransactionType(modificationAction,
                TransactionType.rawValueOf(dto.getValue().getValue()));
    }

    @Override
    protected void copyToDto(AccountOperationRestrictionTransaction transaction,
        AccountOperationRestrictionTransactionDTO dto) {
        dto.setRestrictionType(
            AccountRestrictionTypeEnum.fromValue(transaction.getRestrictionType().getValue()));
        dto.setModifications(transaction.getModifications().stream().map(this::toModification)
            .collect(Collectors.toList()));
    }


    private AccountOperationRestrictionModificationDTO toModification(
        AccountRestrictionModification<TransactionType> model) {
        AccountOperationRestrictionModificationDTO dto = new AccountOperationRestrictionModificationDTO();
        dto.setModificationAction(AccountRestrictionModificationActionEnum
            .fromValue((int) model.getModificationAction().getValue()));
        dto.setValue(TransactionTypeEnum.fromValue(model.getValue().getValue()));
        return dto;
    }
}
