/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import java.math.BigInteger;
import java.time.LocalDateTime;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MosaicSupplyChangeTransactionTest extends AbstractTransactionTester {

    @Test
    void createAMosaicSupplyChangeTransactionViaConstructor() {

        MosaicSupplyChangeTransaction mosaicSupplyChangeTx =
            MosaicSupplyChangeTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("6300565133566699912")),
                MosaicSupplyChangeActionType.INCREASE,
                BigInteger.valueOf(10)).build();

        assertEquals(NetworkType.MIJIN_TEST, mosaicSupplyChangeTx.getNetworkType());
        assertTrue(1 == mosaicSupplyChangeTx.getVersion());
        assertTrue(
            LocalDateTime.now().isBefore(mosaicSupplyChangeTx.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), mosaicSupplyChangeTx.getMaxFee());
        assertEquals(new BigInteger("6300565133566699912"),
            mosaicSupplyChangeTx.getMosaicId().getId());
        assertEquals(
            MosaicSupplyChangeActionType.INCREASE, mosaicSupplyChangeTx.getAction());
        assertEquals(BigInteger.valueOf(10), mosaicSupplyChangeTx.getDelta());
    }

    @Test
    @DisplayName("Serialization")
    void serialization() {
        // Generated at nem2-library-js/test/transactions/MosaicSupplyChangeTransaction.spec.js
        String expected =
            "8900000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904d42000000000000000001000000000000008869746e9b1a7057010a00000000000000";

        MosaicSupplyChangeTransaction transaction =
            MosaicSupplyChangeTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("6300565133566699912")),
                MosaicSupplyChangeActionType.INCREASE,
                BigInteger.valueOf(10)
            ).deadline(new FakeDeadline()).build();

        byte[] actual = transaction.generateBytes();
        assertEquals(expected, Hex.toHexString(actual));

        assertSerialization(expected, transaction);
    }
}
