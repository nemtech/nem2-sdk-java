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

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferTransactionIntegrationTest extends BaseIntegrationTest {


    private Account account = config().getDefaultAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateTransferTransaction(RepositoryType type) {
        Address recipient = getRecipient();
        String message =
            "E2ETest:aggregateTransferTransaction:messagelooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                +
                "oooooooong";
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                recipient,
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                /*new PlainMessage(
                    "E2ETest:aggregateTransferTransaction:message"), */
// short message for debugging
                new PlainMessage(
                    message)
                // Use long message to test if size of inner transaction is calculated correctly
            ).build();

        TransferTransaction processed = announceAggregateAndValidate(type, account,
            transferTransaction);
        Assertions.assertEquals(message, processed.getMessage().getPayload());
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void standaloneTransferTransaction(RepositoryType type) {
        Address recipient = getRecipient();
        String message = "E2ETest:standaloneTransferTransaction:message";
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                recipient,
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                new PlainMessage(message)
            ).build();

        TransferTransaction processed = announceAndValidate(type, this.account,
            transferTransaction);
        Assertions.assertEquals(message, processed.getMessage().getPayload());
    }

}
