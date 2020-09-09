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

import io.nem.symbol.core.utils.ArrayUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import java.math.BigInteger;

/** Represents the underlying finite field for Ed25519. The field has p = 2^255 - 19 elements. */
public class Ed25519Field {

  /** Private constructor for this utility class. */
  private Ed25519Field() {}

  /** P: 2^255 - 19 */
  public static final BigInteger P =
      new BigInteger(
          ConvertUtils.getBytes(
              "7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffed"));

  public static final Ed25519FieldElement ZERO = getFieldElement(0);
  public static final Ed25519FieldElement ONE = getFieldElement(1);
  public static final Ed25519FieldElement TWO = getFieldElement(2);
  public static final Ed25519FieldElement D = getD();
  public static final Ed25519FieldElement D_Times_TWO = D.multiply(TWO);

  /** I ^ 2 = -1 */
  public static final Ed25519FieldElement I =
      new Ed25519EncodedFieldElement(
              ConvertUtils.getBytes(
                  "b0a00e4a271beec478e42fad0618432fa7d7fb3d99004d2b0bdfc14f8024832b"))
          .decode();

  private static Ed25519FieldElement getFieldElement(final int value) {
    final int[] f = new int[10];
    f[0] = value;
    return new Ed25519FieldElement(f);
  }

  private static Ed25519FieldElement getD() {
    final BigInteger d =
        BigInteger.valueOf(-121665)
            .multiply(BigInteger.valueOf(121666).modInverse(Ed25519Field.P))
            .mod(Ed25519Field.P);
    return new Ed25519EncodedFieldElement(ArrayUtils.toByteArray(d, 32)).decode();
  }
}
