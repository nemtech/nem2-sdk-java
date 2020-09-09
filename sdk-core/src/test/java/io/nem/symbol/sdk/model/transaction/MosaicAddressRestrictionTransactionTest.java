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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MosaicAddressRestrictionTransactionTest extends AbstractTransactionTester {

  static Account account =
      new Account(
          "26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930",
          NetworkType.MIJIN_TEST);
  static String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

  @Test
  void createAMosaicAddressRestrictionTransactionViaStaticConstructor() {
    MosaicAddressRestrictionTransaction transaction =
        MosaicAddressRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("0")), // restricted
                // MosaicId
                BigInteger.valueOf(1), // restrictionKey
                account.getAddress(), // targetAddress
                // previousRestrictionValue
                BigInteger.valueOf(8) // newRestrictionValue
                )
            .previousRestrictionValue(BigInteger.valueOf(9))
            .build();

    assertEquals(NetworkType.MIJIN_TEST, transaction.getNetworkType());
    assertEquals(1, (int) transaction.getVersion());
    assertTrue(LocalDateTime.now().isBefore(transaction.getDeadline().getLocalDateTime()));
    assertEquals(BigInteger.valueOf(0), transaction.getMaxFee());
    assertEquals(new BigInteger("0"), transaction.getMosaicId().getId());
    assertEquals(BigInteger.valueOf(1), transaction.getRestrictionKey());
    assertEquals(account.getAddress(), transaction.getTargetAddress());
    assertEquals(BigInteger.valueOf(9), transaction.getPreviousRestrictionValue());
    assertEquals(BigInteger.valueOf(8), transaction.getNewRestrictionValue());
  }

  @Test
  void serializeAndSignTransaction() {
    MosaicAddressRestrictionTransaction transaction =
        MosaicAddressRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("1")), // restricted
                // MosaicId
                BigInteger.valueOf(1), // restrictionKey
                account.getAddress(), // targetAddress
                BigInteger.valueOf(8) // newRestrictionValue
                )
            .previousRestrictionValue(BigInteger.valueOf(9))
            .deadline(new FakeDeadline())
            .build();

    SignedTransaction signedTransaction = transaction.signWith(account, generationHash);

    assertEquals(
        "B8000000000000006EFCD6CE2F8C961035F6BBA96EB2A8BA6B3C652A71A735E1E5ED7145B2947F2F1A9A0FDFC1BFA067744D8A3E8CB0B35394C16B37B24DE337A4B26173609DB2049801508C58666C746F471538E43002B85B1CD542F9874B2861183919BA8787B6000000000190514200000000000000000100000000000000010000000000000001000000000000000900000000000000080000000000000090D66C33420E5411995BACFCA2B28CF1C9F5DD7AB1204EA4",
        signedTransaction.getPayload());
  }

  @Test
  void serialize() {
    MosaicAddressRestrictionTransaction transaction =
        MosaicAddressRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("1")), // restricted
                // MosaicId
                BigInteger.valueOf(1), // restrictionKey
                account.getAddress(), // targetAddress
                BigInteger.valueOf(8) // newRestrictionValue
                )
            .previousRestrictionValue(BigInteger.valueOf(9))
            .deadline(new FakeDeadline())
            .signer(account.getPublicAccount())
            .build();

    String expected =
        "B800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009801508C58666C746F471538E43002B85B1CD542F9874B2861183919BA8787B6000000000190514200000000000000000100000000000000010000000000000001000000000000000900000000000000080000000000000090D66C33420E5411995BACFCA2B28CF1C9F5DD7AB1204EA4";
    assertSerialization(expected, transaction);

    String expectedEmbeddedHash =
        "68000000000000009801508C58666C746F471538E43002B85B1CD542F9874B2861183919BA8787B60000000001905142010000000000000001000000000000000900000000000000080000000000000090D66C33420E5411995BACFCA2B28CF1C9F5DD7AB1204EA4";
    assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
  }
}
