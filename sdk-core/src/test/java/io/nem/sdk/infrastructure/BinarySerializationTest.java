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

package io.nem.sdk.infrastructure;


import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.FakeDeadline;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link BinarySerializationImpl}
 */
class BinarySerializationTest {

    @Test
    void testAllTransactionAreHandled() {
        BinarySerializationImpl binarySerialization = new BinarySerializationImpl();
        List<TransactionType> notHandledTransactionTypes = Arrays.stream(TransactionType.values())
            .filter(t -> {
                try {
                    Assertions.assertNotNull(binarySerialization.resolveMapper(t));
                    return false;
                } catch (UnsupportedOperationException e) {
                    return true;
                }

            }).collect(Collectors.toList());

        Assertions.assertTrue(notHandledTransactionTypes.isEmpty(),
            "The following transaction types are not handled: \n" + notHandledTransactionTypes
                .stream().map(TransactionType::toString).collect(Collectors.joining("\n")));

    }

    @Test
    void testSerializationDeserialization() {
        BinarySerializationImpl binarySerialization = new BinarySerializationImpl();
        TransferTransaction transaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                    new Mosaic(
                        new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))),
                new PlainMessage("Some Message")).deadline(new FakeDeadline()).build();

        byte[] serialize = binarySerialization.serialize(transaction);
        Assertions.assertNotNull(serialize);

        TransferTransaction deserializedTransaction = (TransferTransaction) binarySerialization
            .deserialize(serialize);
        Assertions.assertNotNull(deserializedTransaction);

        Assertions.assertEquals("Some Message",
            deserializedTransaction.getMessage().getPayload());

    }

}
