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
package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.state.MerkleTree;
import io.nem.symbol.sdk.model.state.MerkleTreeBranch;
import io.nem.symbol.sdk.model.state.MerkleTreeBranchLink;
import io.nem.symbol.sdk.model.state.MerkleTreeLeaf;
import io.nem.symbol.sdk.model.state.MerkleTreeNodeType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MerkleStateInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MerkleTreeBranchDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MerkleTreeLeafDTO;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MerkleMapper {

  private final JsonHelper jsonHelper;

  public MerkleMapper(JsonHelper jsonHelper) {
    this.jsonHelper = jsonHelper;
  }

  public MerkleStateInfo toMerkleStateInfo(MerkleStateInfoDTO dto) {
    MerkleTreeLeaf leaf =
        dto.getTree().stream().map(this::toLeaf).filter(Objects::nonNull).findFirst().orElse(null);
    List<MerkleTreeBranch> branches =
        dto.getTree().stream()
            .map(this::toBranch)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    MerkleTree tree = new MerkleTree(branches, leaf);
    return new MerkleStateInfo(dto.getRaw(), tree);
  }

  private io.nem.symbol.sdk.model.state.MerkleTreeBranch toBranch(Object dto) {
    MerkleTreeNodeType type =
        MerkleTreeNodeType.rawValueOf(getJsonHelper().getInteger(dto, "type"));
    if (type == MerkleTreeNodeType.BRANCH) {
      return toMerkleTreeBranch(dto, type);
    }
    return null;
  }

  private io.nem.symbol.sdk.model.state.MerkleTreeLeaf toLeaf(Object dto) {
    MerkleTreeNodeType type =
        MerkleTreeNodeType.rawValueOf(getJsonHelper().getInteger(dto, "type"));
    if (type == MerkleTreeNodeType.LEAF) {
      MerkleTreeLeafDTO branch = getJsonHelper().convert(dto, MerkleTreeLeafDTO.class);
      return toMerkleTreeLeaf(type, branch);
    }
    return null;
  }

  private MerkleTreeLeaf toMerkleTreeLeaf(MerkleTreeNodeType type, MerkleTreeLeafDTO leaf) {
    return new MerkleTreeLeaf(
        type, leaf.getPath(), leaf.getEncodedPath(), leaf.getValue(), leaf.getLeafHash());
  }

  private io.nem.symbol.sdk.model.state.MerkleTreeBranch toMerkleTreeBranch(
      Object dto, MerkleTreeNodeType type) {
    MerkleTreeBranchDTO branch = getJsonHelper().convert(dto, MerkleTreeBranchDTO.class);

    List<MerkleTreeBranchLink> links =
        branch.getLinks().stream()
            .map(link -> new MerkleTreeBranchLink(link.getBit(), link.getLink()))
            .collect(Collectors.toList());
    return new io.nem.symbol.sdk.model.state.MerkleTreeBranch(
        type,
        branch.getPath(),
        branch.getEncodedPath(),
        branch.getLinkMask(),
        links,
        branch.getBranchHash());
  }

  public JsonHelper getJsonHelper() {
    return jsonHelper;
  }
}
