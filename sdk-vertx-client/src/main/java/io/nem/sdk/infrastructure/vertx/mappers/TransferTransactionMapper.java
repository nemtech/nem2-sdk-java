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

import static io.nem.core.utils.MapperUtils.toUnresolvedAddress;
import static io.nem.core.utils.MapperUtils.toUnresolvedMosaicId;

import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.Message;
import io.nem.sdk.model.message.MessageType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.TransactionFactory;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import io.nem.sdk.openapi.vertx.model.MessageDTO;
import io.nem.sdk.openapi.vertx.model.MessageTypeEnum;
import io.nem.sdk.openapi.vertx.model.TransferTransactionDTO;
import io.nem.sdk.openapi.vertx.model.UnresolvedMosaic;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Transfer transaction mapper.
 */
class TransferTransactionMapper extends
    AbstractTransactionMapper<TransferTransactionDTO, TransferTransaction> {

    public TransferTransactionMapper(JsonHelper jsonHelper) {
        super(jsonHelper, TransactionType.TRANSFER, TransferTransactionDTO.class);
    }

    @Override
    protected TransactionFactory<TransferTransaction> createFactory(NetworkType networkType,
        TransferTransactionDTO transaction) {
        List<Mosaic> mosaics = new ArrayList<>();
        if (transaction.getMosaics() != null) {
            mosaics =
                transaction.getMosaics().stream()
                    .map(
                        mosaic ->
                            new Mosaic(
                                toUnresolvedMosaicId(mosaic.getId()),
                                mosaic.getAmount()))
                    .collect(Collectors.toList());
        }

        Message message = Optional.ofNullable(transaction.getMessage())
            .map(m -> Message.createFromPayload(
                MessageType.rawValueOf(m.getType().getValue()),
                m.getPayload())).orElse(PlainMessage.Empty);

        return TransferTransactionFactory.create(networkType,
            toUnresolvedAddress(transaction.getRecipientAddress()),
            mosaics,
            message);
    }

    @Override
    protected void copyToDto(TransferTransaction transaction, TransferTransactionDTO dto) {
        List<UnresolvedMosaic> mosaics = new ArrayList<>();
        if (transaction.getMosaics() != null) {
            mosaics =
                transaction.getMosaics().stream()
                    .map(
                        mosaic -> {
                            io.nem.sdk.openapi.vertx.model.UnresolvedMosaic mosaicDto = new io.nem.sdk.openapi.vertx.model.UnresolvedMosaic();
                            mosaicDto.setAmount(mosaic.getAmount());
                            mosaicDto.setId(MapperUtils.getIdAsHex(mosaic.getId()));
                            return mosaicDto;
                        })
                    .collect(Collectors.toList());
        }

        MessageDTO message = null;
        if (transaction.getMessage() != null) {
            message = new MessageDTO();
            message.setType(MessageTypeEnum.NUMBER_0);
            message.setPayload(ConvertUtils
                .toHex(transaction.getMessage().getPayload().getBytes(StandardCharsets.UTF_8)));

        }
        dto.setRecipientAddress(transaction.getRecipient().encoded(transaction.getNetworkType()));
        dto.setMosaics(mosaics);
        dto.setMessage(message);

    }

}
