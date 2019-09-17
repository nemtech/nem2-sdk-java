/*
 * Copyright 2019 NEM
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

package io.nem.sdk.model.receipt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.AddressAlias;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BalanceTransferReceiptTest {

    static Account account;
    static MosaicId mosaicId;
    static Address recipientAddress;
    static AddressAlias recipientAddressAlias;
    static SignSchema signSchema = SignSchema.DEFAULT;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST, signSchema);
        mosaicId = new MosaicId("85BBEA6CC462B244");
        recipientAddress =
            new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST);
        recipientAddressAlias = new AddressAlias(recipientAddress);
    }

    @Test
    void shouldCreateMosaicRentalFeeReceipt() {

        BalanceTransferReceipt<Address> balanceTransferReceipt =
            new BalanceTransferReceipt(
                account.getPublicAccount(),
                recipientAddress,
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.MOSAIC_RENTAL_FEE,
                ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(ReceiptType.MOSAIC_RENTAL_FEE, balanceTransferReceipt.getType());
        assertNull(balanceTransferReceipt.getSize());
        assertEquals(ReceiptVersion.BALANCE_TRANSFER, balanceTransferReceipt.getVersion());
        assertEquals(
            balanceTransferReceipt.getSender().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(
            "SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
            balanceTransferReceipt.getRecipient().pretty());
        assertEquals("85BBEA6CC462B244",
            balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, balanceTransferReceipt.getAmount());
    }

    @Test
    void shouldCreateMosaicRentalFeeReceiptWithAlias() {

        BalanceTransferReceipt<AddressAlias> balanceTransferReceipt =
            new BalanceTransferReceipt(
                account.getPublicAccount(),
                recipientAddressAlias,
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.MOSAIC_RENTAL_FEE,
                ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(ReceiptType.MOSAIC_RENTAL_FEE, balanceTransferReceipt.getType());
        assertNull(balanceTransferReceipt.getSize());
        assertEquals(ReceiptVersion.BALANCE_TRANSFER, balanceTransferReceipt.getVersion());
        assertEquals(
            balanceTransferReceipt.getSender().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(balanceTransferReceipt.getRecipient().getAliasValue(), recipientAddress);
        assertEquals(
            "85BBEA6CC462B244", balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, balanceTransferReceipt.getAmount());
    }

    @Test
    void shouldCreateNamespaceRentalFeeReceipt() {

        BalanceTransferReceipt<Address> balanceTransferReceipt =
            new BalanceTransferReceipt(
                account.getPublicAccount(),
                recipientAddress,
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.NAMESPACE_RENTAL_FEE,
                ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(ReceiptType.NAMESPACE_RENTAL_FEE, balanceTransferReceipt.getType());
        assertNull(balanceTransferReceipt.getSize());
        assertEquals(ReceiptVersion.BALANCE_TRANSFER, balanceTransferReceipt.getVersion());
        assertEquals(
            balanceTransferReceipt.getSender().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(
            "SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
            balanceTransferReceipt.getRecipient().pretty());
        assertEquals("85BBEA6CC462B244",
            balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, balanceTransferReceipt.getAmount());
    }

    @Test
    void shouldCreateNamespaceRentalFeeReceiptWithAlias() {

        BalanceTransferReceipt<AddressAlias> balanceTransferReceipt =
            new BalanceTransferReceipt<>(
                account.getPublicAccount(),
                recipientAddressAlias,
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.NAMESPACE_RENTAL_FEE,
                ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(ReceiptType.NAMESPACE_RENTAL_FEE, balanceTransferReceipt.getType());
        assertNull(balanceTransferReceipt.getSize());
        assertEquals(ReceiptVersion.BALANCE_TRANSFER, balanceTransferReceipt.getVersion());
        assertEquals(
            balanceTransferReceipt.getSender().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(balanceTransferReceipt.getRecipient().getAliasValue(), recipientAddress);
        assertEquals("85BBEA6CC462B244",
            balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, balanceTransferReceipt.getAmount());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongReceiptType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                new BalanceChangeReceipt(
                    account.getPublicAccount(),
                    mosaicId,
                    BigInteger.valueOf(10),
                    ReceiptType.NAMESPACE_EXPIRED,
                    ReceiptVersion.BALANCE_TRANSFER);
            });
    }
}
