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

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionModification;
import io.nem.sdk.model.transaction.AccountRestrictionModificationAction;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.vertx.model.AccountMosaicRestrictionModificationDTO;
import io.nem.sdk.openapi.vertx.model.AccountMosaicRestrictionTransactionDTO;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionModificationActionEnum;
import io.nem.sdk.openapi.vertx.model.AccountRestrictionTypeEnum;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO mapper of {@link AccountMosaicRestrictionTransaction}.
 */
public class AccountMosaicRestrictionTransactionMapper extends
    AbstractTransactionMapper<AccountMosaicRestrictionTransactionDTO, AccountMosaicRestrictionTransaction> {

    public AccountMosaicRestrictionTransactionMapper(
        JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.ACCOUNT_MOSAIC_RESTRICTION,
            AccountMosaicRestrictionTransactionDTO.class);
    }

    @Override
    protected AccountMosaicRestrictionTransactionFactory createFactory(
        NetworkType networkType, AccountMosaicRestrictionTransactionDTO transaction) {
        AccountRestrictionType restrictionType = AccountRestrictionType
            .rawValueOf(transaction.getRestrictionType().getValue());
        List<AccountRestrictionModification<UnresolvedMosaicId>> modifications = transaction
            .getModifications().stream().map(this::toModification).collect(Collectors.toList());
        return AccountMosaicRestrictionTransactionFactory.create(networkType, restrictionType,
            modifications);
    }

    private AccountRestrictionModification<UnresolvedMosaicId> toModification(
        AccountMosaicRestrictionModificationDTO dto) {
        AccountRestrictionModificationAction modificationAction = AccountRestrictionModificationAction
            .rawValueOf(dto.getModificationAction().getValue().byteValue());
        return AccountRestrictionModification
            .createForMosaic(modificationAction, MapperUtils.toUnresolvedMosaicId(dto.getValue()));
    }

    @Override
    protected void copyToDto(AccountMosaicRestrictionTransaction transaction,
        AccountMosaicRestrictionTransactionDTO dto) {
        dto.setRestrictionType(
            AccountRestrictionTypeEnum.fromValue(transaction.getRestrictionType().getValue()));
        dto.setModifications(transaction.getModifications().stream().map(this::toModification)
            .collect(Collectors.toList()));
    }


    private AccountMosaicRestrictionModificationDTO toModification(
        AccountRestrictionModification<UnresolvedMosaicId> model) {
        AccountMosaicRestrictionModificationDTO dto = new AccountMosaicRestrictionModificationDTO();
        dto.setModificationAction(AccountRestrictionModificationActionEnum
            .fromValue((int) model.getModificationAction().getValue()));
        dto.setValue(MapperUtils.getIdAsHex(model.getValue()));
        return dto;
    }

}
