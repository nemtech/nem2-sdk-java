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
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionModification;
import io.nem.sdk.model.transaction.AccountRestrictionModificationAction;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.openapi.okhttp_gson.model.AccountAddressRestrictionModificationDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountAddressRestrictionTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionModificationActionEnum;
import io.nem.sdk.openapi.okhttp_gson.model.AccountRestrictionTypeEnum;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO mapper of {@link AccountAddressRestrictionTransaction}.
 */
public class AccountAddressRestrictionTransactionMapper extends
    AbstractTransactionMapper<AccountAddressRestrictionTransactionDTO, AccountAddressRestrictionTransaction> {

    public AccountAddressRestrictionTransactionMapper(
        JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.ACCOUNT_ADDRESS_RESTRICTION,
            AccountAddressRestrictionTransactionDTO.class);
    }

    @Override
    protected AccountAddressRestrictionTransactionFactory createFactory(
        NetworkType networkType, AccountAddressRestrictionTransactionDTO transaction) {
        AccountRestrictionType restrictionType = AccountRestrictionType
            .rawValueOf(transaction.getRestrictionType().getValue());
        List<AccountRestrictionModification<UnresolvedAddress>> modifications = transaction
            .getModifications().stream().map(this::toModification).collect(Collectors.toList());
        return AccountAddressRestrictionTransactionFactory.create(networkType, restrictionType,
            modifications);
    }

    private AccountRestrictionModification<UnresolvedAddress> toModification(
        AccountAddressRestrictionModificationDTO dto) {
        AccountRestrictionModificationAction modificationAction = AccountRestrictionModificationAction
            .rawValueOf(dto.getModificationAction().getValue().byteValue());
        return AccountRestrictionModification
            .createForAddress(modificationAction,
                MapperUtils.toUnresolvedAddress(dto.getValue()));
    }

    @Override
    protected void copyToDto(
        AccountAddressRestrictionTransaction transaction,
        AccountAddressRestrictionTransactionDTO dto) {
        dto.setRestrictionType(
            AccountRestrictionTypeEnum.fromValue(transaction.getRestrictionType().getValue()));
        dto.setModifications(
            transaction.getModifications().stream().map(
                source -> toModification(source, transaction.getNetworkType())).collect(
                Collectors.toList()));
    }

    private AccountAddressRestrictionModificationDTO toModification(
        AccountRestrictionModification<UnresolvedAddress> source,
        NetworkType networkType) {
        AccountRestrictionModificationActionEnum modificationAction = AccountRestrictionModificationActionEnum
            .fromValue((int) source.getModificationAction().getValue());
        AccountAddressRestrictionModificationDTO target = new AccountAddressRestrictionModificationDTO();
        target.setModificationAction(modificationAction);
        target.setValue(source.getValue().encoded(networkType));
        return target;
    }
}
