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

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.AccountMosaicRestrictionTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionFlagsEnum;
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

        List<UnresolvedMosaicId> additions = transaction.getRestrictionAdditions().stream()
            .map(MapperUtils::toUnresolvedMosaicId).collect(
                Collectors.toList());

        List<UnresolvedMosaicId> deletions = transaction.getRestrictionDeletions().stream()
            .map(MapperUtils::toUnresolvedMosaicId).collect(
                Collectors.toList());
        return AccountMosaicRestrictionTransactionFactory.create(networkType, restrictionType,
            additions, deletions);
    }

    @Override
    protected void copyToDto(AccountMosaicRestrictionTransaction transaction,
        AccountMosaicRestrictionTransactionDTO dto) {
        dto.setRestrictionType(
            AccountRestrictionFlagsEnum.fromValue(transaction.getRestrictionType().getValue()));

        List<String> additions = transaction.getRestrictionAdditions().stream()
            .map(MapperUtils::getIdAsHex).collect(
                Collectors.toList());

        List<String> deletions = transaction.getRestrictionDeletions().stream()
            .map(MapperUtils::getIdAsHex).collect(
                Collectors.toList());

        dto.setRestrictionAdditions(additions);
        dto.setRestrictionDeletions(deletions);
    }

}
