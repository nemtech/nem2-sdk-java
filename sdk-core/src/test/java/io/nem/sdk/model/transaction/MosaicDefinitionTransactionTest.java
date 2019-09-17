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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicProperties;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MosaicDefinitionTransactionTest {

    static SignSchema signSchema = SignSchema.DEFAULT;

    @Test
    void createAMosaicCreationTransactionViaStaticConstructor() {
        MosaicDefinitionTransaction mosaicCreationTx =
            MosaicDefinitionTransaction.create(
                new Deadline(2, ChronoUnit.HOURS),
                BigInteger.ZERO,
                MosaicNonce.createFromBigInteger(new BigInteger("0")),
                new MosaicId(new BigInteger("0")),
                MosaicProperties.create(true, true, 3, BigInteger.valueOf(10)),
                NetworkType.MIJIN_TEST);

        assertEquals(NetworkType.MIJIN_TEST, mosaicCreationTx.getNetworkType());
        assertTrue(1 == mosaicCreationTx.getVersion());
        assertTrue(LocalDateTime.now().isBefore(mosaicCreationTx.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), mosaicCreationTx.getFee());
        assertEquals(new BigInteger("0"), mosaicCreationTx.getMosaicId().getId());
        assertEquals(true, mosaicCreationTx.getMosaicProperties().isSupplyMutable());
        assertEquals(true, mosaicCreationTx.getMosaicProperties().isTransferable());
        assertEquals(3, mosaicCreationTx.getMosaicProperties().getDivisibility());
        assertEquals(
            BigInteger.valueOf(10).longValue(),
            mosaicCreationTx.getMosaicProperties().getDuration().longValue());
    }

    @Test
    @DisplayName("Serialization")
    void serialization() {
        String expected =
            "8e00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904d410000000000000000010000000000000000000000000000000000000003041027000000000000";
        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransaction.create(
                new FakeDeadline(),
                BigInteger.ZERO,
                MosaicNonce.createFromBigInteger(new BigInteger("0")),
                new MosaicId(new BigInteger("0")),
                MosaicProperties.create(true, true, 4, BigInteger.valueOf(10000)),
                NetworkType.MIJIN_TEST);

        byte[] actual = mosaicDefinitionTransaction.generateBytes();
        assertEquals(expected, Hex.toHexString(actual));
    }

    @Test
    @DisplayName("SerializationEmbeddedBytes")
    void shouldGenerateEmbeddedBytes() {
        String expected =
            "3e00000068b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b76301904d4100000000000000000000000003030a00000000000000";

        NetworkType networkType = NetworkType.MIJIN_TEST;
        Integer version = 1;
        Deadline deadline = new Deadline(2, ChronoUnit.HOURS);
        MosaicId mosaicId = new MosaicId(new BigInteger("0"));
        BigInteger fee = BigInteger.ONE;
        MosaicNonce mosaicNonce = MosaicNonce.createFromBigInteger(new BigInteger("0"));
        MosaicProperties mosaicProperties = MosaicProperties
            .create(true, true, 3, BigInteger.valueOf(10));

        PublicAccount signature = PublicAccount.createFromPublicKey(
            "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763",
            NetworkType.MIJIN_TEST, signSchema);
        TransactionInfo transactionInfo =
            TransactionInfo.createAggregate(
                new BigInteger("121855"),
                1,
                "5A3D23889CD1E800015929A9",
                "3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006",
                "5A0069D83F17CF0001777E55");

        MosaicDefinitionTransaction mosaicCreationTx = new MosaicDefinitionTransaction(networkType,
            version, deadline, fee, mosaicNonce, mosaicId, mosaicProperties, "theSigner",
            signature, transactionInfo);

        byte[] actual = mosaicCreationTx.generateEmbeddedBytes();
        assertEquals(expected, Hex.toHexString(actual));
    }
}
