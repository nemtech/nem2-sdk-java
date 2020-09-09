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
package io.nem.symbol.core.crypto.ed25519;

import io.nem.symbol.core.crypto.CryptoEngines;
import java.math.BigInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

public class Ed25519CurveTest {

  private static final BigInteger GROUP_ORDER =
      BigInteger.ONE.shiftLeft(252).add(new BigInteger("27742317777372353535851937790883648493"));

  @Test
  public void getNameReturnsCorrectName() {
    // Assert:
    MatcherAssert.assertThat(
        CryptoEngines.ed25519Engine().getCurve().getName(), IsEqual.equalTo("ed25519"));
  }

  @Test
  public void getNameReturnsCorrectGroupOrder() {
    // Assert:
    MatcherAssert.assertThat(
        CryptoEngines.ed25519Engine().getCurve().getGroupOrder(), IsEqual.equalTo(GROUP_ORDER));
  }

  @Test
  public void getNameReturnsCorrectHalfGroupOrder() {
    // Arrange:
    final BigInteger halfGroupOrder = GROUP_ORDER.shiftRight(1);

    // Assert:
    MatcherAssert.assertThat(
        CryptoEngines.ed25519Engine().getCurve().getHalfGroupOrder(),
        IsEqual.equalTo(halfGroupOrder));
  }
}
