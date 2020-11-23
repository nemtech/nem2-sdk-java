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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class VotingKeyLinkTransactionTest extends AbstractTransactionTester {

  static Account account =
      new Account(
          "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
          NetworkType.MIJIN_TEST);

  static PublicKey publicKey = PublicKey.fromHexString("AAAA");

  @Test
  void create() {
    VotingKeyLinkTransaction transaction =
        VotingKeyLinkTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                publicKey,
                1,
                3,
                LinkAction.LINK)
            .build();
    assertEquals(LinkAction.LINK, transaction.getLinkAction());
    assertEquals(publicKey.toHex(), transaction.getLinkedPublicKey().toHex());

    assertEquals(1L, transaction.getStartEpoch());
    assertEquals(3L, transaction.getEndEpoch());
    assertEquals(2, transaction.getVersion());
  }

  @Test
  void shouldGenerateBytes() {

    VotingKeyLinkTransaction transaction =
        VotingKeyLinkTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                publicKey,
                1,
                3,
                LinkAction.LINK)
            .signer(account.getPublicAccount())
            .build();

    String expected =
        "A90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E6000000000290434100000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000AAAA010000000300000001";
    assertEquals(2, assertSerialization(expected, transaction).getVersion());

    String expectedEmbeddedHash =
        "5900000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000002904341000000000000000000000000000000000000000000000000000000000000AAAA010000000300000001";
    assertEquals(2, assertEmbeddedSerialization(expectedEmbeddedHash, transaction).getVersion());
  }
}
