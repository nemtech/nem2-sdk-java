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

import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.CosignatoryModificationActionType;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.sdk.model.transaction.MultisigCosignatoryModification;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;

/**
 * Utility main class that uses the nemesis address configured to generate new accounts necessary
 * for the integration tests. Use with caution!!
 */
public class SetUpAccountsTool extends BaseIntegrationTest {

    public static final int AMOUNT_PER_TRANSFER = 10000;

    private final RepositoryType type = DEFAULT_REPOSITORY_TYPE;

    public static void main(String[] args) {
        new SetUpAccountsTool().createAccounts();
    }

    private void createAccounts() {
        setUp();

        sendMosaicFromNemesis(config().getTestAccount());
        sendMosaicFromNemesis(config().getTestAccount2());
        sendMosaicFromNemesis(config().getCosignatoryAccount());
        sendMosaicFromNemesis(config().getCosignatory2Account());
        sendMosaicFromNemesis(config().getCosignatory3Account());
        sendMosaicFromNemesis(config().getMultisigAccount());
        //TODO Failure_Core_Insufficient_Balance error!
        createMultisigAccount(config().getMultisigAccount(), config().getCosignatoryAccount(),
            config().getCosignatory2Account());
        tearDown();
    }

    private void createMultisigAccount(Account multisigAccount, Account... accounts) {

        System.out.println("Creating multisg account");
        MultisigAccountModificationTransaction convertIntoMultisigTransaction = MultisigAccountModificationTransactionFactory
            .create(getNetworkType(), (byte) 2, (byte) 1, Arrays.stream(accounts)
            .map(a -> new MultisigCosignatoryModification(CosignatoryModificationActionType.ADD,
                a.getPublicAccount())).collect(Collectors.toList())).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createBonded(
            getNetworkType(),
            Collections.singletonList(
                convertIntoMultisigTransaction.toAggregate(multisigAccount.getPublicAccount()))
        ).build();

        SignedTransaction signedTransaction = multisigAccount
            .signTransactionWithCosignatories(aggregateTransaction, Arrays.asList(accounts),
                getGenerationHash());

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getRepositoryFactory(type).createTransactionRepository()
                .announce(signedTransaction));
        assertEquals(
            "packet 9 was pushed to the network via /transaction",
            transactionAnnounceResponse.getMessage());

        validateTransactionAnnounceCorrectly(multisigAccount.getAddress(), signedTransaction.getHash(), type);

    }

    private void sendMosaicFromNemesis(Account recipient) {
        if (hasMosaic(recipient)) {
            System.out.println("Ignoring recipient. It has the mosaic already: ");
            printAccount(recipient);
            return;
        }

        Account nemesisAccount = config().getNemesisAccount();
        System.out.println("Sending " + AMOUNT_PER_TRANSFER + " Mosaic to: ");
        printAccount(recipient);

        BigInteger amount = BigInteger.valueOf(AMOUNT_PER_TRANSFER);
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                Optional.of(recipient.getAddress()),
                Optional.empty(),
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(amount)),
                new PlainMessage("E2ETest:SetUpAccountsTool")
            ).build();

        TransferTransaction processedTransaction = announceAndValidate(type, nemesisAccount,
            transferTransaction);
        Assertions.assertEquals(amount, processedTransaction.getMosaics().get(0).getAmount());
    }

    private boolean hasMosaic(Account recipient) {
        try {
            AccountInfo accountInfo = get(getRepositoryFactory(type).createAccountRepository()
                .getAccountInfo(recipient.getAddress()));
            return accountInfo.getMosaics().stream().anyMatch(
                m -> m.getAmount().longValue() >= 100);
        } catch (RepositoryCallException e) {
            return false;
        }
    }


    void printAccount(Account account) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("privateKey", account.getPrivateKey());
        map.put("publicKey", account.getPublicKey());
        map.put("address", account.getAddress().plain());
        System.out.println(jsonHelper().print(map));
    }
}
