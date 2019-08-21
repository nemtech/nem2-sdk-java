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

package io.nem.sdk.infrastructure.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.vertx.core.json.JsonObject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LegacyAggregateTransactionTest {

    private final String generationHash =
        "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

    @Test
    void createAAggregateTransactionViaStaticConstructor() {

        TransferTransaction transferTx =
            TransferTransaction.create(
                new Deadline(2, ChronoUnit.HOURS),
                BigInteger.ZERO,
                new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
                    NetworkType.MIJIN_TEST),
                Collections.emptyList(),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTx =
            AggregateTransaction.createComplete(
                new Deadline(2, ChronoUnit.HOURS),
                Arrays.asList(
                    transferTx.toAggregate(
                        new PublicAccount(
                            "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
                            NetworkType.MIJIN_TEST))),
                NetworkType.MIJIN_TEST);

        assertEquals(NetworkType.MIJIN_TEST, aggregateTx.getNetworkType());
        assertEquals(1, (int) aggregateTx.getVersion());
        assertTrue(LocalDateTime.now().isBefore(aggregateTx.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), aggregateTx.getFee());
        assertEquals(1, aggregateTx.getInnerTransactions().size());
    }

    @Test
    @DisplayName("Serialization")
    void serialization() {
        // Generated at nem2-library-js/test/transactions/RegisterNamespaceTransaction.spec.js
        String expected =
            "d100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904141000000000000000001000000000000005500000055000000846b4439154579a5903b1459c9cf69cb8153f6d0110a7a0ed61de29ae4810bf2019054419050b9837efab4bbe8a4b9bb32d812f9885c00d8fc1650e1420100010044b262c46ceabb858096980000000000";

        TransferTransaction transferTx =
            TransferTransaction.create(
                new LegacyFakeDeadline(),
                BigInteger.ZERO,
                new Address("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC", NetworkType.MIJIN_TEST),
                Collections.singletonList(
                    new Mosaic(NetworkCurrencyMosaic.NAMESPACEID, BigInteger.valueOf(10000000))),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTx =
            AggregateTransaction.createComplete(
                new LegacyFakeDeadline(),
                Collections.singletonList(
                    transferTx.toAggregate(
                        new PublicAccount(
                            "846B4439154579A5903B1459C9CF69CB8153F6D0110A7A0ED61DE29AE4810BF2",
                            NetworkType.MIJIN_TEST))),
                NetworkType.MIJIN_TEST);

        byte[] actual = aggregateTx.generateBytes();
        assertEquals(expected, Hex.toHexString(actual));
    }

    @Test
    void shouldCreateAggregateTransactionAndSignWithMultipleCosignatories() {

        TransferTransaction transferTx =
            TransferTransaction.create(
                new LegacyFakeDeadline(),
                BigInteger.ZERO,
                new Address("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC", NetworkType.MIJIN_TEST),
                Collections.emptyList(),
                new PlainMessage("test-message"),
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTx =
            AggregateTransaction.createComplete(
                new LegacyFakeDeadline(),
                Collections.singletonList(
                    transferTx.toAggregate(
                        new PublicAccount(
                            "B694186EE4AB0558CA4AFCFDD43B42114AE71094F5A1FC4A913FE9971CACD21D",
                            NetworkType.MIJIN_TEST))),
                NetworkType.MIJIN_TEST);

        Account cosignatoryAccount =
            new Account(
                "2a2b1f5d366a5dd5dc56c3c757cf4fe6c66e2787087692cf329d7a49a594658b",
                NetworkType.MIJIN_TEST);
        Account cosignatoryAccount2 =
            new Account(
                "b8afae6f4ad13a1b8aad047b488e0738a437c7389d4ff30c359ac068910c1d59",
                NetworkType.MIJIN_TEST); // TODO bug with private key

        SignedTransaction signedTransaction =
            cosignatoryAccount.signTransactionWithCosignatories(
                aggregateTx, Collections.singletonList(cosignatoryAccount2), generationHash);

        assertEquals("2d010000", signedTransaction.getPayload().substring(0, 8));
        assertEquals("5100000051000000", signedTransaction.getPayload().substring(240, 256));
        // assertEquals("039054419050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1420D000000746573742D6D65737361676568B3FBB18729C1FDE225C57F8CE080FA828F0067E451A3FD81FA628842B0B763", signedTransaction.getPayload().substring(320, 474));

    }

    @Test
    void shouldFindAccountInAsASignerOfTheTransaction() {
        JsonObject aggregateTransferTransactionDTO =
            new JsonObject(
                "{\"meta\":{\"hash\":\"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E55\",\"index\":0,\"merkleComponentHash\":\"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\":{\"cosignatures\":[{\"signature\":\"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\":\"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\":[3266625578,11],\"maxFee\":[0,0],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\":\"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\":[{\"meta\":{\"aggregateHash\":\"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\":\"5A0069D83F17CF0001777E55\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E56\",\"index\":0},\"transaction\":{\"message\":{\"payload\":\"746573742D6D657373616765\",\"type\":0},\"mosaics\":[{\"amount\":[3863990592,95248],\"id\":[3646934825,3576016193]}],\"recipient\":\"9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142\",\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16724,\"version\":36867}}],\"type\":16705,\"version\":36867}}");

        AggregateTransaction aggregateTransferTransaction =
            (AggregateTransaction) new TransactionMappingLegacy().apply(aggregateTransferTransactionDTO);

        assertTrue(
            aggregateTransferTransaction.signedByAccount(
                PublicAccount.createFromPublicKey(
                    "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
                    NetworkType.MIJIN_TEST)));
        assertTrue(
            aggregateTransferTransaction.signedByAccount(
                PublicAccount.createFromPublicKey(
                    "7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D",
                    NetworkType.MIJIN_TEST)));
        assertFalse(
            aggregateTransferTransaction.signedByAccount(
                PublicAccount.createFromPublicKey(
                    "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF",
                    NetworkType.MIJIN_TEST)));
    }
}