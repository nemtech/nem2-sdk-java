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

package io.nem.symbol.sdk.model.receipt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BalanceTransferReceiptTest {

    static Account account;
    static MosaicId mosaicId;
    static Address unresolvedAddress;
    static Address address;
    static NetworkType networkType = NetworkType.MIJIN_TEST;

    @BeforeAll
    public static void setup() {
        account = Account.generateNewAccount(networkType);
        mosaicId = new MosaicId("85BBEA6CC462B244");
        address = Address.generateRandom(networkType);
        unresolvedAddress = address;

    }

    @Test
    void shouldCreateMosaicRentalFeeReceipt() {

        BalanceTransferReceipt balanceTransferReceipt =
            new BalanceTransferReceipt(
                account.getAddress(),
                unresolvedAddress,
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.MOSAIC_RENTAL_FEE,
                ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(ReceiptType.MOSAIC_RENTAL_FEE, balanceTransferReceipt.getType());
        assertFalse(balanceTransferReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_TRANSFER, balanceTransferReceipt.getVersion());
        assertEquals(
            balanceTransferReceipt.getSenderAddress(),
            account.getAddress());
        assertEquals(MapperUtils.toAddress(balanceTransferReceipt.getRecipientAddress().encoded(
            networkType)), address);
        assertEquals("85BBEA6CC462B244",
            balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, balanceTransferReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceTransferReceipt.serialize());

        Assertions.assertEquals(
            "01004D1244B262C46CEABB850A00000000000000" + account.getPublicAccount().getAddress().encoded()
                + unresolvedAddress.encoded(),
            hex);

    }

    @Test
    void shouldSerializeTheSameAsTypescript() {
        Address sender = Address.generateRandom(networkType);
        Address recipient = Address.generateRandom(networkType);
        BalanceTransferReceipt balanceTransferReceipt =
            new BalanceTransferReceipt(
                sender,
                recipient,
                new MosaicId("941299B2B7E1291C"),
                BigInteger.valueOf(1000),
                ReceiptType.MOSAIC_RENTAL_FEE,
                ReceiptVersion.BALANCE_TRANSFER);

        String hex = ConvertUtils.toHex(balanceTransferReceipt.serialize());
        Assertions.assertEquals(
            "01004D121C29E1B7B2991294E803000000000000" + sender.encoded() + recipient.encoded(),
            hex.toUpperCase());
    }

    @Test
    void shouldCreateNamespaceRentalFeeReceipt() {

        BalanceTransferReceipt balanceTransferReceipt =
            new BalanceTransferReceipt(
                account.getPublicAccount().getAddress(),
                unresolvedAddress,
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.NAMESPACE_RENTAL_FEE,
                ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(ReceiptType.NAMESPACE_RENTAL_FEE, balanceTransferReceipt.getType());
        assertFalse(balanceTransferReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_TRANSFER, balanceTransferReceipt.getVersion());
        assertEquals(
            balanceTransferReceipt.getSenderAddress(),
            account.getAddress());
        assertEquals(MapperUtils.toAddress(balanceTransferReceipt.getRecipientAddress().encoded(
            networkType)), address);
        assertEquals("85BBEA6CC462B244",
            balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, balanceTransferReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceTransferReceipt.serialize());

        System.out.println(account.getPublicAccount().getAddress().encoded());
        System.out.println(unresolvedAddress.encoded());

        Assertions.assertEquals(
            "01004E1344B262C46CEABB850A00000000000000" + account.getPublicAccount().getAddress().encoded()
                + unresolvedAddress.encoded(),
            hex);

    }

    @Test
    void shouldCreateNamespaceRentalFeeReceiptWithAlias() {

        BalanceTransferReceipt balanceTransferReceipt =
            new BalanceTransferReceipt(
                account.getAddress(),
                unresolvedAddress,
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.NAMESPACE_RENTAL_FEE,
                ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(ReceiptType.NAMESPACE_RENTAL_FEE, balanceTransferReceipt.getType());
        assertFalse(balanceTransferReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_TRANSFER, balanceTransferReceipt.getVersion());
        assertEquals(balanceTransferReceipt.getSenderAddress(), account.getAddress());
        assertEquals(MapperUtils.toAddress(balanceTransferReceipt.getRecipientAddress().encoded(
            networkType)), address);
        assertEquals("85BBEA6CC462B244",
            balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, balanceTransferReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceTransferReceipt.serialize());

        Assertions.assertEquals(
            "01004E1344B262C46CEABB850A00000000000000" + account.getPublicAccount().getAddress().encoded()
                + unresolvedAddress.encoded(),
            hex);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongReceiptType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                new BalanceChangeReceipt(
                    account.getAddress(),
                    mosaicId,
                    BigInteger.valueOf(10),
                    ReceiptType.NAMESPACE_EXPIRED,
                    ReceiptVersion.BALANCE_TRANSFER);
            });
    }
}
