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

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Ed25519EncodedGroupElementTest {

  @Test
  public void canBeCreatedFromByteArray() {
    Ed25519EncodedGroupElement element = new Ed25519EncodedGroupElement(new byte[32]);

    // Assert:
    Assertions.assertEquals(32, element.getRaw().length);
  }

  public void cannotBeCreatedFromArrayWithIncorrectLength() {
    Ed25519EncodedGroupElement element = new Ed25519EncodedGroupElement(new byte[30]);
    // Assert:
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> Assertions.assertEquals(64, element.getRaw().length));
  }

  // region getRaw

  @Test
  public void getRawReturnsUnderlyingArray() {
    // Act:
    final byte[] values = new byte[32];
    values[0] = 5;
    values[6] = 15;
    values[23] = -67;
    final Ed25519EncodedGroupElement encoded = new Ed25519EncodedGroupElement(values);

    // Assert:
    MatcherAssert.assertThat(values, IsEqual.equalTo(encoded.getRaw()));
  }

  // endregion

  // region encode / decode

  @Test
  public void decodePlusEncodeDoesNotAlterTheEncodedGroupElement() {
    // Act:
    for (int i = 0; i < 1000; i++) {
      // Arrange:
      final Ed25519EncodedGroupElement original = MathUtils.getRandomEncodedGroupElement();
      final Ed25519EncodedGroupElement encoded = original.decode().encode();

      // Assert:
      MatcherAssert.assertThat(encoded, IsEqual.equalTo(original));
    }
  }

  // endregion

  @Test
  public void getAffineXReturnsExpectedResult() {
    for (int i = 0; i < 1000; i++) {
      // Arrange:
      final Ed25519EncodedGroupElement encoded = MathUtils.getRandomGroupElement().encode();

      // Act:
      final Ed25519FieldElement affineX1 = encoded.getAffineX();
      final Ed25519FieldElement affineX2 =
          MathUtils.toRepresentation(encoded.decode(), CoordinateSystem.AFFINE).getX();

      // Assert:
      MatcherAssert.assertThat(affineX1, IsEqual.equalTo(affineX2));
    }
  }

  @Test
  public void getAffineXThrowsIfEncodedGroupElementIsInvalid() {
    // Arrange:
    final Ed25519GroupElement g =
        Ed25519GroupElement.p2(Ed25519Field.ONE, Ed25519Field.D, Ed25519Field.ONE);
    final Ed25519EncodedGroupElement encoded = g.encode();

    // Assert:
    Assertions.assertThrows(IllegalArgumentException.class, encoded::getAffineX);
  }

  @Test
  public void getAffineYReturnsExpectedResult() {
    for (int i = 0; i < 1000; i++) {
      // Arrange:
      final Ed25519EncodedGroupElement encoded = MathUtils.getRandomEncodedGroupElement();

      // Act:
      final Ed25519FieldElement affineY1 = encoded.getAffineY();
      final Ed25519FieldElement affineY2 =
          MathUtils.toRepresentation(encoded.decode(), CoordinateSystem.AFFINE).getY();

      // Assert:
      MatcherAssert.assertThat(affineY1, IsEqual.equalTo(affineY2));
    }
  }

  // region hashCode / equals

  @Test
  public void equalsOnlyReturnsTrueForEquivalentObjects() {
    // Arrange:
    final Ed25519EncodedGroupElement g1 = MathUtils.getRandomEncodedGroupElement();
    final Ed25519EncodedGroupElement g2 = g1.decode().encode();
    final Ed25519EncodedGroupElement g3 = MathUtils.getRandomEncodedGroupElement();
    final Ed25519EncodedGroupElement g4 = MathUtils.getRandomEncodedGroupElement();

    // Assert
    MatcherAssert.assertThat(g2, IsEqual.equalTo(g1));
    MatcherAssert.assertThat(g1, IsNot.not(IsEqual.equalTo(g3)));
    MatcherAssert.assertThat(g2, IsNot.not(IsEqual.equalTo(g4)));
    MatcherAssert.assertThat(g3, IsNot.not(IsEqual.equalTo(g4)));
  }

  @Test
  public void hashCodesAreEqualForEquivalentObjects() {
    // Arrange:
    final Ed25519EncodedGroupElement g1 = MathUtils.getRandomEncodedGroupElement();
    final Ed25519EncodedGroupElement g2 = g1.decode().encode();
    final Ed25519EncodedGroupElement g3 = MathUtils.getRandomEncodedGroupElement();
    final Ed25519EncodedGroupElement g4 = MathUtils.getRandomEncodedGroupElement();

    // Assert
    MatcherAssert.assertThat(g2.hashCode(), IsEqual.equalTo(g1.hashCode()));
    MatcherAssert.assertThat(g1.hashCode(), IsNot.not(IsEqual.equalTo(g3.hashCode())));
    MatcherAssert.assertThat(g2.hashCode(), IsNot.not(IsEqual.equalTo(g4.hashCode())));
    MatcherAssert.assertThat(g3.hashCode(), IsNot.not(IsEqual.equalTo(g4.hashCode())));
  }

  // endregion

  // region toString

  @Test
  public void toStringReturnsCorrectRepresentation() {
    // Arrange:
    final Ed25519EncodedGroupElement encoded =
        Ed25519GroupElement.p2(Ed25519Field.ZERO, Ed25519Field.ONE, Ed25519Field.ONE).encode();

    // Act:
    final String encodedAsString = encoded.toString();
    final String expectedString =
        String.format(
            "x=%s\ny=%s%n",
            "0000000000000000000000000000000000000000000000000000000000000000",
            "0100000000000000000000000000000000000000000000000000000000000000");

    // Assert:
    MatcherAssert.assertThat(encodedAsString, IsEqual.equalTo(expectedString));
  }

  // endregion
}
