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

/** Finalization proof */
public class FinalizationProof {

  /** Version. */
  private final int version;
  /** Finalization epoch. */
  private final long finalizationEpoch;
  /** Finalization point. */
  private final long finalizationPoint;
  /** Finalization height. */
  private final BigInteger height;
  /** Hash. */
  private final String hash;
  /** Message groups. */
  public final List<MessageGroup> messageGroups;

  public FinalizationProof(
      int version,
      long finalizationEpoch,
      long finalizationPoint,
      BigInteger height,
      String hash,
      List<MessageGroup> messageGroups) {
    Validate.notNull(height, "height is required");
    Validate.notNull(hash, "hash is required");
    Validate.notNull(messageGroups, "messageGroups is required");
    this.version = version;
    this.finalizationEpoch = finalizationEpoch;
    this.finalizationPoint = finalizationPoint;
    this.height = height;
    this.hash = hash;
    this.messageGroups = messageGroups;
  }

  public int getVersion() {
    return version;
  }

  public long getFinalizationEpoch() {
    return finalizationEpoch;
  }

  public long getFinalizationPoint() {
    return finalizationPoint;
  }

  public BigInteger getHeight() {
    return height;
  }

  public String getHash() {
    return hash;
  }

  public List<MessageGroup> getMessageGroups() {
    return messageGroups;
  }
}
