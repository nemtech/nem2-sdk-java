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

package io.nem.symbol.sdk.infrastructure.okhttp.mappers;

import static io.nem.symbol.core.utils.MapperUtils.toNamespaceId;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceRegistrationTransactionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceRegistrationTypeEnum;
import java.util.Optional;

/**
 * Namespace registration transaction mapper.
 */
class NamespaceRegistrationTransactionMapper extends
    AbstractTransactionMapper<NamespaceRegistrationTransactionDTO, NamespaceRegistrationTransaction> {

    public NamespaceRegistrationTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.NAMESPACE_REGISTRATION,
            NamespaceRegistrationTransactionDTO.class);
    }

    @Override
    protected TransactionFactory<NamespaceRegistrationTransaction> createFactory(
        NetworkType networkType, NamespaceRegistrationTransactionDTO transaction) {

        NamespaceRegistrationType namespaceRegistrationType = NamespaceRegistrationType
            .rawValueOf(transaction.getRegistrationType().getValue());

        return NamespaceRegistrationTransactionFactory.create(networkType,
            transaction.getName(),
            toNamespaceId(transaction.getId()),
            namespaceRegistrationType,
            namespaceRegistrationType == NamespaceRegistrationType.ROOT_NAMESPACE
                ? Optional.of(transaction.getDuration())
                : Optional.empty(),
            namespaceRegistrationType == NamespaceRegistrationType.SUB_NAMESPACE
                ? Optional
                .of(MapperUtils.toNamespaceId(transaction.getParentId()))
                : Optional.empty());
    }

    @Override
    protected void copyToDto(NamespaceRegistrationTransaction transaction,
        NamespaceRegistrationTransactionDTO dto) {
        dto.setName(transaction.getNamespaceName());
        dto.setId(MapperUtils.getIdAsHex(transaction.getNamespaceId()));
        dto.setRegistrationType(NamespaceRegistrationTypeEnum
            .fromValue(transaction.getNamespaceRegistrationType().getValue()));
        dto.setDuration(transaction.getDuration().orElse(null));
        dto.setParentId(MapperUtils.getIdAsHex(transaction.getParentId().orElse(null)));
    }

}
