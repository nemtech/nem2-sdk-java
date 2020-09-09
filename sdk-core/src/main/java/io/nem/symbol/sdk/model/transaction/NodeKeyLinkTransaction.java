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

/** Voting key link transaction. */
public class NodeKeyLinkTransaction extends Transaction implements PublicKeyLinkTransaction {

  /** The linked public key. */
  private final PublicKey linkedPublicKey;

  /** The link action. */
  private final LinkAction linkAction;

  /**
   * Constructor
   *
   * @param factory the factory.
   */
  NodeKeyLinkTransaction(NodeKeyLinkTransactionFactory factory) {
    super(factory);
    linkedPublicKey = factory.getLinkedPublicKey();
    linkAction = factory.getLinkAction();
  }

  public PublicKey getLinkedPublicKey() {
    return linkedPublicKey;
  }

  public LinkAction getLinkAction() {
    return linkAction;
  }
}
