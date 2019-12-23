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

import io.nem.catapult.builders.AggregateBondedTransactionBuilder;
import io.nem.catapult.builders.AggregateCompleteTransactionBuilder;
import io.nem.catapult.builders.TransactionBuilderFactory;
import io.nem.sdk.api.BinarySerialization;
import io.nem.sdk.infrastructure.BinarySerializationImpl;
import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AggregateTransactionTest extends AbstractTransactionTester {

    static Account account =
        new Account(
            "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
            NetworkType.MIJIN_TEST);

    @Test
    void serializeEmpty() {
        NetworkType networkType = NetworkType.MIJIN_TEST;
        AggregateTransaction aggregateTransaction = AggregateTransactionFactory
            .create(TransactionType.AGGREGATE_BONDED, networkType, Collections.emptyList(),
                Collections.emptyList()).deadline(new FakeDeadline()).build();
        String expected = "a80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019041420000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

        AggregateTransaction serialized = assertSerialization(expected,
            aggregateTransaction);
        Assertions.assertEquals(0, serialized.getInnerTransactions().size());
        Assertions.assertEquals(0, serialized.getCosignatures().size());
    }

    @Test
    void basicCatbufferDeserialization() {
        String expected =
            "a80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019041420000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

        AggregateBondedTransactionBuilder transactionBuilder = (AggregateBondedTransactionBuilder) TransactionBuilderFactory
            .createTransactionBuilder(SerializationUtils.toDataInput(Hex.decode(expected)));

        Assertions.assertEquals(expected, Hex.toHexString(
            transactionBuilder.serialize()));

        Assertions
            .assertEquals(0, transactionBuilder.getTransactions().size());

    }


    @Test
    void serializeTwoTransaction() {
        NetworkType networkType = NetworkType.MIJIN_TEST;

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
                Collections.emptyList()).deadline(new FakeDeadline()).build();

        BinarySerializationImpl binarySerialization = new BinarySerializationImpl();

        String expected = "6001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414200000000000000000100000000000000e308d7404d8087f995b5c41b671a5c07861e1f8892b10a9cec8526d09e167f52b8000000000000006d000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24000000000190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac010d0000000000672b0000ce560000640000000000000000536f6d65204d65737361676500000041000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001904d428869746e9b1a70570a000000000000000100000000000000";

        Assertions.assertEquals(expected, Hex.toHexString(aggregateTransaction.serialize()));

        AggregateTransaction serialized = assertSerialization(expected, aggregateTransaction);
        Assertions.assertEquals(0, serialized.getCosignatures().size());
        Assertions.assertEquals(2, serialized.getInnerTransactions().size());
        Assertions.assertEquals(transaction1.getType(),
            serialized.getInnerTransactions().get(0).getType());
        Assertions.assertArrayEquals(binarySerialization.serializeEmbedded(transaction1),
            binarySerialization.serializeEmbedded(serialized.getInnerTransactions().get(0)));
        Assertions.assertEquals(transaction2.getType(),
            serialized.getInnerTransactions().get(1).getType());
        Assertions.assertArrayEquals(binarySerialization.serializeEmbedded(transaction2),
            binarySerialization.serializeEmbedded(serialized.getInnerTransactions().get(1)));
    }


    @Test
    void basicCatbufferAggregateSerialization() {
        String expected =
            "6001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414200000000000000000100000000000000e308d7404d8087f995b5c41b671a5c07861e1f8892b10a9cec8526d09e167f52b8000000000000006d000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24000000000190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac010d0000000000672b0000ce560000640000000000000000536f6d65204d65737361676500000041000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001904d428869746e9b1a70570a000000000000000100000000000000";

        BinarySerialization serialization = new BinarySerializationImpl();

        AggregateTransaction deserialize = (AggregateTransaction) serialization
            .deserialize(Hex.decode(expected));

        Assertions.assertEquals(2, deserialize.getInnerTransactions().size());
        Assertions.assertEquals(0, deserialize.getCosignatures().size());

//        AggregateBondedTransactionBuilder transactionBuilder = (AggregateBondedTransactionBuilder) TransactionBuilderFactory
//            .createTransactionBuilder(SerializationUtils.toDataInput(Hex.decode(expected)));

        AggregateBondedTransactionBuilder transactionBuilder = AggregateBondedTransactionBuilder
            .loadFromBinary(SerializationUtils.toDataInput(Hex.decode(expected)));

        Assertions.assertEquals(2, transactionBuilder.getTransactions().size());
        Assertions.assertEquals(0, transactionBuilder.getCosignatures().size());

//        Transaction anotherTransaction = serialization.deserialize(transactionBuilder.serialize());
//
//        Assertions.assertEquals(expected, Hex.toHexString(
//            anotherTransaction.serialize()));

        Assertions.assertEquals(expected, Hex.toHexString(
            deserialize.serialize()));

        Assertions.assertEquals(expected, Hex.toHexString(
            transactionBuilder.serialize()));
    }


    @Test
    void serializeThreeCosignature() {
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

        AggregateTransactionCosignature cosignature3 =
            new AggregateTransactionCosignature(
                "CCC9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222",
                new PublicAccount(
                    "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456333",
                    networkType));

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory
            .create(TransactionType.AGGREGATE_COMPLETE, networkType,
                Collections.emptyList(),
                Arrays.asList(cosignature1, cosignature2, cosignature3))
            .deadline(new FakeDeadline()).build();

        Assertions.assertEquals(3, aggregateTransaction.getCosignatures().size());

        String expected = "c801000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414100000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456111aaa9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456111aaa9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea654561119a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea654562229a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456333ccc9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222";

        AggregateTransaction serialized = assertSerialization(expected,
            aggregateTransaction);

        Assertions.assertEquals(TransactionType.AGGREGATE_COMPLETE, serialized.getType());

        Assertions.assertEquals(0, serialized.getInnerTransactions().size());
        Assertions.assertEquals(3, serialized.getCosignatures().size());
        Assertions.assertEquals(cosignature1.getSigner(),
            serialized.getCosignatures().get(0).getSigner());
        Assertions.assertEquals(cosignature1.getSignature(),
            serialized.getCosignatures().get(0).getSignature().toUpperCase());

        Assertions.assertEquals(cosignature2.getSigner(),
            serialized.getCosignatures().get(1).getSigner());
        Assertions.assertEquals(cosignature2.getSignature(),
            serialized.getCosignatures().get(1).getSignature().toUpperCase());

        Assertions.assertEquals(cosignature3.getSigner(),
            serialized.getCosignatures().get(2).getSigner());
        Assertions.assertEquals(cosignature3.getSignature(),
            serialized.getCosignatures().get(2).getSignature().toUpperCase());
    }

    @Test
    void basicCatbufferAggregateSerialization3Consignatures() {

        String expected = "c801000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414100000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456111aaa9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456111aaa9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea654561119a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea654562229a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456333ccc9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222";

        AggregateCompleteTransactionBuilder transactionBuilder = (AggregateCompleteTransactionBuilder) TransactionBuilderFactory
            .createTransactionBuilder(SerializationUtils.toDataInput(Hex.decode(expected)));

        Assertions.assertEquals(expected, Hex.toHexString(
            transactionBuilder.serialize()));

        Assertions.assertEquals(0, transactionBuilder.getTransactions().size());
        Assertions.assertEquals(3, transactionBuilder.getCosignatures().size());

    }

    @Test
    void serializeTwoTransactionTwoCosignature() {
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

        String expected = "2002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414200000000000000000100000000000000e308d7404d8087f995b5c41b671a5c07861e1f8892b10a9cec8526d09e167f52b8000000000000006d000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24000000000190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac010d0000000000672b0000ce560000640000000000000000536f6d65204d65737361676500000041000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001904d428869746e9b1a70570a0000000000000001000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456111aaa9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456111aaa9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea654561119a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222";

        Assertions.assertEquals(expected, Hex.toHexString(aggregateTransaction.serialize()));

        AggregateTransaction serialized = assertSerialization(expected,
            aggregateTransaction);
        Assertions.assertEquals(TransactionType.AGGREGATE_BONDED, serialized.getType());

        Assertions.assertEquals(2, serialized.getCosignatures().size());
        Assertions.assertEquals(cosignature1.getSigner(),
            serialized.getCosignatures().get(0).getSigner());
        Assertions.assertEquals(cosignature1.getSignature(),
            serialized.getCosignatures().get(0).getSignature().toUpperCase());

        Assertions.assertEquals(cosignature2.getSigner(),
            serialized.getCosignatures().get(1).getSigner());
        Assertions.assertEquals(cosignature2.getSignature(),
            serialized.getCosignatures().get(1).getSignature().toUpperCase());

        BinarySerializationImpl binarySerialization = new BinarySerializationImpl();

        Assertions.assertEquals(2, serialized.getInnerTransactions().size());
        Assertions.assertEquals(transaction1.getType(),
            serialized.getInnerTransactions().get(0).getType());
        Assertions.assertArrayEquals(binarySerialization.serializeEmbedded(transaction1),
            binarySerialization.serializeEmbedded(serialized.getInnerTransactions().get(0)));
        Assertions.assertEquals(transaction2.getType(),
            serialized.getInnerTransactions().get(1).getType());
        Assertions.assertArrayEquals(binarySerialization.serializeEmbedded(transaction2),
            binarySerialization.serializeEmbedded(serialized.getInnerTransactions().get(1)));
    }


    @Test
    void basicCatbufferAggregateSerializationWithCosignatures() {

        String expected2 = "2002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414200000000000000000100000000000000e308d7404d8087f995b5c41b671a5c07861e1f8892b10a9cec8526d09e167f52b8000000000000006d000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24000000000190544190e8febd671dd41bee94ec3ba5831cb608a312c2f203ba84ac010d0000000000672b0000ce560000640000000000000000536f6d65204d65737361676500000041000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b240000000001904d428869746e9b1a70570a0000000000000001000000000000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456111aaa9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456111aaa9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea654561119a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222bbb9366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456222";

        AggregateBondedTransactionBuilder transactionBuilder = (AggregateBondedTransactionBuilder) TransactionBuilderFactory
            .createTransactionBuilder(SerializationUtils.toDataInput(Hex.decode(expected2)));

        Assertions.assertEquals(expected2, Hex.toHexString(
            transactionBuilder.serialize()));

        Assertions.assertEquals(2, transactionBuilder.getTransactions().size());
        Assertions.assertEquals(2, transactionBuilder.getCosignatures().size());


    }


}
