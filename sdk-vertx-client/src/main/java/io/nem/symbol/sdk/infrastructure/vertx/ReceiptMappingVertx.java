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

package io.nem.symbol.sdk.infrastructure.vertx;

import static io.nem.symbol.core.utils.MapperUtils.toAddress;
import static io.nem.symbol.core.utils.MapperUtils.toMosaicId;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.ArtifactExpiryReceipt;
import io.nem.symbol.sdk.model.receipt.BalanceChangeReceipt;
import io.nem.symbol.sdk.model.receipt.BalanceTransferReceipt;
import io.nem.symbol.sdk.model.receipt.InflationReceipt;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.Receipt;
import io.nem.symbol.sdk.model.receipt.ReceiptSource;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.ReceiptVersion;
import io.nem.symbol.sdk.model.receipt.ResolutionEntry;
import io.nem.symbol.sdk.model.receipt.Statement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.vertx.model.BalanceChangeReceiptDTO;
import io.nem.symbol.sdk.openapi.vertx.model.BalanceTransferReceiptDTO;
import io.nem.symbol.sdk.openapi.vertx.model.InflationReceiptDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicExpiryReceiptDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NamespaceExpiryReceiptDTO;
import io.nem.symbol.sdk.openapi.vertx.model.ResolutionStatementBodyDTO;
import io.nem.symbol.sdk.openapi.vertx.model.ResolutionStatementDTO;
import io.nem.symbol.sdk.openapi.vertx.model.StatementsDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionStatementDTO;
import java.util.List;
import java.util.stream.Collectors;


public class ReceiptMappingVertx {

    private final JsonHelper jsonHelper;

