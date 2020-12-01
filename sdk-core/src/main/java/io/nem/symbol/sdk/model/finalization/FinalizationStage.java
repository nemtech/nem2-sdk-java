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
package io.nem.symbol.sdk.model.finalization;

import java.util.Arrays;

/** Type of stage: * 0 - Prevote. * 1 - Precommit. * 2 - Count. */
public enum FinalizationStage {
  PRE_VOTE(0),

  PRE_COMMIT(1),

  COUNT(2);

  private final int value;

  FinalizationStage(Integer value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static FinalizationStage rawValueOf(int value) {
    return Arrays.stream(values())
        .filter(e -> e.value == (value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }
}
