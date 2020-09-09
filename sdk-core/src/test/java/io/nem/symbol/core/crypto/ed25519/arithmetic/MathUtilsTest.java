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
import org.junit.jupiter.api.Test;

public class MathUtilsTest {

  /** Simple test for verifying that the MathUtils code works as expected. */
  @Test
  public void mathUtilsWorkAsExpected() {
    final Ed25519GroupElement neutral =
        Ed25519GroupElement.p3(
            Ed25519Field.ZERO, Ed25519Field.ONE, Ed25519Field.ONE, Ed25519Field.ZERO);
    for (int i = 0; i < 1000; i++) {
      final Ed25519GroupElement g = MathUtils.getRandomGroupElement();

      // Act:
      final Ed25519GroupElement h1 = MathUtils.addGroupElements(g, neutral);
      final Ed25519GroupElement h2 = MathUtils.addGroupElements(neutral, g);

      // Assert:
      MatcherAssert.assertThat(g, IsEqual.equalTo(h1));
      MatcherAssert.assertThat(g, IsEqual.equalTo(h2));
    }

    for (int i = 0; i < 1000; i++) {
      Ed25519GroupElement g = MathUtils.getRandomGroupElement();

      // P3 -> P2.
      Ed25519GroupElement h = MathUtils.toRepresentation(g, CoordinateSystem.P2);
      MatcherAssert.assertThat(h, IsEqual.equalTo(g));
      // P3 -> P1xP1.
      h = MathUtils.toRepresentation(g, CoordinateSystem.P1xP1);
      MatcherAssert.assertThat(g, IsEqual.equalTo(h));

      // P3 -> CACHED.
      h = MathUtils.toRepresentation(g, CoordinateSystem.CACHED);
      MatcherAssert.assertThat(h, IsEqual.equalTo(g));

      // P3 -> P2 -> P3.
      g = MathUtils.toRepresentation(g, CoordinateSystem.P2);
      h = MathUtils.toRepresentation(g, CoordinateSystem.P3);
      MatcherAssert.assertThat(g, IsEqual.equalTo(h));

      // P3 -> P2 -> P1xP1.
      g = MathUtils.toRepresentation(g, CoordinateSystem.P2);
      h = MathUtils.toRepresentation(g, CoordinateSystem.P1xP1);
      MatcherAssert.assertThat(g, IsEqual.equalTo(h));
    }

    for (int i = 0; i < 10; i++) {
      // Arrange:
      final Ed25519GroupElement g = MathUtils.getRandomGroupElement();

      // Act:
      final Ed25519GroupElement h = MathUtils.scalarMultiplyGroupElement(g, Ed25519Field.ZERO);

      // Assert:
      MatcherAssert.assertThat(Ed25519Group.ZERO_P3, IsEqual.equalTo(h));
    }
  }
}
