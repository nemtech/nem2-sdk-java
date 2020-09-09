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

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.apache.commons.lang3.Validate;

/** Vrf key link transaction factory. */
public class VrfKeyLinkTransactionFactory extends TransactionFactory<VrfKeyLinkTransaction> {

  /** The linked public key. */
  private final PublicKey linkedPublicKey;

  /** The link action. */
  private final LinkAction linkAction;

  /**
   * The factory constructor for {@link VrfKeyLinkTransaction}
   *
   * @param networkType the network type of this transaction.
   * @param linkedPublicKey the linked public key.
   * @param linkAction the link action.
   */
  public VrfKeyLinkTransactionFactory(
      final NetworkType networkType, final PublicKey linkedPublicKey, LinkAction linkAction) {
    super(TransactionType.VRF_KEY_LINK, networkType);
    Validate.notNull(linkedPublicKey, "linkedPublicKey must not be null");
    Validate.notNull(linkAction, "linkAction must not be null");
    this.linkedPublicKey = linkedPublicKey;
    this.linkAction = linkAction;
  }

  /**
   * The factory constructor for {@link VrfKeyLinkTransaction}
   *
   * @param networkType the network type of this transaction.
   * @param linkedPublicKey the linked public key.
   * @param linkAction the link action.
   * @return an instance of the factory.
   */
  public static VrfKeyLinkTransactionFactory create(
      final NetworkType networkType, final PublicKey linkedPublicKey, final LinkAction linkAction) {
    return new VrfKeyLinkTransactionFactory(networkType, linkedPublicKey, linkAction);
  }

  @Override
  public VrfKeyLinkTransaction build() {
    return new VrfKeyLinkTransaction(this);
  }

  public PublicKey getLinkedPublicKey() {
    return linkedPublicKey;
  }

  public LinkAction getLinkAction() {
    return linkAction;
  }
}
