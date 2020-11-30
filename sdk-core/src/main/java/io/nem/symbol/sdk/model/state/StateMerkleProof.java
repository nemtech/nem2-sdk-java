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
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * This object holds the state merkle proof information
 * @param <S> the state for reference
 */
public class StateMerkleProof<S> {

  /**
   * The state for reference
   */
  private final S state;

  /**
   * The state hash
   */
  private final String stateHash;

  /**
   * The merkle tree
   */
  private final MerkleTree merkleTree;

  /**
   * The raw merkle tree for reference.
   */
  private final String raw;

  public StateMerkleProof(S state, String stateHash, MerkleTree merkleTree, String raw) {
    this.state = state;
    this.stateHash = stateHash;
    this.merkleTree = merkleTree;
    this.raw = raw;
  }

  public String getStateHash() {
    return stateHash;
  }

  public MerkleTree getMerkleTree() {
    return merkleTree;
  }

  /**
   * @return if the proof is valid or not.
   */
  public boolean isValid() {
    return getLeafValue()
        .map(leafValue -> StringUtils.equalsAnyIgnoreCase(stateHash, leafValue))
        .orElse(false);
  }

  /**
   * Get merkle tree root hash
   *
   * @return root hash if any
   */
  public Optional<String> getRootHash() {
    MerkleTree tree = getMerkleTree();
    if (!tree.getBranches().isEmpty()) {
      return Optional.of(tree.getBranches().get(0).getBranchHash());
    }
    if (tree.getLeaf() != null) {
      return Optional.ofNullable(tree.getLeaf().getLeafHash());
    }
    return Optional.empty();
  }

  /**
   * Get merkle tree root hash
   *
   * @return root hash if any
   */
  public Optional<String> getLeafValue() {

    MerkleTree tree = getMerkleTree();
    if (tree.getLeaf() != null) {
      return Optional.ofNullable(tree.getLeaf().getValue());
    }
    return Optional.empty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StateMerkleProof<?> that = (StateMerkleProof<?>) o;
    return Objects.equals(state, that.state)
        && Objects.equals(stateHash, that.stateHash)
        && Objects.equals(merkleTree, that.merkleTree)
        && Objects.equals(raw, that.raw);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, stateHash, merkleTree, raw);
  }

  public String getRaw() {
    return this.raw;
  }

  public S getState() {
    return state;
  }
}
