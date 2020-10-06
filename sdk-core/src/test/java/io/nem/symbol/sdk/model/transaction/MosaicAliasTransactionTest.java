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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

/** Tests for the {@link MosaicAliasTransaction} and the factory. */
public class MosaicAliasTransactionTest extends AbstractTransactionTester {

  private static Account account =
      new Account(
          "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
          NetworkType.MIJIN_TEST);

  @Test
  void shouldBuild() {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    NamespaceId namespaceId = NamespaceId.createFromName("anamespaced");
    MosaicAliasTransaction transaction =
        MosaicAliasTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                AliasAction.LINK,
                namespaceId,
                mosaicId)
            .build();

    assertEquals(NetworkType.MIJIN_TEST, transaction.getNetworkType());
    assertEquals(AliasAction.LINK, transaction.getAliasAction());
    assertEquals(mosaicId, transaction.getMosaicId());
    assertEquals(namespaceId, transaction.getNamespaceId());
  }

  @Test
  void serialize() {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    NamespaceId namespaceId = NamespaceId.createFromName("anamespaced");
    MosaicAliasTransaction transaction =
        MosaicAliasTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new Deadline(BigInteger.ONE),
                AliasAction.LINK,
                namespaceId,
                mosaicId)
            .signer(account.getPublicAccount())
            .build();

    String expectedHash =
        "910000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60000000001904e4300000000000000000100000000000000a487791451fdf1b60a0000000000000001";
    assertSerialization(expectedHash, transaction);

    String expectedEmbeddedHash =
        "4100000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60000000001904e43a487791451fdf1b60a0000000000000001";
    assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
  }
}
