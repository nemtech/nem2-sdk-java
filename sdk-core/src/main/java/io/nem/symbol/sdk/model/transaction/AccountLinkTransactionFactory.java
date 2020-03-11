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

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link AccountLinkTransaction}
 */
public class AccountLinkTransactionFactory extends TransactionFactory<AccountLinkTransaction> {

    private final PublicAccount remoteAccount;
    private final AccountLinkAction linkAction;

    private AccountLinkTransactionFactory(
        final NetworkType networkType,
        final PublicAccount remoteAccount,
        final AccountLinkAction linkAction) {
        super(TransactionType.ACCOUNT_LINK, networkType);
        Validate.notNull(remoteAccount, "RemoteAccount must not be null");
        Validate.notNull(linkAction, "LinkAction must not be null");
        this.remoteAccount = remoteAccount;
        this.linkAction = linkAction;
    }

  /**
   * Static create method for factory.
   *
   * @param networkType Network type.
   * @param remoteAccount Remote account.
   * @param linkAction Link action.
   * @return Account link transaction.
   */
  public static AccountLinkTransactionFactory create(
      NetworkType networkType, PublicAccount remoteAccount, AccountLinkAction linkAction) {
        return new AccountLinkTransactionFactory(networkType, remoteAccount, linkAction);
    }

    /**
     * Gets the public key.
     *
     * @return Public key.
     */
    public PublicAccount getRemoteAccount() {
        return remoteAccount;
    }

    /**
     * Gets the link action.
     *
     * @return Link action.
     */
    public AccountLinkAction getLinkAction() {
        return linkAction;
    }


    @Override
    public AccountLinkTransaction build() {
        return new AccountLinkTransaction(this);
    }
}
