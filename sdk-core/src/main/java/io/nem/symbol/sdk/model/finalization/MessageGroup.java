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

import java.math.BigInteger;
import java.util.List;
import org.apache.commons.lang3.Validate;

/** Finalization message group */
public class MessageGroup {

  /** Stage. */
  private final FinalizationStage stage;
  /** Height. */
  private final BigInteger height;
  /** Hashes. */
  private final List<String> hashes;
  /** Signatures. */
  public final List<BmTreeSignature> signatures;

  public MessageGroup(
      FinalizationStage stage,
      BigInteger height,
      List<String> hashes,
      List<BmTreeSignature> signatures) {
    Validate.notNull(stage, "stage is required");
    Validate.notNull(height, "height is required");
    Validate.notNull(hashes, "hash is required");
    Validate.notNull(signatures, "signatures is required");
    this.stage = stage;
    this.height = height;
    this.hashes = hashes;
    this.signatures = signatures;
  }

  public FinalizationStage getStage() {
    return stage;
  }

  public BigInteger getHeight() {
    return height;
  }

  public List<String> getHashes() {
    return hashes;
  }

  public List<BmTreeSignature> getSignatures() {
    return signatures;
  }
}