    public ReceiptMappingVertx(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public Statement createStatementFromDto(StatementsDTO input, NetworkType networkType) {
        List<TransactionStatement> transactionStatements =
            input.getTransactionStatements().stream()
                .map(receiptDto -> createTransactionStatement(receiptDto, networkType))
                .collect(Collectors.toList());
        List<AddressResolutionStatement> addressResolutionStatements =
            input.getAddressResolutionStatements().stream()
                .map(this::createAddressResolutionStatementFromDto)
                .collect(Collectors.toList());
        List<MosaicResolutionStatement> mosaicResolutionStatements =
            input.getMosaicResolutionStatements().stream()
                .map(this::createMosaicResolutionStatementFromDto)
                .collect(Collectors.toList());
        return new Statement(
            transactionStatements, addressResolutionStatements, mosaicResolutionStatements);
    }


    public AddressResolutionStatement createAddressResolutionStatementFromDto(
        ResolutionStatementDTO receiptDto) {
        ResolutionStatementBodyDTO statement = receiptDto.getStatement();
        return new AddressResolutionStatement(
            statement.getHeight(),
            MapperUtils.toUnresolvedAddress(statement.getUnresolved()),
            statement.getResolutionEntries().stream()
                .map(
                    entry ->
                        ResolutionEntry.forAddress(
                            toAddress(entry.getResolved()),
                            new ReceiptSource(entry.getSource().getPrimaryId(),
                                entry.getSource().getSecondaryId())))
                .collect(Collectors.toList()));
    }

    public MosaicResolutionStatement createMosaicResolutionStatementFromDto(
        ResolutionStatementDTO receiptDto) {
        ResolutionStatementBodyDTO statement = receiptDto.getStatement();
        return new MosaicResolutionStatement(
            statement.getHeight(),
            MapperUtils.toUnresolvedMosaicId(statement.getUnresolved()),
            statement.getResolutionEntries().stream()
                .map(
                    entry ->
                        ResolutionEntry.forMosaicId(
                            toMosaicId(entry.getResolved()),
                            new ReceiptSource(
                                entry.getSource().getPrimaryId(),
                                entry.getSource().getSecondaryId())))
                .collect(Collectors.toList()));
    }


    public TransactionStatement createTransactionStatement(
        TransactionStatementDTO input, NetworkType networkType) {
        return new TransactionStatement(
            input.getStatement().getHeight(),
            new ReceiptSource(
                input.getStatement().getSource().getPrimaryId(),
                input.getStatement().getSource().getSecondaryId()),
            input.getStatement().getReceipts().stream()
                .map(receipt -> createReceiptFromDto(receipt))
                .collect(Collectors.toList()));
    }

    public Receipt createReceiptFromDto(Object receiptDto) {
        ReceiptType type = ReceiptType.rawValueOf(jsonHelper.getInteger(receiptDto, "type"));
        switch (type) {
            case HARVEST_FEE:
            case LOCK_HASH_CREATED:
            case LOCK_HASH_COMPLETED:
            case LOCK_HASH_EXPIRED:
            case LOCK_SECRET_CREATED:
            case LOCK_SECRET_COMPLETED:
            case LOCK_SECRET_EXPIRED:
                return createBalanceChangeReceipt(
                    jsonHelper.convert(receiptDto, BalanceChangeReceiptDTO.class));
            case MOSAIC_RENTAL_FEE:
            case NAMESPACE_RENTAL_FEE:
                return createBalanceTransferRecipient(
                    jsonHelper.convert(receiptDto, BalanceTransferReceiptDTO.class));
            case MOSAIC_EXPIRED:
                return createArtifactExpiryReceipt(
                    jsonHelper.convert(receiptDto, MosaicExpiryReceiptDTO.class), type);
            case NAMESPACE_EXPIRED:
            case NAMESPACE_DELETED:
                return createArtifactExpiryReceipt(
                    jsonHelper.convert(receiptDto, NamespaceExpiryReceiptDTO.class), type);
            case INFLATION:
                return createInflationReceipt(
                    jsonHelper.convert(receiptDto, InflationReceiptDTO.class));
            default:
                throw new IllegalArgumentException("Receipt type: " + type.name() + " not valid");
        }
    }

    public ArtifactExpiryReceipt<NamespaceId> createArtifactExpiryReceipt(
        NamespaceExpiryReceiptDTO receipt, ReceiptType type) {
        return new ArtifactExpiryReceipt<>(
            MapperUtils.toNamespaceId(receipt.getArtifactId()), type,
            ReceiptVersion.ARTIFACT_EXPIRY);
    }

    public ArtifactExpiryReceipt<MosaicId> createArtifactExpiryReceipt(
        MosaicExpiryReceiptDTO receipt, ReceiptType type) {
        return new ArtifactExpiryReceipt<>(
            MapperUtils.toMosaicId(receipt.getArtifactId()), type,
            ReceiptVersion.ARTIFACT_EXPIRY);
    }


    public BalanceChangeReceipt createBalanceChangeReceipt(
        BalanceChangeReceiptDTO receipt) {
        return new BalanceChangeReceipt(
            MapperUtils.toAddress(receipt.getTargetAddress()),
            new MosaicId(receipt.getMosaicId()),
            receipt.getAmount(),
            ReceiptType.rawValueOf(receipt.getType().getValue()),
            ReceiptVersion.BALANCE_CHANGE);
    }

    public BalanceTransferReceipt createBalanceTransferRecipient(BalanceTransferReceiptDTO receipt) {
        return new BalanceTransferReceipt(
            MapperUtils.toAddress(receipt.getSenderAddress()),
            Address.createFromEncoded(receipt.getRecipientAddress()),
            new MosaicId(receipt.getMosaicId()),
            receipt.getAmount(),
            ReceiptType.rawValueOf(receipt.getType().getValue()),
            ReceiptVersion.BALANCE_TRANSFER);
    }

    public InflationReceipt createInflationReceipt(InflationReceiptDTO receipt) {
        return new InflationReceipt(
            new MosaicId(receipt.getMosaicId()),
            receipt.getAmount(),
            ReceiptType.rawValueOf(receipt.getType().getValue()),
            ReceiptVersion.INFLATION_RECEIPT);
    }

}
