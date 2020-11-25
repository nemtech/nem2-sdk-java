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
package io.nem.symbol.sdk.model.state;

import java.util.Objects;

/** Merkle tree branch link. */
public class MerkleTreeBranchLink {

  /** Link bit index */
  private final String bit;

  /** Link hash */
  private final String link;

  public MerkleTreeBranchLink(String bit, String link) {
    this.bit = bit;
    this.link = link;
  }

  public String getBit() {
    return bit;
  }

  public String getLink() {
    return link;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MerkleTreeBranchLink that = (MerkleTreeBranchLink) o;
    return Objects.equals(bit, that.bit) && Objects.equals(link, that.link);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bit, link);
  }
}
