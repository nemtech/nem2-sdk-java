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
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InflationReceiptTest {

    static MosaicId mosaicId;

    @BeforeAll
    public static void setup() {
        mosaicId = new MosaicId("85BBEA6CC462B244");
    }

    @Test
    void shouldCreateInflationReceipt() {

        InflationReceipt inflationReceipt =
            new InflationReceipt(
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.INFLATION,
                ReceiptVersion.INFLATION_RECEIPT);
        assertEquals(ReceiptType.INFLATION, inflationReceipt.getType());
        assertFalse(inflationReceipt.getSize().isPresent());
        assertEquals(ReceiptVersion.INFLATION_RECEIPT, inflationReceipt.getVersion());
        assertEquals("85BBEA6CC462B244", inflationReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, inflationReceipt.getAmount());

        String hex = ConvertUtils.toHex(inflationReceipt.serialize());
        Assertions.assertEquals("0100435144B262C46CEABB850A00000000000000", hex);
    }

    @Test
    void shouldCreateInflationReceiptWithSize() {

        InflationReceipt inflationReceipt =
            new InflationReceipt(
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.INFLATION,
                ReceiptVersion.INFLATION_RECEIPT,
                Optional.of(100));
        assertEquals(ReceiptType.INFLATION, inflationReceipt.getType());
        assertEquals(inflationReceipt.getSize(), Optional.of(100));
        assertEquals(ReceiptVersion.INFLATION_RECEIPT, inflationReceipt.getVersion());
        assertEquals("85BBEA6CC462B244", inflationReceipt.getMosaicId().getIdAsHex().toUpperCase());
        assertEquals(BigInteger.TEN, inflationReceipt.getAmount());
        assertEquals(100, inflationReceipt.getSize().get().intValue());

        String hex = ConvertUtils.toHex(inflationReceipt.serialize());
        Assertions.assertEquals("0100435144B262C46CEABB850A00000000000000", hex);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongReceiptType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                new InflationReceipt(
                    mosaicId,
                    BigInteger.valueOf(10),
                    ReceiptType.NAMESPACE_RENTAL_FEE,
                    ReceiptVersion.INFLATION_RECEIPT);
            });
    }
}
