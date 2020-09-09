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
package io.nem.symbol.sdk.model.blockchain;

public class MerklePathItem {

  private final Position position;
  private final String hash;

  /**
   * Constructor
   *
   * @param position the position in the path.
   * @param hash the hash.
   */
  public MerklePathItem(Position position, String hash) {
    this.position = position;
    this.hash = hash;
  }

  /**
   * Return position
   *
   * @return Integer
   */
  public Position getPosition() {
    return this.position;
  }

  /**
   * Return hash
   *
   * @return String
   */
  public String getHash() {
    return this.hash;
  }
}
