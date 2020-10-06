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
package io.nem.symbol.sdk.infrastructure.vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.CosignatureTransaction;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.vertx.core.json.Json;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class VertxCosignatureTransactionTest {

  static Account account;

  private final JsonHelper jsonHelper =
      new JsonHelperJackson2(JsonHelperJackson2.configureMapper(Json.mapper));

  @BeforeAll
  public static void setup() {
    account =
        new Account(
            "26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930",
            NetworkType.MIJIN_TEST);
  }

  @Test
  void createACosignatureTransactionViaConstructor() {
    TransactionInfoDTO transactionInfoDTO =
        TestHelperVertx.loadTransactionInfoDTO("cosignatureTransactionViaConstructor.json");
    AggregateTransaction aggregateTransaction =
        (AggregateTransaction)
            new GeneralTransactionMapper(jsonHelper).mapFromDto(transactionInfoDTO);

    CosignatureTransaction cosignatureTransaction =
        CosignatureTransaction.create(aggregateTransaction);

    CosignatureSignedTransaction cosignatureSignedTransaction =
        account.signCosignatureTransaction(cosignatureTransaction);

    assertTrue(aggregateTransaction.getTransactionInfo().get().getHash().isPresent());
    assertEquals(
        aggregateTransaction.getTransactionInfo().get().getHash().get(),
        cosignatureSignedTransaction.getParentHash());
    assertEquals(
        "5EA75D1A2C8AD25DA4F400C1BD2DA84449FAF583AFD813E1179E72AF0CDF5AC1C0F7404AF6FC7268EE416204240DD3D5B11420D80215F19AA314FC86D6E03E0D",
        cosignatureSignedTransaction.getSignature());
    assertEquals(
        "671653C94E2254F2A23EFEDB15D67C38332AED1FBD24B063C0A8E675582B6A96",
        cosignatureTransaction.getTransactionToCosign().getTransactionInfo().get().getHash().get());
  }

  @Test
  void shouldThrowExceptionWhenTransactionToCosignHasNotBeenAnnunced() throws Exception {

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                NetworkType.MIJIN_TEST, new Deadline(BigInteger.ONE), Collections.emptyList())
            .build();

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          CosignatureTransaction.create(aggregateTransaction);
        },
        "Transaction to cosign should be announced before being able to cosign it");
  }
}
