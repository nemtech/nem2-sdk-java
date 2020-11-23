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
import io.nem.symbol.core.crypto.VotingKey;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkV1Transaction;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkV1TransactionFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VotingKeyLinkTransactionIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void basicAnnounceV1(RepositoryType type) {

    Account account = config().getNemesisAccount2();
    VotingKey linkedPublicKey = VotingKey.fromHexString("AAAAA");

    VotingKeyLinkV1Transaction linkTransaction =
        VotingKeyLinkV1TransactionFactory.create(
                getNetworkType(), getDeadline(), linkedPublicKey, (72), (26280), LinkAction.LINK)
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, account, linkTransaction);

    VotingKeyLinkV1Transaction unlinkTransaction =
        VotingKeyLinkV1TransactionFactory.create(
                getNetworkType(), getDeadline(), linkedPublicKey, (72), (26280), LinkAction.UNLINK)
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, account, unlinkTransaction);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void basicAnnounce(RepositoryType type) {

    Account account = config().getNemesisAccount3();
    PublicKey linkedPublicKey = PublicKey.fromHexString("BBBBB");

    VotingKeyLinkTransaction linkTransaction =
        VotingKeyLinkTransactionFactory.create(
                getNetworkType(), getDeadline(), linkedPublicKey, (72), (26280), LinkAction.LINK)
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, account, linkTransaction);

    VotingKeyLinkTransaction unlinkTransaction =
        VotingKeyLinkTransactionFactory.create(
                getNetworkType(), getDeadline(), linkedPublicKey, (72), (26280), LinkAction.UNLINK)
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, account, unlinkTransaction);
  }
}
