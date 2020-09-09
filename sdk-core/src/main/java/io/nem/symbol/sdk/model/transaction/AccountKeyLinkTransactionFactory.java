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

/** Factory of {@link AccountKeyLinkTransaction} */
public class AccountKeyLinkTransactionFactory
    extends TransactionFactory<AccountKeyLinkTransaction> {

  private final PublicKey linkedPublicKey;
  private final LinkAction linkAction;

  private AccountKeyLinkTransactionFactory(
      final NetworkType networkType, final PublicKey linkedPublicKey, final LinkAction linkAction) {
    super(TransactionType.ACCOUNT_KEY_LINK, networkType);
    Validate.notNull(linkedPublicKey, "LinkedPublicKey must not be null");
    Validate.notNull(linkAction, "LinkAction must not be null");
    this.linkedPublicKey = linkedPublicKey;
    this.linkAction = linkAction;
  }

  /**
   * Static create method for factory.
   *
   * @param networkType Network type.
   * @param linkedPublicKey linked public key.
   * @param linkAction Link action.
   * @return Account link transaction.
   */
  public static AccountKeyLinkTransactionFactory create(
      NetworkType networkType, PublicKey linkedPublicKey, LinkAction linkAction) {
    return new AccountKeyLinkTransactionFactory(networkType, linkedPublicKey, linkAction);
  }

  /**
   * Gets the public key.
   *
   * @return Public key.
   */
  public PublicKey getLinkedPublicKey() {
    return linkedPublicKey;
  }

  /**
   * Gets the link action.
   *
   * @return Link action.
   */
  public LinkAction getLinkAction() {
    return linkAction;
  }

  @Override
  public AccountKeyLinkTransaction build() {
    return new AccountKeyLinkTransaction(this);
  }
}
