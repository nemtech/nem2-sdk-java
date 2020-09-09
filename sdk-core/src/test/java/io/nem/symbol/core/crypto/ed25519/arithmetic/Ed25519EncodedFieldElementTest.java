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
package io.nem.symbol.core.crypto.ed25519.arithmetic;

import io.nem.symbol.sdk.infrastructure.RandomUtils;
import java.math.BigInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Ed25519EncodedFieldElementTest {

  // region constructor

  @Test
  public void canBeCreatedFromByteArrayWithLengthThirtyTwo() {
    Ed25519EncodedFieldElement element = new Ed25519EncodedFieldElement(new byte[32]);
    // Assert:
    Assertions.assertEquals(32, element.getRaw().length);
  }

  @Test
  public void canBeCreatedFromByteArrayWithLengthSixtyFour() {
    Ed25519EncodedFieldElement element = new Ed25519EncodedFieldElement(new byte[64]);

    // Assert:
    Assertions.assertEquals(64, element.getRaw().length);
  }

  @Test
  public void cannotBeCreatedFromArrayWithIncorrectLength() {
    // Assert:
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new Ed25519EncodedFieldElement(new byte[50]));
  }

  // endregion

  // region isNonZero

  @Test
  public void isNonZeroReturnsFalseIfEncodedFieldElementIsZero() {
    // Act:
    final Ed25519EncodedFieldElement encoded = new Ed25519EncodedFieldElement(new byte[32]);

    // Assert:
    MatcherAssert.assertThat(encoded.isNonZero(), IsEqual.equalTo(false));
  }

  @Test
  public void isNonZeroReturnsTrueIfEncodedFieldElementIsNonZero() {
    // Act:
    final byte[] values = new byte[32];
    values[0] = 5;
    final Ed25519EncodedFieldElement encoded = new Ed25519EncodedFieldElement(values);

    // Assert:
    MatcherAssert.assertThat(encoded.isNonZero(), IsEqual.equalTo(true));
  }

  // endregion

  // region getRaw

  @Test
  public void getRawReturnsUnderlyingArray() {
    // Act:
    final byte[] values = new byte[32];
    values[0] = 5;
    values[6] = 15;
    values[23] = -67;
    final Ed25519EncodedFieldElement encoded = new Ed25519EncodedFieldElement(values);

    // Assert:
    MatcherAssert.assertThat(values, IsEqual.equalTo(encoded.getRaw()));
  }

  // endregion

  // region encode / decode

  @Test
  public void decodePlusEncodeDoesNotAlterTheEncodedFieldElement() {
    // Act:
    for (int i = 0; i < 1000; i++) {
      // Arrange:
      final Ed25519EncodedFieldElement original = MathUtils.getRandomEncodedFieldElement(32);
      final Ed25519EncodedFieldElement encoded = original.decode().encode();

      // Assert:
      MatcherAssert.assertThat(encoded, IsEqual.equalTo(original));
    }
  }

  // endregion

  // region modulo group order arithmetic

  @Test
  public void modQReturnsExpectedResult() {
    for (int i = 0; i < 1000; i++) {
      // Arrange:
      final Ed25519EncodedFieldElement encoded =
          new Ed25519EncodedFieldElement(MathUtils.getRandomByteArray(64));

      // Act:
      final Ed25519EncodedFieldElement reduced1 = encoded.modQ();
      final Ed25519EncodedFieldElement reduced2 = MathUtils.reduceModGroupOrder(encoded);

      // Assert:
      MatcherAssert.assertThat(
          MathUtils.toBigInteger(reduced1).compareTo(Ed25519Field.P), IsEqual.equalTo(-1));
      MatcherAssert.assertThat(
          MathUtils.toBigInteger(reduced1).compareTo(new BigInteger("-1")), IsEqual.equalTo(1));
      MatcherAssert.assertThat(reduced1, IsEqual.equalTo(reduced2));
    }
  }

  @Test
  public void multiplyAndAddModQReturnsExpectedResult() {
    for (int i = 0; i < 1000; i++) {
      // Arrange:
      final Ed25519EncodedFieldElement encoded1 = MathUtils.getRandomEncodedFieldElement(32);
      final Ed25519EncodedFieldElement encoded2 = MathUtils.getRandomEncodedFieldElement(32);
      final Ed25519EncodedFieldElement encoded3 = MathUtils.getRandomEncodedFieldElement(32);

      // Act:
      final Ed25519EncodedFieldElement result1 = encoded1.multiplyAndAddModQ(encoded2, encoded3);
      final Ed25519EncodedFieldElement result2 =
          MathUtils.multiplyAndAddModGroupOrder(encoded1, encoded2, encoded3);

      // Assert:
      MatcherAssert.assertThat(
          MathUtils.toBigInteger(result1).compareTo(Ed25519Field.P), IsEqual.equalTo(-1));
      MatcherAssert.assertThat(
          MathUtils.toBigInteger(result1).compareTo(new BigInteger("-1")), IsEqual.equalTo(1));
      MatcherAssert.assertThat(result1, IsEqual.equalTo(result2));
    }
  }

  // endregion

  // region encode

  @Test
  public void encodeReturnsCorrectByteArrayForSimpleFieldElements() {
    // Arrange:
    final int[] t1 = new int[10];
    final int[] t2 = new int[10];
    t2[0] = 1;
    final Ed25519FieldElement fieldElement1 = new Ed25519FieldElement(t1);
    final Ed25519FieldElement fieldElement2 = new Ed25519FieldElement(t2);

    // Act:
    final Ed25519EncodedFieldElement encoded1 = fieldElement1.encode();
    final Ed25519EncodedFieldElement encoded2 = fieldElement2.encode();

    // Assert:
    MatcherAssert.assertThat(
        encoded1, IsEqual.equalTo(MathUtils.toEncodedFieldElement(BigInteger.ZERO)));
    MatcherAssert.assertThat(
        encoded2, IsEqual.equalTo(MathUtils.toEncodedFieldElement(BigInteger.ONE)));
  }

  @Test
  public void encodeReturnsCorrectByteArrayIfJthBitOfTiIsSetToOne() {
    for (int i = 0; i < 10; i++) {
      // Arrange:
      final int[] t = new int[10];
      for (int j = 0; j < 24; j++) {
        t[i] = 1 << j;
        final Ed25519FieldElement fieldElement = new Ed25519FieldElement(t);
        final BigInteger b = MathUtils.toBigInteger(t).mod(Ed25519Field.P);

        // Act:
        final Ed25519EncodedFieldElement encoded = fieldElement.encode();

        // Assert:
        MatcherAssert.assertThat(encoded, IsEqual.equalTo(MathUtils.toEncodedFieldElement(b)));
      }
    }
  }

  @Test
  public void encodeReturnsCorrectByteArray() {
    for (int i = 0; i < 10000; i++) {
      // Arrange:
      final int[] t = new int[10];
      for (int j = 0; j < 10; j++) {
        t[j] = RandomUtils.generateRandomInt(1 << 28) - (1 << 27);
      }
      final Ed25519FieldElement fieldElement = new Ed25519FieldElement(t);
      final BigInteger b = MathUtils.toBigInteger(t);

      // Act:
      final Ed25519EncodedFieldElement encoded = fieldElement.encode();

      // Assert:
      MatcherAssert.assertThat(
          encoded, IsEqual.equalTo(MathUtils.toEncodedFieldElement(b.mod(Ed25519Field.P))));
    }
  }

  // region isNegative

  @Test
  public void isNegativeReturnsCorrectResult() {
    for (int i = 0; i < 10000; i++) {
      // Arrange:
      final byte[] values = RandomUtils.generateRandomBytes(32);
      values[31] &= 0x7F;
      final Ed25519EncodedFieldElement encoded = new Ed25519EncodedFieldElement(values);
      final boolean isNegative =
          MathUtils.toBigInteger(encoded)
              .mod(Ed25519Field.P)
              .mod(new BigInteger("2"))
              .equals(BigInteger.ONE);

      // Assert:
      MatcherAssert.assertThat(encoded.isNegative(), IsEqual.equalTo(isNegative));
    }
  }

  // endregion

  // region hashCode / equals

  @Test
  public void equalsOnlyReturnsTrueForEquivalentObjects() {
    // Arrange:
    final Ed25519EncodedFieldElement encoded1 = MathUtils.getRandomEncodedFieldElement(32);
    final Ed25519EncodedFieldElement encoded2 = encoded1.decode().encode();
    final Ed25519EncodedFieldElement encoded3 = MathUtils.getRandomEncodedFieldElement(32);
    final Ed25519EncodedFieldElement encoded4 = MathUtils.getRandomEncodedFieldElement(32);

    // Assert:
    MatcherAssert.assertThat(encoded1, IsEqual.equalTo(encoded2));
    MatcherAssert.assertThat(encoded1, IsNot.not(IsEqual.equalTo(encoded3)));
    MatcherAssert.assertThat(encoded1, IsNot.not(IsEqual.equalTo(encoded4)));
    MatcherAssert.assertThat(encoded3, IsNot.not(IsEqual.equalTo(encoded4)));
  }

  @Test
  public void hashCodesAreEqualForEquivalentObjects() {
    // Arrange:
    final Ed25519EncodedFieldElement encoded1 = MathUtils.getRandomEncodedFieldElement(32);
    final Ed25519EncodedFieldElement encoded2 = encoded1.decode().encode();
    final Ed25519EncodedFieldElement encoded3 = MathUtils.getRandomEncodedFieldElement(32);
    final Ed25519EncodedFieldElement encoded4 = MathUtils.getRandomEncodedFieldElement(32);

    // Assert:
    MatcherAssert.assertThat(encoded1.hashCode(), IsEqual.equalTo(encoded2.hashCode()));
    MatcherAssert.assertThat(encoded1.hashCode(), IsNot.not(IsEqual.equalTo(encoded3.hashCode())));
    MatcherAssert.assertThat(encoded1.hashCode(), IsNot.not(IsEqual.equalTo(encoded4.hashCode())));
    MatcherAssert.assertThat(encoded3.hashCode(), IsNot.not(IsEqual.equalTo(encoded4.hashCode())));
  }

  // endregion

  // region toString

  @Test
  public void toStringReturnsCorrectRepresentation() {
    // Arrange:
    final byte[] bytes = new byte[32];
    for (int i = 0; i < 32; i++) {
      bytes[i] = (byte) (i + 1);
    }
    final Ed25519EncodedFieldElement encoded = new Ed25519EncodedFieldElement(bytes);

    // Act:
    final String encodedAsString = encoded.toString();
    final StringBuilder builder = new StringBuilder();
    for (final byte b : bytes) {
      builder.append(String.format("%02X", b));
    }

    // Assert:
    MatcherAssert.assertThat(encodedAsString, IsEqual.equalTo(builder.toString()));
  }

  // endregion
}
