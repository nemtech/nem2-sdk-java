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

package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import java.math.BigInteger;
import java.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AggregateTransactionTest {

    static Account account;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
                NetworkType.MIJIN_TEST);
    }

    @Test
    void serialize() {
        NetworkType networkType = NetworkType.MIJIN_TEST;
        AggregateTransactionCosignature cosignature1 =
            new AggregateTransactionCosignature(
                "AAA9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456111AAA9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456111",
                new PublicAccount(
                    "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456111",
                    networkType));

        AggregateTransactionCosignature cosignature2 =
            new AggregateTransactionCosignature(
                "BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222",
                new PublicAccount(
                    "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222",
                    networkType));

        TransferTransaction transaction1 =
            TransferTransactionFactory.create(
                networkType,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", networkType),
                Arrays.asList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                new PlainMessage("Some Message")).signer(account.getPublicAccount()).build();

        MosaicSupplyChangeTransaction transaction2 =
            MosaicSupplyChangeTransactionFactory.create(
                networkType,
                new MosaicId(new BigInteger("6300565133566699912")),
                MosaicSupplyChangeActionType.INCREASE,
                BigInteger.valueOf(10)).signer(account.getPublicAccount()).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory
            .create(TransactionType.AGGREGATE_BONDED, networkType,
                Arrays.asList(transaction1, transaction2),
                Arrays.asList(cosignature1, cosignature2)).deadline(new FakeDeadline()).build();

        byte[] actual = aggregateTransaction.serialize();

        String expected = "1002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904142000000000000000001000000000000009a000000610000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac0d000100536f6d65204d657373616765672b0000ce5600006400000000000000390000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b2401904d428869746e9b1a7057010a00000000000000610000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac0d000100536f6d65204d657373616765672b0000ce5600006400000000000000390000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b2401904d428869746e9b1a7057010a000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222";
        assertEquals(expected, Hex.toHexString(actual));

    }
}
