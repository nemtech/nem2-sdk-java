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

import io.nem.symbol.sdk.api.TransactionService;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreatingAnEscrowContractWithAggregateBondedTransactionIntegrationTest
    extends BaseIntegrationTest {

  @Test
  @Disabled
  void executeTransfer() {

    Account ticketDistributorAccount = this.config().getTestAccount();
    Account aliceAccount = this.config().getTestAccount2();
    RepositoryType type = DEFAULT_REPOSITORY_TYPE;

    MosaicId mosaicId = createMosaic(ticketDistributorAccount, type, BigInteger.ZERO, null);

    TransferTransaction aliceToTicketDistributorTx =
        TransferTransactionFactory.create(
                getNetworkType(),
                ticketDistributorAccount.getAddress(),
                Collections.singletonList(
                    getNetworkCurrency().createRelative(BigInteger.valueOf(100))),
                PlainMessage.create("send 100 cat.currency to distributor"))
            .maxFee(this.maxFee)
            .build();

    TransferTransaction ticketDistributorToAliceTx =
        TransferTransactionFactory.create(
                getNetworkType(),
                aliceAccount.getAddress(),
                Collections.singletonList(new Mosaic(mosaicId, BigInteger.ONE)),
                PlainMessage.create("send 1 museum ticket to alice"))
            .maxFee(this.maxFee)
            .build();

    /* end block 01 */

    /* start block 02 */
    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createBonded(
                getNetworkType(),
                Arrays.asList(
                    aliceToTicketDistributorTx.toAggregate(aliceAccount.getPublicAccount()),
                    ticketDistributorToAliceTx.toAggregate(
                        ticketDistributorAccount.getPublicAccount())))
            .maxFee(this.maxFee)
            .build();

    String networkGenerationHash = getGenerationHash();

    SignedTransaction signedTransaction =
        aliceAccount.sign(aggregateTransaction, networkGenerationHash);
    System.out.println("Aggregate Transaction Hash: " + signedTransaction.getHash());
    /* end block 02 */

    /* start block 03 */
    HashLockTransaction hashLockTransaction =
        HashLockTransactionFactory.create(
                getNetworkType(),
                getNetworkCurrency().createRelative(BigInteger.TEN),
                BigInteger.valueOf(480),
                signedTransaction)
            .maxFee(this.maxFee)
            .build();

    SignedTransaction signedHashLockTransaction =
        aliceAccount.sign(hashLockTransaction, networkGenerationHash);

    System.out.println("Hash Transaction Hash: " + hashLockTransaction.getHash());

    TransactionService transactionService = getTransactionService(type);

    Transaction transaction =
        get(
            transactionService.announceHashLockAggregateBonded(
                getListener(type), signedHashLockTransaction, signedTransaction));

    Assertions.assertNotNull(transaction);
  }
}
