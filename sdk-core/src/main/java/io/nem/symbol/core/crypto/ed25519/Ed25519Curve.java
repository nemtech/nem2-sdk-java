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

import io.nem.symbol.core.crypto.Curve;
import io.nem.symbol.core.crypto.ed25519.arithmetic.Ed25519Group;
import java.math.BigInteger;

/** Class that wraps the elliptic curve Ed25519. */
public class Ed25519Curve implements Curve {

  private static final Ed25519Curve ED25519;

  static {
    ED25519 = new Ed25519Curve();
  }

  /**
   * Gets the Ed25519 instance.
   *
   * @return The Ed25519 instance.
   */
  public static Ed25519Curve ed25519() {
    return ED25519;
  }

  @Override
  public String getName() {
    return "ed25519";
  }

  @Override
  public BigInteger getGroupOrder() {
    return Ed25519Group.GROUP_ORDER;
  }

  @Override
  public BigInteger getHalfGroupOrder() {
    return Ed25519Group.GROUP_ORDER.shiftRight(1);
  }
}
