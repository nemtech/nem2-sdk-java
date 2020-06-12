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

package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.catapult.builders.AggregateBondedTransactionBuilder;
import io.nem.symbol.catapult.builders.AggregateCompleteTransactionBuilder;
import io.nem.symbol.catapult.builders.TransactionBuilderFactory;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.BinarySerialization;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
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

        Assertions.assertThrows(IllegalArgumentException.class, () -> assertSerialization(expected,
            aggregateTransaction));
    }

    @Test
    void basicCatbufferDeserialization() {
        String expected =
            "A80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000019041420000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

        AggregateBondedTransactionBuilder transactionBuilder = (AggregateBondedTransactionBuilder) TransactionBuilderFactory
            .createTransactionBuilder(
                SerializationUtils.toDataInput(ConvertUtils.fromHexToBytes(expected)));

        Assertions.assertEquals(expected, ConvertUtils.toHex(
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
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBF", networkType),
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

        String expected = "6001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414200000000000000000100000000000000B4C97320255A2F755F6BE2F4DDAC0BB3EBDD25508DBE460EA6988366F404706AB8000000000000006D00000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E6000000000190544190E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA840D00010000000000672B0000CE560000640000000000000000536F6D65204D6573736167650000004100000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001904D428869746E9B1A70570A000000000000000100000000000000";

        Assertions.assertEquals(expected, ConvertUtils.toHex(aggregateTransaction.serialize()));

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
            "6001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414200000000000000000100000000000000B4C97320255A2F755F6BE2F4DDAC0BB3EBDD25508DBE460EA6988366F404706AB8000000000000006D00000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E6000000000190544190E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA840D00010000000000672B0000CE560000640000000000000000536F6D65204D6573736167650000004100000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001904D428869746E9B1A70570A000000000000000100000000000000";

        BinarySerialization serialization = new BinarySerializationImpl();

        AggregateTransaction deserialize = (AggregateTransaction) serialization
            .deserialize(ConvertUtils.fromHexToBytes(expected));

        Assertions.assertEquals(2, deserialize.getInnerTransactions().size());
        Assertions.assertEquals(0, deserialize.getCosignatures().size());

        AggregateBondedTransactionBuilder transactionBuilder = AggregateBondedTransactionBuilder
            .loadFromBinary(SerializationUtils.toDataInput(ConvertUtils.fromHexToBytes(expected)));

        Assertions.assertEquals(2, transactionBuilder.getTransactions().size());
        Assertions.assertEquals(0, transactionBuilder.getCosignatures().size());

        Assertions.assertEquals(expected, ConvertUtils.toHex(
            deserialize.serialize()));

        Assertions.assertEquals(expected, ConvertUtils.toHex(
            transactionBuilder.serialize()));
    }

    @Test
    void maxFeeThreeCosignature() {
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

        TransferTransaction transaction1 =
            TransferTransactionFactory.create(
                networkType,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBF", networkType),
                Arrays.asList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                new PlainMessage("Some Message")).signer(account.getPublicAccount()).build();

        AggregateTransactionFactory aggregateTransaction = AggregateTransactionFactory
            .create(TransactionType.AGGREGATE_COMPLETE, networkType,
                Collections.singletonList(transaction1),
                Arrays.asList(cosignature1, cosignature2, cosignature3));

        int multiplier = 10;
        Assertions.assertThrows(IllegalArgumentException.class, () -> aggregateTransaction
            .calculateMaxFeeFromMultiplier(multiplier));

        aggregateTransaction.calculateMaxFeeForAggregate(multiplier, 1);
        Assertions.assertEquals(BigInteger.valueOf(aggregateTransaction.getSize() * multiplier),
            aggregateTransaction.getMaxFee());

        aggregateTransaction.calculateMaxFeeForAggregate(multiplier, 2);
        Assertions.assertEquals(BigInteger.valueOf(aggregateTransaction.getSize() * multiplier),
            aggregateTransaction.getMaxFee());

        aggregateTransaction.calculateMaxFeeForAggregate(multiplier, 3);
        Assertions.assertEquals(BigInteger.valueOf(aggregateTransaction.getSize() * multiplier),
            aggregateTransaction.getMaxFee());

        aggregateTransaction.calculateMaxFeeForAggregate(multiplier, 4);
        Assertions
            .assertEquals(BigInteger.valueOf(multiplier * (aggregateTransaction.getSize() + AggregateTransactionFactory.COSIGNATURE_SIZE)),
                aggregateTransaction.getMaxFee());

        aggregateTransaction.calculateMaxFeeForAggregate(multiplier, 5);
        Assertions
            .assertEquals(BigInteger.valueOf(
                multiplier * (aggregateTransaction.getSize() + AggregateTransactionFactory.COSIGNATURE_SIZE * 2)),
                aggregateTransaction.getMaxFee());


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

        IllegalArgumentException exception = Assertions
            .assertThrows(IllegalArgumentException.class, () -> assertSerialization("", aggregateTransaction));
        Assertions.assertEquals("Partially loaded and incomplete transactions cannot be serialized.", exception.getMessage());

    }

    @Test
    void basicCatbufferAggregateSerialization3Consignatures() {

        String expected = "E0010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904141000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456111AAA9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456111AAA9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA6545611100000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA6545622200000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456333CCC9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222";

        AggregateCompleteTransactionBuilder transactionBuilder = (AggregateCompleteTransactionBuilder) TransactionBuilderFactory
            .createTransactionBuilder(
                SerializationUtils.toDataInput(ConvertUtils.fromHexToBytes(expected)));

        Assertions.assertEquals(expected, ConvertUtils.toHex(
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
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBF", networkType),
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

        String expected = "3002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414200000000000000000100000000000000B4C97320255A2F755F6BE2F4DDAC0BB3EBDD25508DBE460EA6988366F404706AB8000000000000006D00000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E6000000000190544190E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA840D00010000000000672B0000CE560000640000000000000000536F6D65204D6573736167650000004100000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001904D428869746E9B1A70570A00000000000000010000000000000000000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456111AAA9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456111AAA9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA6545611100000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222";

        Assertions.assertEquals(expected,
            ConvertUtils.toHex(aggregateTransaction.serialize()));

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

        String expected2 = "3002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190414200000000000000000100000000000000B4C97320255A2F755F6BE2F4DDAC0BB3EBDD25508DBE460EA6988366F404706AB8000000000000006D00000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E6000000000190544190E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA840D00010000000000672B0000CE560000640000000000000000536F6D65204D6573736167650000004100000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001904D428869746E9B1A70570A00000000000000010000000000000000000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456111AAA9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456111AAA9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA6545611100000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222BBB9366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456222";

        AggregateBondedTransactionBuilder transactionBuilder = (AggregateBondedTransactionBuilder) TransactionBuilderFactory
            .createTransactionBuilder(
                SerializationUtils.toDataInput(ConvertUtils.fromHexToBytes(expected2)));

        Assertions.assertEquals(expected2, ConvertUtils.toHex(
            transactionBuilder.serialize()));

        Assertions.assertEquals(2, transactionBuilder.getTransactions().size());
        Assertions.assertEquals(2, transactionBuilder.getCosignatures().size());


    }


}
