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
import io.nem.symbol.sdk.model.state.MerkleTreeBranch;
import io.nem.symbol.sdk.model.state.MerkleTreeBranchLink;
import io.nem.symbol.sdk.model.state.MerkleTreeLeaf;
import io.nem.symbol.sdk.model.state.MerkleTreeNodeType;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MerkleStateInfoTest {

  @Test
  void constructor() {
    MerkleTree tree =
        new MerkleTree(Collections.singletonList(createBranch("abc")), createLeaf("ABCD"));
    MerkleStateInfo info = new MerkleStateInfo("1234", tree);

    Assertions.assertEquals("1234", info.getRaw());
    Assertions.assertEquals(tree, info.getTree());
  }

  @Test
  void equals() {
    MerkleTree tree1 =
        new MerkleTree(Collections.singletonList(createBranch("abc")), createLeaf("ABCD"));
    MerkleStateInfo info1 = new MerkleStateInfo("1234", tree1);

    MerkleTree tree2 =
        new MerkleTree(Collections.singletonList(createBranch("abc")), createLeaf("ABCD"));
    MerkleStateInfo info2 = new MerkleStateInfo("1234", tree2);

    MerkleTree tree3 =
        new MerkleTree(Collections.singletonList(createBranch("abc")), createLeaf("1234"));
    MerkleStateInfo info3 = new MerkleStateInfo("1234", tree3);

    Assertions.assertEquals(info1, info1);
    Assertions.assertEquals(info2, info1);
    Assertions.assertNotEquals(info2, info3);
  }

  private MerkleTreeLeaf createLeaf(String value) {
    return new MerkleTreeLeaf(MerkleTreeNodeType.LEAF, "path", "encoded path", value, "leaf");
  }

  private MerkleTreeBranch createBranch(String hash) {
    return new MerkleTreeBranch(
        MerkleTreeNodeType.BRANCH,
        "path",
        "encoded path",
        "mask",
        Collections.singletonList(new MerkleTreeBranchLink("bit", "link")),
        hash);
  }
}
