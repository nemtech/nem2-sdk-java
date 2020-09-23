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
package io.nem.symbol.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MosaicNonceTest {

  @Test
  void createRandomNonce() {
    MosaicNonce nonce = MosaicNonce.createRandom();
    assertEquals(Integer.toUnsignedLong(nonce.getNonceAsInt()), nonce.getNonceAsLong());
  }

  @Test
  void createRandomNonceTwiceNotTheSame() {
    MosaicNonce nonce1 = MosaicNonce.createRandom();
    MosaicNonce nonce2 = MosaicNonce.createRandom();
    assertNotEquals(nonce1, nonce2);
    assertEquals(nonce2, nonce2);
  }

  @Test
  void createNonceFromHexadecimalStringTwiceNotTheSame() {
    MosaicNonce nonce1 = MosaicNonce.createFromInteger(0);
    assertEquals(0L, nonce1.getNonceAsLong());

    MosaicNonce nonce2 = MosaicNonce.createFromInteger((int) 4294967295L);
    assertEquals(4294967295L, nonce2.getNonceAsLong());
    assertEquals(-1, nonce2.getNonceAsInt());
    assertNotEquals(nonce1, nonce2);

    MosaicNonce nonce3 = MosaicNonce.createFromInteger(1234);
    assertEquals(1234, nonce3.getNonceAsLong());
    assertEquals(1234, nonce3.getNonceAsInt());
    assertNotEquals(nonce1, nonce3);
  }

  @Test
  void createNonceFromBigInteger() {
    MosaicNonce nonce1 = MosaicNonce.createFromBigInteger(new BigInteger("0"));
    MosaicNonce nonce2 = MosaicNonce.createFromBigInteger(new BigInteger("4294967295"));
    MosaicNonce nonce3 = MosaicNonce.createFromBigInteger(new BigInteger("4294967295"));
    assertEquals(0, nonce1.getNonceAsLong());
    long actual = 4294967295L;
    assertEquals(nonce2.getNonceAsLong(), actual);
    assertNotEquals(nonce1, nonce2);
    assertEquals(nonce3, nonce2);
  }

  @Test
  void createMosaicNonceFromInteger() {
    assertFromInt((int) 4294967295L);
    assertFromInt(1);
    assertFromInt(2);
    assertFromInt(3);
    assertFromInt(4);
    assertFromInt(67305985);
    assertFromInt((int) 3310277026L);
  }

  private void assertFromInt(int nonceNumber) {

    Assertions.assertEquals(
        nonceNumber, MosaicNonce.createFromInteger(nonceNumber).getNonceAsInt());

    Assertions.assertEquals(
        Integer.toUnsignedLong(nonceNumber),
        MosaicNonce.createFromBigInteger(BigInteger.valueOf(nonceNumber)).getNonceAsLong());

    Assertions.assertEquals(
        Integer.toUnsignedLong(nonceNumber),
        MosaicNonce.createFromInteger(nonceNumber).getNonceAsLong());

    Assertions.assertEquals(
        nonceNumber,
        MosaicNonce.createFromBigInteger(BigInteger.valueOf(nonceNumber)).getNonceAsInt());
  }
}
