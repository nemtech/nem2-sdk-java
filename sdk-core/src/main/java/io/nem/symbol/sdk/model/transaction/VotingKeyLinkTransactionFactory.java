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
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.apache.commons.lang3.Validate;

/** Vrf key link transaction factory. */
public class VotingKeyLinkTransactionFactory extends TransactionFactory<VotingKeyLinkTransaction> {

  /** The voting key. */
  private final VotingKey linkedPublicKey;

  /** Start finalization epoch. */
  private final long startEpoch;

  /** End finalization epoch. */
  private final long endEpoch;

  /** The link action. */
  private final LinkAction linkAction;

  /**
   * The factory constructor for {@link VotingKeyLinkTransactionFactory}
   *
   * @param networkType the network type of this transaction.
   * @param linkedPublicKey the voting key.
   * @param startEpoch Start finalization epoch.
   * @param endEpoch End finalization epoch.
   * @param linkAction the link action.
   */
  private VotingKeyLinkTransactionFactory(
      final NetworkType networkType,
      final Deadline deadline,
      final VotingKey linkedPublicKey,
      long startEpoch,
      long endEpoch,
      final LinkAction linkAction) {
    super(TransactionType.VOTING_KEY_LINK, networkType, deadline);
    Validate.notNull(linkedPublicKey, "linkedPublicKey must not be null");
    Validate.notNull(linkAction, "linkAction must not be null");
    ConvertUtils.validateNotNegative(startEpoch);
    ConvertUtils.validateNotNegative(endEpoch);
    this.linkedPublicKey = linkedPublicKey;
    this.startEpoch = startEpoch;
    this.endEpoch = endEpoch;
    this.linkAction = linkAction;
  }

  /**
   * Create method factory for {@link VotingKeyLinkTransactionFactory}
   *
   * @param networkType the network type of this transaction.
   * @param linkedPublicKey the voting key.
   * @param startEpoch Start finalization epoch.
   * @param endEpoch End finalization epoch.
   * @param linkAction the link action.
   * @return a new factory for {@link VotingKeyLinkTransactionFactory}
   */
  public static VotingKeyLinkTransactionFactory create(
      final NetworkType networkType,
      final Deadline deadline,
      final VotingKey linkedPublicKey,
      long startEpoch,
      long endEpoch,
      final LinkAction linkAction) {
    return new VotingKeyLinkTransactionFactory(
        networkType, deadline, linkedPublicKey, startEpoch, endEpoch, linkAction);
  }

  @Override
  public VotingKeyLinkTransaction build() {
    return new VotingKeyLinkTransaction(this);
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
