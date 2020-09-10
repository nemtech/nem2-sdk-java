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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.Validate;

public enum RoleType {
  PEER_NODE(1),
  API_NODE(2),
  VOTING_NODE(4);

  private static final int MAX_FLAG_VALUE =
      Arrays.stream(RoleType.values()).mapToInt(RoleType::getValue).sum();

  private final int value;

  RoleType(int value) {
    this.value = value;
  }

  /**
   * Creates a lit of roles based on the 1-7 bit bitwise value
   *
   * @param flags the flags
   * @return the list of roles
   */
  public static List<RoleType> toList(int flags) {
    Validate.isTrue(flags >= 0, "flags must be 0 or greater");
    Validate.isTrue(flags <= MAX_FLAG_VALUE, "flags must be " + MAX_FLAG_VALUE + " or smaller");
    List<RoleType> roles = new ArrayList<>();

    int temp = flags;
    int totalValues = RoleType.values().length;
    for (int i = 0; i < totalValues; i++) {
      RoleType roleType = RoleType.values()[totalValues - i - 1];
      if (temp >= roleType.getValue()) {
        temp = temp - roleType.getValue();
        roles.add(0, roleType);
      }
    }

    return roles;
  }

  /**
   * Returns enum value.
   *
   * @return enum value
   */
  public int getValue() {
    return this.value;
  }
}
