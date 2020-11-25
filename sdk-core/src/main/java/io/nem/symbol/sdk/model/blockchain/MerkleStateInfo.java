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

import io.nem.symbol.sdk.model.state.MerkleTree;
import java.util.Objects;

/** The merkle path information clients can use to proof the state of the given entity. */
public class MerkleStateInfo {

  /**
   * The hex information of the complete merkle tree as returned by server api. More * information
   * can be found in chapter 4.3 of the catapult whitepaper.
   */
  private final String raw;

  /** The merkle tree object parsed from raw */
  private final MerkleTree tree;

  /**
   * @param raw The hex information of the complete merkle tree as returned by server api. More
   *     information can be found in chapter 4.3 of the catapult whitepaper.
   * @param tree The merkle tree object parsed from raw
   */
  public MerkleStateInfo(String raw, MerkleTree tree) {
    this.raw = raw;
    this.tree = tree;
  }

  public MerkleTree getTree() {
    return tree;
  }

  public String getRaw() {
    return raw;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MerkleStateInfo that = (MerkleStateInfo) o;
    return Objects.equals(raw, that.raw) && Objects.equals(tree, that.tree);
  }

  @Override
  public int hashCode() {
    return Objects.hash(raw, tree);
  }
}
