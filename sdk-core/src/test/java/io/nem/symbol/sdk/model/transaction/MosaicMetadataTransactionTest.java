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
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Tests for the {@link MosaicMetadataTransaction} and the factory. */
public class MosaicMetadataTransactionTest extends AbstractTransactionTester {

  private static Account account;

  @BeforeAll
  public static void setup() {
    account =
        new Account(
            "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
            NetworkType.MIJIN_TEST);
  }

  @Test
  void shouldBuild() {
    MosaicId mosaicId = new MosaicId(BigInteger.valueOf(1000));
    MosaicMetadataTransaction transaction =
        MosaicMetadataTransactionFactory.create(
                NetworkType.MIJIN_TEST, account.getAddress(), mosaicId, BigInteger.TEN, "123ABC")
            .valueSizeDelta(10)
            .deadline(new FakeDeadline())
            .build();
    assertEquals("123ABC", transaction.getValue());
    assertEquals(mosaicId, transaction.getTargetMosaicId());
    assertEquals(NetworkType.MIJIN_TEST, transaction.getNetworkType());
    assertEquals(10, transaction.getValueSizeDelta());
    assertEquals(BigInteger.TEN, transaction.getScopedMetadataKey());

    assertEquals(account.getAddress(), transaction.getTargetAddress());
  }

  @Test
  void shouldGenerateBytes() {
    MosaicId mosaicId = new MosaicId(BigInteger.valueOf(1000));
    MosaicMetadataTransaction transaction =
        MosaicMetadataTransactionFactory.create(
                NetworkType.MIJIN_TEST, account.getAddress(), mosaicId, BigInteger.TEN, "123ABC")
            .valueSizeDelta(10)
            .signer(account.getPublicAccount())
            .deadline(new FakeDeadline())
            .build();

    String expectedHash =
        "B20000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001904442000000000000000001000000000000009083025FF3A8AB5AD104631FB370F290004952CD1FDDC4C90A00000000000000E8030000000000000A000600313233414243";
    assertSerialization(expectedHash, transaction);

    String expectedEmbeddedHash =
        "6200000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E600000000019044429083025FF3A8AB5AD104631FB370F290004952CD1FDDC4C90A00000000000000E8030000000000000A000600313233414243";
    assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
  }
}
