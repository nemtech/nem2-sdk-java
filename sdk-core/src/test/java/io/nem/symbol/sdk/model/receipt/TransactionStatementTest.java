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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TransactionStatementTest {

    static List<Receipt> receipts;
    static ReceiptSource receiptSource;

    @BeforeAll
    public static void setup() {
        receiptSource = new ReceiptSource(1, 1);
        Account account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST);
        MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
        Address recipientAddress =
            new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST);
        ArtifactExpiryReceipt<MosaicId> mosaicExpiryReceipt =
            new ArtifactExpiryReceipt(
                mosaicId, ReceiptType.MOSAIC_EXPIRED, ReceiptVersion.ARTIFACT_EXPIRY);
        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_SECRET_EXPIRED,
                ReceiptVersion.BALANCE_CHANGE);
        BalanceTransferReceipt balanceTransferReceipt =
            new BalanceTransferReceipt(
                account.getPublicAccount(),
                recipientAddress,
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.MOSAIC_RENTAL_FEE,
                ReceiptVersion.BALANCE_TRANSFER);
        List<Receipt> list = new ArrayList<>();
        list.add(mosaicExpiryReceipt);
        list.add(balanceChangeReceipt);
        list.add(balanceTransferReceipt);
        receipts = list;
    }

    @Test
    void shouldCreateTransactionStatement() {
        TransactionStatement transactionStatement =
            new TransactionStatement(BigInteger.TEN, receiptSource, receipts);
        assertEquals(BigInteger.TEN, transactionStatement.getHeight());
        assertEquals(transactionStatement.getReceiptSource(), receiptSource);
        assertEquals(transactionStatement.getReceipts(), receipts);
    }

    @Test
    void shouldGenerateHash() {
        TransactionStatement transactionStatement =
            new TransactionStatement(BigInteger.TEN, receiptSource, receipts);

        String hash = transactionStatement.generateHash();

        assertTrue(!hash.isEmpty());
        assertEquals("0C34C53E84366FF2662C60D60D61608831E0F3239BD32B729D94DA27FB917C5F", hash);
    }
}
