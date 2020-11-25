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

import java.util.List;
import java.util.Objects;

/** Merkle tree branch node. */
public class MerkleTreeBranch {
  /** Merkle tree node type */
  private final MerkleTreeNodeType type;
  /** Branch node path */
  private final String path;
  /** Branch node path encoded */
  private final String encodedPath;
  /** Branch node link bitmask */
  private final String linkMask;
  /** Branch node links */
  public final List<MerkleTreeBranchLink> links;
  /** Branch node hash */
  public final String branchHash;

  public MerkleTreeBranch(
      MerkleTreeNodeType type,
      String path,
      String encodedPath,
      String linkMask,
      List<MerkleTreeBranchLink> links,
      String branchHash) {
    this.type = type;
    this.path = path;
    this.encodedPath = encodedPath;
    this.linkMask = linkMask;
    this.links = links;
    this.branchHash = branchHash;
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

  public String getLinkMask() {
    return linkMask;
  }

  public List<MerkleTreeBranchLink> getLinks() {
    return links;
  }

  public String getBranchHash() {
    return branchHash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MerkleTreeBranch that = (MerkleTreeBranch) o;
    return type == that.type
        && Objects.equals(path, that.path)
        && Objects.equals(encodedPath, that.encodedPath)
        && Objects.equals(linkMask, that.linkMask)
        && Objects.equals(links, that.links)
        && Objects.equals(branchHash, that.branchHash);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, path, encodedPath, linkMask, links, branchHash);
  }
}
