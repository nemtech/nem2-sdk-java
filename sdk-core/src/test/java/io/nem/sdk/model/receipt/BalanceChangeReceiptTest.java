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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class BalanceChangeReceiptTest {

    private Account account;

    private MosaicId mosaicId;

    @BeforeAll
    public void setup() {
        account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST);
        mosaicId = new MosaicId("85BBEA6CC462B244");
    }

    @Test
    void shouldCreateHarvestFeeReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.HARVEST_FEE,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(ReceiptType.HARVEST_FEE, balanceChangeReceipt.getType());
        assertFalse(balanceChangeReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_CHANGE, balanceChangeReceipt.getVersion());
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toHex().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals("85BBEA6CC462B244",
            balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, balanceChangeReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceChangeReceipt.serialize());
        Assertions.assertEquals("0100432144B262C46CEABB850A000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2", hex);
    }

    @Test
    void shouldCreateLockHashCreatedReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_HASH_CREATED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(ReceiptType.LOCK_HASH_CREATED, balanceChangeReceipt.getType());assertFalse(balanceChangeReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_CHANGE, balanceChangeReceipt.getVersion());
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toHex().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals("85BBEA6CC462B244",
            balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase()
        );
        assertEquals(BigInteger.TEN, balanceChangeReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceChangeReceipt.serialize());
        Assertions.assertEquals("0100483144B262C46CEABB850A000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2", hex);
    }

    @Test
    void shouldCreateLockHashExpiredReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_HASH_EXPIRED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(ReceiptType.LOCK_HASH_EXPIRED, balanceChangeReceipt.getType());
        assertFalse(balanceChangeReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_CHANGE, balanceChangeReceipt.getVersion());
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toHex().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals("85BBEA6CC462B244",
            balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase()
        );
        assertEquals(BigInteger.TEN, balanceChangeReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceChangeReceipt.serialize());
        Assertions.assertEquals("0100482344B262C46CEABB850A000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2", hex);
    }

    @Test
    void shouldCreateLockHashCompletedReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_HASH_COMPLETED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(ReceiptType.LOCK_HASH_COMPLETED, balanceChangeReceipt.getType());assertFalse(balanceChangeReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_CHANGE, balanceChangeReceipt.getVersion());
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toHex().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals("85BBEA6CC462B244",
            balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase()
        );
        assertEquals(BigInteger.TEN, balanceChangeReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceChangeReceipt.serialize());
        Assertions.assertEquals("0100482244B262C46CEABB850A000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2", hex);
    }

    @Test
    void shouldCreateLockSecretCreatedReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_SECRET_CREATED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(ReceiptType.LOCK_SECRET_CREATED, balanceChangeReceipt.getType());
        assertFalse(balanceChangeReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_CHANGE, balanceChangeReceipt.getVersion());
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toHex().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals("85BBEA6CC462B244",
            balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase()
        );
        assertEquals(BigInteger.TEN, balanceChangeReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceChangeReceipt.serialize());
        Assertions.assertEquals("0100523144B262C46CEABB850A000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2", hex);
    }

    @Test
    void shouldCreateLockSecretExpiredReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_SECRET_EXPIRED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(ReceiptType.LOCK_SECRET_EXPIRED, balanceChangeReceipt.getType());
        assertFalse(balanceChangeReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_CHANGE, balanceChangeReceipt.getVersion());
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toHex().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals("85BBEA6CC462B244",
            balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase()
        );
        assertEquals(BigInteger.TEN, balanceChangeReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceChangeReceipt.serialize());
        Assertions.assertEquals("0100522344B262C46CEABB850A000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2", hex);
    }

    @Test
    void shouldCreateLockSecretCompletedReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_SECRET_COMPLETED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(ReceiptType.LOCK_SECRET_COMPLETED, balanceChangeReceipt.getType());
        assertFalse(balanceChangeReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.BALANCE_CHANGE, balanceChangeReceipt.getVersion());
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toHex().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals("85BBEA6CC462B244",
            balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase()
        );
        assertEquals(BigInteger.TEN, balanceChangeReceipt.getAmount());

        String hex = ConvertUtils.toHex(balanceChangeReceipt.serialize());
        Assertions.assertEquals("0100522244B262C46CEABB850A000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F2", hex);
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
                    ReceiptVersion.BALANCE_CHANGE);
            });
    }
}
