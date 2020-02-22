/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp;

import static io.nem.core.utils.MapperUtils.toAddressFromEncoded;
import static io.nem.core.utils.MapperUtils.toMosaicId;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.receipt.AddressResolutionStatement;
import io.nem.sdk.model.receipt.ArtifactExpiryReceipt;
import io.nem.sdk.model.receipt.BalanceChangeReceipt;
import io.nem.sdk.model.receipt.BalanceTransferReceipt;
import io.nem.sdk.model.receipt.InflationReceipt;
import io.nem.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.sdk.model.receipt.Receipt;
import io.nem.sdk.model.receipt.ReceiptSource;
import io.nem.sdk.model.receipt.ReceiptType;
import io.nem.sdk.model.receipt.ReceiptVersion;
import io.nem.sdk.model.receipt.ResolutionEntry;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.receipt.TransactionStatement;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BalanceChangeReceiptDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.BalanceTransferReceiptDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.InflationReceiptDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicExpiryReceiptDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceExpiryReceiptDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ResolutionStatementBodyDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ResolutionStatementDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.StatementsDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionStatementDTO;
import java.util.List;
import java.util.stream.Collectors;


public class ReceiptMappingOkHttp {

    private final JsonHelper jsonHelper;

    public ReceiptMappingOkHttp(JsonHelper jsonHelper) {
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
            MapperUtils.toUnresolvedAddress(statement.getUnresolved().toString()),
            statement.getResolutionEntries().stream()
                .map(
                    entry ->
                        ResolutionEntry.forAddress(
                            toAddressFromEncoded(entry.getResolved().toString()),
                            new ReceiptSource(entry.getSource().getPrimaryId(),
                                entry.getSource().getSecondaryId())))
                .collect(Collectors.toList()));
    }

    public MosaicResolutionStatement createMosaicResolutionStatementFromDto(
        ResolutionStatementDTO receiptDto) {
        ResolutionStatementBodyDTO statement = receiptDto.getStatement();
        return new MosaicResolutionStatement(
            statement.getHeight(),
            MapperUtils.toUnresolvedMosaicId(statement.getUnresolved().toString()),
            statement.getResolutionEntries().stream()
                .map(
                    entry ->
                        ResolutionEntry.forMosaicId(
                            toMosaicId(entry.getResolved().toString()),
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
                .map(receipt -> createReceiptFromDto(receipt, networkType))
                .collect(Collectors.toList()));
    }

    public Receipt createReceiptFromDto(Object receiptDto, NetworkType networkType) {
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
                    jsonHelper.convert(receiptDto, BalanceChangeReceiptDTO.class), networkType);
            case MOSAIC_RENTAL_FEE:
            case NAMESPACE_RENTAL_FEE:
                return createBalanceTransferRecipient(
                    jsonHelper.convert(receiptDto, BalanceTransferReceiptDTO.class), networkType);
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

    public ArtifactExpiryReceipt<MosaicId> createArtifactExpiryReceipt(
        NamespaceExpiryReceiptDTO receipt, ReceiptType type) {
        return new ArtifactExpiryReceipt<>(
            MapperUtils.toMosaicId(receipt.getArtifactId()), type,
            ReceiptVersion.ARTIFACT_EXPIRY);
    }

    public ArtifactExpiryReceipt<MosaicId> createArtifactExpiryReceipt(
        MosaicExpiryReceiptDTO receipt, ReceiptType type) {
        return new ArtifactExpiryReceipt<>(
            MapperUtils.toMosaicId(receipt.getArtifactId()), type,
            ReceiptVersion.ARTIFACT_EXPIRY);
    }


    public BalanceChangeReceipt createBalanceChangeReceipt(
        BalanceChangeReceiptDTO receipt, NetworkType networkType) {
        return new BalanceChangeReceipt(
            PublicAccount.createFromPublicKey(receipt.getTargetPublicKey(), networkType),
            new MosaicId(receipt.getMosaicId()),
            receipt.getAmount(),
            ReceiptType.rawValueOf(receipt.getType().getValue()),
            ReceiptVersion.BALANCE_CHANGE);
    }

    public BalanceTransferReceipt createBalanceTransferRecipient(
        BalanceTransferReceiptDTO receipt, NetworkType networkType) {
        return new BalanceTransferReceipt(
            PublicAccount.createFromPublicKey(receipt.getSenderPublicKey(), networkType),
            MapperUtils.toUnresolvedAddress(receipt.getRecipientAddress()),
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
