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
package io.nem.symbol.sdk.model.node;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests of {@link RoleType} */
public class RoleTypeTest {

  @Test
  void toList() {
    Assertions.assertEquals(Collections.emptyList(), RoleType.toList(0));
    Assertions.assertEquals(Collections.singletonList(RoleType.PEER_NODE), RoleType.toList(1));
    Assertions.assertEquals(Collections.singletonList(RoleType.API_NODE), RoleType.toList(2));
    Assertions.assertEquals(
        Arrays.asList(RoleType.PEER_NODE, RoleType.API_NODE), RoleType.toList(3));
    Assertions.assertEquals(Collections.singletonList(RoleType.VOTING_NODE), RoleType.toList(4));
    Assertions.assertEquals(
        Arrays.asList(RoleType.PEER_NODE, RoleType.VOTING_NODE), RoleType.toList(5));
    Assertions.assertEquals(
        Arrays.asList(RoleType.API_NODE, RoleType.VOTING_NODE), RoleType.toList(6));
    Assertions.assertEquals(
        Arrays.asList(RoleType.PEER_NODE, RoleType.API_NODE, RoleType.VOTING_NODE),
        RoleType.toList(7));

    Assertions.assertEquals(
        Arrays.asList(
            RoleType.PEER_NODE,
            RoleType.API_NODE,
            RoleType.VOTING_NODE,
            RoleType.IP_V4_NODE,
            RoleType.IP_V6_NODE),
        RoleType.toList(128 + 64 + 7));

    Assertions.assertEquals(
        Arrays.asList(
            RoleType.PEER_NODE,
            RoleType.API_NODE,
            RoleType.IP_V4_NODE),
        RoleType.toList( 64 + 3));

    Assertions.assertEquals(
        Collections.singletonList(
            RoleType.IP_V6_NODE),
        RoleType.toList(128));

    Assertions.assertThrows(IllegalArgumentException.class, () -> RoleType.toList(-1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> RoleType.toList(8));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> RoleType.toList(2 * RoleType.IP_V6_NODE.getValue()));
  }
}
