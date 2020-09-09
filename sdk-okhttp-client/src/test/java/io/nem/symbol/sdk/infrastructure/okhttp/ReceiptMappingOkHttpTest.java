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
package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.ArtifactExpiryReceipt;
import io.nem.symbol.sdk.model.receipt.BalanceTransferReceipt;
import io.nem.symbol.sdk.model.receipt.InflationReceipt;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.Receipt;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ResolutionStatementInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionStatementInfoDTO;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReceiptMappingOkHttpTest {

  private final JsonHelper jsonHelper = new JsonHelperGson();

  private final NetworkType networkType = NetworkType.MIJIN_TEST;

  private final ReceiptMappingOkHttp mapper = new ReceiptMappingOkHttp(jsonHelper);

  @Test
  public void getMosaicResolutionStatementHash() {

    List<ResolutionStatementInfoDTO> dtos =
        jsonHelper.parseList(
            TestHelperOkHttp.loadResource("Recipient-MosaicResolutionStatement.json"),
            ResolutionStatementInfoDTO.class);

    List<MosaicResolutionStatement> mosaicResolutionStatement =
        dtos.stream()
            .map(mapper::createMosaicResolutionStatementFromDto)
            .collect(Collectors.toList());

    Assertions.assertEquals(
        "DE29FB6356530E5D1FBEE0A84202520C155D882C46EA74456752D6C75F0707B3",
        mosaicResolutionStatement.get(0).generateHash(networkType));
  }

  @Test
  public void getTransactionStatementshash() {
    List<TransactionStatementInfoDTO> transactionStatementInfoDTOS =
        jsonHelper.parseList(
            TestHelperOkHttp.loadResource("Recipient-TransactionResolutionStatement.json"),
            TransactionStatementInfoDTO.class);

    List<TransactionStatement> transactionStatements =
        transactionStatementInfoDTOS.stream()
            .map(mapper::createTransactionStatement)
            .collect(Collectors.toList());

    Assertions.assertEquals(
        "82FEFFC329618ECF56B8A6FDBCFCF1BF0A4B6747AB6A5746B195CEEB810F335C",
        transactionStatements.get(0).generateHash().toUpperCase());
  }

  @Test
  public void getTransactionStatements2Recipients() {

    List<TransactionStatementInfoDTO> transactionStatementInfoDTOS =
        jsonHelper.parseList(
            TestHelperOkHttp.loadResource("Recipient-TransactionResolutionStatement.json"),
            TransactionStatementInfoDTO.class);

    List<TransactionStatement> transactionStatements =
        transactionStatementInfoDTOS.stream()
            .map(mapper::createTransactionStatement)
            .collect(Collectors.toList());
    TransactionStatement transactionStatement = transactionStatements.get(1);
    Assertions.assertEquals(
        "41BF730BD307EB507880CA0576A4CCEFEF95F3060DC334CA364AE737114D1F60",
        transactionStatement.generateHash().toUpperCase());

    List<Receipt> receipts = transactionStatement.getReceipts();

    Assertions.assertEquals(5, receipts.size());
    Assertions.assertEquals(ReceiptType.NAMESPACE_RENTAL_FEE, receipts.get(0).getType());

    Assertions.assertEquals(
        "85BBEA6CC462B244", ((BalanceTransferReceipt) receipts.get(0)).getMosaicId().getIdAsHex());

    Assertions.assertEquals(ReceiptType.MOSAIC_EXPIRED, receipts.get(1).getType());
    Assertions.assertEquals(
        MosaicId.class, ((ArtifactExpiryReceipt) receipts.get(1)).getArtifactId().getClass());

    Assertions.assertEquals(ReceiptType.NAMESPACE_EXPIRED, receipts.get(2).getType());
    Assertions.assertEquals(
        NamespaceId.class, ((ArtifactExpiryReceipt) receipts.get(2)).getArtifactId().getClass());

    Assertions.assertEquals(ReceiptType.NAMESPACE_DELETED, receipts.get(3).getType());
    Assertions.assertEquals(
        NamespaceId.class, ((ArtifactExpiryReceipt) receipts.get(3)).getArtifactId().getClass());

    Assertions.assertEquals(ReceiptType.INFLATION, receipts.get(4).getType());
    Assertions.assertEquals(333, ((InflationReceipt) receipts.get(4)).getAmount().longValue());
  }

  @Test
  public void createReceiptFromDtoInvalid() {
    ReceiptMappingOkHttp receiptMappingOkHttp = new ReceiptMappingOkHttp(jsonHelper);
    IllegalArgumentException e =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {
              receiptMappingOkHttp.createReceiptFromDto(Collections.singletonMap("type", 61763));
            });
    Assertions.assertEquals("Receipt type: ADDRESS_ALIAS_RESOLUTION not valid", e.getMessage());
  }

  @Test
  public void getAddressResolutionStatementsHash() {
    List<ResolutionStatementInfoDTO> dtos =
        jsonHelper.parseList(
            TestHelperOkHttp.loadResource("Recipient-AddressResolutionStatement.json"),
            ResolutionStatementInfoDTO.class);

    List<AddressResolutionStatement> addressResolutionStatements =
        dtos.stream()
            .map(mapper::createAddressResolutionStatementFromDto)
            .collect(Collectors.toList());

    Assertions.assertEquals(
        "9FB5235FB53C815DEFDCC9FC137BF949987097BDE2D3132EEF746F0AE0A6B02F",
        addressResolutionStatements.get(0).generateHash(networkType));
  }
}
