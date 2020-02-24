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

package io.nem.symbol.sdk.infrastructure.vertx.mappers;

import static io.nem.symbol.core.utils.MapperUtils.getIdAsHex;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.AliasActionEnum;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicAliasTransactionDTO;

/**
 * Mosaic alias transaction mapper.
 */
class MosaicAliasTransactionMapper extends
    AbstractTransactionMapper<MosaicAliasTransactionDTO, MosaicAliasTransaction> {

    public MosaicAliasTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.MOSAIC_ALIAS, MosaicAliasTransactionDTO.class);
    }

    @Override
    protected TransactionFactory<MosaicAliasTransaction> createFactory(NetworkType networkType,
        MosaicAliasTransactionDTO transaction) {
        NamespaceId namespaceId = MapperUtils.toNamespaceId(transaction.getNamespaceId());
        AliasAction aliasAction = AliasAction
            .rawValueOf(transaction.getAliasAction().getValue().byteValue());
        return MosaicAliasTransactionFactory.create(
            networkType,
            aliasAction,
            namespaceId,
            MapperUtils.toMosaicId(transaction.getMosaicId()));
    }

    @Override
    protected void copyToDto(MosaicAliasTransaction transaction, MosaicAliasTransactionDTO dto) {
        dto.setAliasAction(
            AliasActionEnum.fromValue((int) transaction.getAliasAction().getValue()));
        dto.setNamespaceId(getIdAsHex(transaction.getNamespaceId()));
        dto.setMosaicId(getIdAsHex(transaction.getMosaicId()));
    }
}
