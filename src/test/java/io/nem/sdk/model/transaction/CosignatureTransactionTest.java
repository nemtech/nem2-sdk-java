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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.nem.sdk.infrastructure.TransactionMapping;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CosignatureTransactionTest {
    static Account account;

    @BeforeAll
    public static void setup() {
        account = new Account("26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930", NetworkType.MIJIN_TEST);
    }

    @Test
    void createACosignatureTransactionViaConstructor() throws Exception {

        JsonObject aggregateTransactionDTO = new Gson().fromJson("{\"meta\":{\"hash\":\"671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E55\",\"index\":0,\"merkleComponentHash\":\"81E5E7AE49998802DABC816EC10158D3A7879702FF29084C2C992CD1289877A7\"},\"transaction\":{\"cosignatures\":[{\"signature\":\"5780C8DF9D46BA2BCF029DCC5D3BF55FE1CB5BE7ABCF30387C4637DDEDFC2152703CA0AD95F21BB9B942F3CC52FCFC2064C7B84CF60D1A9E69195F1943156C07\",\"signer\":\"A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630\"}],\"deadline\":[3266625578,11],\"fee\":[0,0],\"signature\":\"939673209A13FF82397578D22CC96EB8516A6760C894D9B7535E3A1E068007B9255CFA9A914C97142A7AE18533E381C846B69D2AE0D60D1DC8A55AD120E2B606\",\"signer\":\"7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D\",\"transactions\":[{\"meta\":{\"aggregateHash\":\"3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006\",\"aggregateId\":\"5A0069D83F17CF0001777E55\",\"height\":[18160,0],\"id\":\"5A0069D83F17CF0001777E56\",\"index\":0},\"transaction\":{\"message\":{\"payload\":\"746573742D6D657373616765\",\"type\":0},\"mosaics\":[{\"amount\":[3863990592,95248],\"id\":[3646934825,3576016193]}],\"recipient\":\"9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142\",\"signer\":\"B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF\",\"type\":16724,\"version\":36867}}],\"type\":16705,\"version\":36867}}", JsonObject.class);

        AggregateTransaction aggregateTransaction = (AggregateTransaction) new TransactionMapping().apply(aggregateTransactionDTO);

        CosignatureTransaction cosignatureTransaction = CosignatureTransaction.create(aggregateTransaction);

        CosignatureSignedTransaction cosignatureSignedTransaction = account.signCosignatureTransaction(cosignatureTransaction);

        assertTrue(aggregateTransaction.getTransactionInfo().get().getHash().isPresent());
        assertEquals(aggregateTransaction.getTransactionInfo().get().getHash().get(), cosignatureSignedTransaction.getParentHash());
        assertEquals("bf3bc39f2292c028cb0ffa438a9f567a7c4d793d2f8522c8deac74befbcb61af6414adf27b2176d6a24fef612aa6db2f562176a11c46ba6d5e05430042cb5705", cosignatureSignedTransaction.getSignature());
        assertEquals("671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96", cosignatureTransaction.getTransactionToCosign().getTransactionInfo().get().getHash().get());

    }

    @Test
    void shouldThrowExceptionWhenTransactionToCosignHasNotBeenAnnunced() throws Exception {

        AggregateTransaction aggregateTransaction = AggregateTransaction.createComplete(new Deadline(2, ChronoUnit.HOURS), Collections.emptyList(), NetworkType.MIJIN_TEST);

        assertThrows(IllegalArgumentException.class, ()->{CosignatureTransaction.create(aggregateTransaction);}, "Transaction to cosign should be announced before being able to cosign it");

    }
}
