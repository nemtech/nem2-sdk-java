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
package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.core.crypto.VotingKey;

/** Voting key link transaction. */
public class VotingKeyLinkV1Transaction extends Transaction {

  /** The voting key. */
  private final VotingKey linkedPublicKey;

  /** Start finalization epoch. */
  private final long startEpoch;

  /** End finalization epoch. */
  private final long endEpoch;

  /** The link action. */
  private final LinkAction linkAction;

  /**
   * Constructor
   *
   * @param factory the factory.
   */
  VotingKeyLinkV1Transaction(VotingKeyLinkV1TransactionFactory factory) {
    super(factory);
    this.linkedPublicKey = factory.getLinkedPublicKey();
    this.startEpoch = factory.getStartEpoch();
    this.endEpoch = factory.getEndEpoch();
    this.linkAction = factory.getLinkAction();
  }

  public VotingKey getLinkedPublicKey() {
    return linkedPublicKey;
  }

  public LinkAction getLinkAction() {
    return linkAction;
  }

  public long getStartEpoch() {
    return startEpoch;
  }

  public long getEndEpoch() {
    return endEpoch;
  }
}
