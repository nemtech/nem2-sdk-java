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

/** Merkle tree leaf node. */
public class MerkleTreeLeaf {

  private final MerkleTreeNodeType type;
  /** Leaf node path */
  private final String path;
  /** Leaf node path encoded */
  private final String encodedPath;
  /** Leaf node value hash */
  private final String value;
  /** Leaf node hash */
  private final String leafHash;

  public MerkleTreeLeaf(
      MerkleTreeNodeType type, String path, String encodedPath, String value, String leafHash) {
    this.type = type;
    this.path = path;
    this.encodedPath = encodedPath;
    this.value = value;
    this.leafHash = leafHash;
  }

  public MerkleTreeNodeType getType() {
    return type;
  }

  public String getPath() {
    return path;
  }

  public String getEncodedPath() {
    return encodedPath;
  }

  public String getValue() {
    return value;
  }

  public String getLeafHash() {
    return leafHash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MerkleTreeLeaf that = (MerkleTreeLeaf) o;
    return type == that.type
        && Objects.equals(path, that.path)
        && Objects.equals(encodedPath, that.encodedPath)
        && Objects.equals(value, that.value)
        && Objects.equals(leafHash, that.leafHash);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, path, encodedPath, value, leafHash);
  }
}
