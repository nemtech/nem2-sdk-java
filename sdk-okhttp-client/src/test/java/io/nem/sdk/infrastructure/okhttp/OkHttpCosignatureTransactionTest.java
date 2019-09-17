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

package io.nem.sdk.infrastructure.okhttp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.CosignatureTransaction;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OkHttpCosignatureTransactionTest {

    static SignSchema signSchema = SignSchema.DEFAULT;

    static Account account;


    private final JsonHelper jsonHelper = new JsonHelperGson(new JSON().getGson());

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930",
                NetworkType.MIJIN_TEST, signSchema);
    }

    @Test
    void createACosignatureTransactionViaConstructor() {
        TransactionInfoDTO transactionInfoDTO = TestHelperOkHttp.loadCosignatureTransactionInfoDTO(
            "createACosignatureTransactionViaConstructor.json");
        AggregateTransaction aggregateTransaction =
            (AggregateTransaction) new GeneralTransactionMapper(jsonHelper, signSchema)
                .map(transactionInfoDTO);

        CosignatureTransaction cosignatureTransaction =
            CosignatureTransaction.create(aggregateTransaction);

        CosignatureSignedTransaction cosignatureSignedTransaction =
            account.signCosignatureTransaction(cosignatureTransaction);

        assertTrue(aggregateTransaction.getTransactionInfo().get().getHash().isPresent());
        assertEquals(
            aggregateTransaction.getTransactionInfo().get().getHash().get(),
            cosignatureSignedTransaction.getParentHash());
        assertEquals(
            "bf3bc39f2292c028cb0ffa438a9f567a7c4d793d2f8522c8deac74befbcb61af6414adf27b2176d6a24fef612aa6db2f562176a11c46ba6d5e05430042cb5705",
            cosignatureSignedTransaction.getSignature());
        assertEquals(
            "671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96",
            cosignatureTransaction.getTransactionToCosign().getTransactionInfo().get().getHash()
                .get());
    }


    @Test
    void shouldThrowExceptionWhenTransactionToCosignHasNotBeenAnnunced() throws Exception {

        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                Deadline.create(2, ChronoUnit.HOURS), BigInteger.ZERO, Collections.emptyList(),
                NetworkType.MIJIN_TEST);

        assertThrows(
            IllegalArgumentException.class,
            () -> {
                CosignatureTransaction.create(aggregateTransaction);
            },
            "Transaction to cosign should be announced before being able to cosign it");
    }
}
