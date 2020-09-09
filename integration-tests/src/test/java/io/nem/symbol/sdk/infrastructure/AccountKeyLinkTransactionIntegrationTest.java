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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.transaction.AccountKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.AccountKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountKeyLinkTransactionIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void basicAnnounce(RepositoryType type) {

    Account account = config().getNemesisAccount2();
    PublicKey linkedPublicKey =
        PublicKey.fromHexString("F5D0AAD909CFBC810A3F888C33C57A9051AE1A59D1CDA872A8B90BCA7EF2D34A");

    AccountKeyLinkTransaction linkTransaction =
        AccountKeyLinkTransactionFactory.create(getNetworkType(), linkedPublicKey, LinkAction.LINK)
            .maxFee(this.maxFee)
            .build();

    announceAndValidate(type, account, linkTransaction);

    AccountKeyLinkTransaction unlinkTransaction =
        AccountKeyLinkTransactionFactory.create(
                getNetworkType(), linkedPublicKey, LinkAction.UNLINK)
            .maxFee(this.maxFee)
            .build();

    announceAndValidate(type, account, unlinkTransaction);
  }
}
