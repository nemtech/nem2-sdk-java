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

import static io.nem.symbol.core.utils.MapperUtils.toUnresolvedMosaicId;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.message.Message;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransferTransactionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.UnresolvedMosaic;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Transfer transaction mapper. */
class TransferTransactionMapper
    extends AbstractTransactionMapper<TransferTransactionDTO, TransferTransaction> {

  public TransferTransactionMapper(JsonHelper jsonHelper) {
    super(jsonHelper, TransactionType.TRANSFER, TransferTransactionDTO.class);
  }

  @Override
  protected TransactionFactory<TransferTransaction> createFactory(
      NetworkType networkType, TransferTransactionDTO transaction) {
    List<Mosaic> mosaics = new ArrayList<>();
    if (transaction.getMosaics() != null) {
      mosaics =
          transaction.getMosaics().stream()
              .map(mosaic -> new Mosaic(toUnresolvedMosaicId(mosaic.getId()), mosaic.getAmount()))
              .collect(Collectors.toList());
    }

    TransferTransactionFactory transferTransactionFactory =
        TransferTransactionFactory.create(
            networkType,
            MapperUtils.toUnresolvedAddress(transaction.getRecipientAddress()),
            mosaics);
    Message.createFromHexPayload(transaction.getMessage())
        .ifPresent(transferTransactionFactory::message);

    return transferTransactionFactory;
  }

  @Override
  protected void copyToDto(TransferTransaction transaction, TransferTransactionDTO dto) {
    List<UnresolvedMosaic> mosaics = new ArrayList<>();
    if (transaction.getMosaics() != null) {
      mosaics =
          transaction.getMosaics().stream()
              .map(
                  mosaic -> {
                    UnresolvedMosaic mosaicDto = new UnresolvedMosaic();
                    mosaicDto.setAmount(mosaic.getAmount());
                    mosaicDto.setId(MapperUtils.getIdAsHex(mosaic.getId()));
                    return mosaicDto;
                  })
              .collect(Collectors.toList());
    }

    String message = transaction.getMessage().map(Message::getPayloadHex).orElse(null);
    dto.setRecipientAddress(transaction.getRecipient().encoded(transaction.getNetworkType()));
    dto.setMosaics(mosaics);
    dto.setMessage(message);
  }
}
