/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility main class that uses the nemesis address configured to generate new accounts necessary
 * for the integration tests. Use with caution!!
 */
public class SetUpAccountsTool extends BaseIntegrationTest {

    public static void main(String[] args) {
        new SetUpAccountsTool().createAccounts();
    }

    private void createAccounts() {

        RepositoryType type = DEFAULT_REPOSITORY_TYPE;

        String generationHash = getGenerationHash();
        Account nemesisAccount = config().getNemesisAccount();
        Account recipient = config().getTestAccount();

        printAccount(recipient);

        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                Optional.of(recipient.getAddress()),
                Optional.empty(),
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(100))),
                new PlainMessage("E2ETest:SetUpAccountsTool")
            ).build();

        SignedTransaction signedTransaction = nemesisAccount
            .sign(transferTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getRepositoryFactory(type).createTransactionRepository()
                .announce(signedTransaction));
        assertEquals(
            "packet 9 was pushed to the network via /transaction",
            transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            nemesisAccount.getAddress(), signedTransaction.getHash(), type);
    }


    public void printAccount(Account account) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("privateKey", account.getPrivateKey());
        map.put("publicKey", account.getPublicKey());
        map.put("address", account.getAddress().plain());
        System.out.println(jsonHelper().print(map));
    }
}
