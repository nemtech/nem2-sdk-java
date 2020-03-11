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

import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.reactivex.Observable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test of {@link AggregateTransactionServiceImpl}
 */
public class AggregateTransactionServiceTest {

    private NetworkType networkType = NetworkType.MIJIN_TEST;
    private AggregateTransactionServiceImpl service;

    private Account account1 = Account
        .createFromPrivateKey("82DB2528834C9926F0FCCE042466B24A266F5B685CB66D2869AF6648C043E950",
            networkType);
    private Account multisig1 = Account
        .createFromPrivateKey("8B0622C2CCFC5CCC5A74B500163E3C68F3AD3643DB12932FC931143EAC67280D",
            networkType);
    private Account multisig2 = Account
        .createFromPrivateKey("22A1D67F8519D1A45BD7116600BB6E857786E816FE0B45E4C5B9FFF3D64BC177",
            networkType);

    private Account multisig3 = Account
        .createFromPrivateKey("5E7812AB0E709ABC45466034E1A209099F6A12C4698748A63CDCAA9B0DDE1DBD",
            networkType);
    private Account account2 = Account
        .createFromPrivateKey("A4D410270E01CECDCDEADCDE32EC79C8D9CDEA4DCD426CB1EB666EFEF148FBCE",
            networkType);
    private Account account3 = Account
        .createFromPrivateKey("336AB45EE65A6AFFC0E7ADC5342F91E34BACA0B901A1D9C876FA25A1E590077E",
            networkType);

    private Account account4 = Account
        .createFromPrivateKey("4D8B3756592532753344E11E2B7541317BCCFBBCF4444274CDBF359D2C4AE0F1",
            networkType);
    private String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

    @BeforeEach
    void setup() {

        RepositoryFactory factory = Mockito.mock(RepositoryFactory.class);
        MultisigRepository multisigRepository = Mockito.mock(MultisigRepository.class);
        Mockito.when(factory.createMultisigRepository()).thenReturn(multisigRepository);

        service = new AggregateTransactionServiceImpl(factory);

        Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(account1.getAddress())))
            .thenReturn(Observable.just(givenAccount1Info()));
        Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(account4.getAddress())))
            .thenReturn(Observable.just(givenAccount4Info()));
        Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(multisig2.getAddress())))
            .thenReturn(Observable.just(givenMultisig2AccountInfo()));
        Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(multisig3.getAddress())))
            .thenReturn(Observable.just(givenMultisig3AccountInfo()));
        Mockito.when(
            multisigRepository.getMultisigAccountGraphInfo(Mockito.eq(multisig2.getAddress())))
            .thenReturn(Observable.just(givenMultisig2AccountGraphInfo()));
        Mockito.when(
            multisigRepository.getMultisigAccountGraphInfo(Mockito.eq(multisig3.getAddress())))
            .thenReturn(Observable.just(givenMultisig3AccountGraphInfo()));
        Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(account2.getAddress())))
            .thenReturn(Observable.just(givenAccount2Info()));
        Mockito.when(multisigRepository.getMultisigAccountInfo(Mockito.eq(account3.getAddress())))
            .thenReturn(Observable.just(givenAccount3Info()));
    }

    public MultisigAccountInfo givenMultisig2AccountInfo() {
        return new MultisigAccountInfo(multisig2.getPublicAccount(),
            2, 1,
            Arrays.asList(multisig1.getPublicAccount(),
                account1.getPublicAccount()),
            Collections.emptyList()
        );
    }

    MultisigAccountInfo givenMultisig3AccountInfo() {
        return new MultisigAccountInfo(multisig3.getPublicAccount(),
            2, 2,
            Arrays.asList(account2.getPublicAccount(),
                account3.getPublicAccount()),
            Collections.emptyList()
        );
    }

    MultisigAccountInfo givenAccount1Info() {
        return new MultisigAccountInfo(account1.getPublicAccount(),
            0, 0,
            Collections.emptyList(),
            Collections.singletonList(multisig2.getPublicAccount())
        );
    }

    MultisigAccountInfo givenAccount2Info() {
        return new MultisigAccountInfo(account2.getPublicAccount(),
            0, 0,
            Collections.emptyList(),
            Arrays.asList(multisig2.getPublicAccount(),
                multisig3.getPublicAccount())
        );
    }

    MultisigAccountInfo givenAccount3Info() {
        return new MultisigAccountInfo(account3.getPublicAccount(),
            0, 0,
            Collections.emptyList(),
            Arrays.asList(multisig2.getPublicAccount(),
                multisig3.getPublicAccount())
        );
    }

    MultisigAccountInfo givenAccount4Info() {
        return new MultisigAccountInfo(account4.getPublicAccount(),
            0, 0,
            Collections.emptyList(),
            Collections.emptyList()
        );
    }


    MultisigAccountGraphInfo givenMultisig2AccountGraphInfo() {
        Map<Integer, List<MultisigAccountInfo>> map = new HashMap<>();
        map.put(1, Collections.singletonList(new MultisigAccountInfo(multisig2.getPublicAccount(),
            0, 1,
            Arrays.asList(multisig1.getPublicAccount(),
                account1.getPublicAccount()),
            Collections.emptyList()
        )));

        map.put(2, Collections.singletonList(new MultisigAccountInfo(multisig1.getPublicAccount(),
            1, 1,
            Arrays.asList(account2.getPublicAccount(), account3.getPublicAccount()),
            Collections.singletonList(multisig2.getPublicAccount())
        )));

        return new MultisigAccountGraphInfo(map);
    }

    MultisigAccountGraphInfo givenMultisig3AccountGraphInfo() {
        Map<Integer, List<MultisigAccountInfo>> map = new HashMap<>();
        map.put(0, Collections.singletonList(new MultisigAccountInfo(multisig3.getPublicAccount(),
            2, 2,
            Arrays.asList(account2.getPublicAccount(),
                account3.getPublicAccount()),
            Collections.emptyList()
        )));

        return new MultisigAccountGraphInfo(map);
    }

    /*
     * MLMA
     * Alice (account1): normal account
     * Bob (multisig2) - Multisig 2-1 (account1 && multisig1)
     * Charles (multisig1) - Multisig 1-1 (account2 || account3)
     * Given signatories: Account1 && Account4
     * Expecting complete as Bob needs 2 signatures (account1 && (account2 || account3))
     */

    @Test
    void shouldReturnIsCompleteTrueForAggregatedCompleteTransaction2LevelsMultisig()
        throws ExecutionException, InterruptedException {

        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            Address.createFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC"),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType, Collections
                .singletonList(transferTransaction.toAggregate(multisig2.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(account1, Collections.singletonList(account2),
                generationHash);

        Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());
    }

    /*
     * MLMA
     * Alice (account1): normal account
     * Bob (multisig2) - Multisig 2-1 (account1 && multisig1)
     * Charles (multisig1) - Multisig 1-1 (account2 || account3)
     * Given signatories: Account1 && Account4
     * Expecting incomplete as Bob needs 2 signatures (account1 && (account2 || account3)) but only got account1
     */
    @Test
    void shouldReturnIsCompleteFalseForAggregatedCompleteTransaction2LevelsMultisig()
        throws ExecutionException, InterruptedException {
        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            Address.createFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC"),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType, Collections
                .singletonList(transferTransaction.toAggregate(multisig2.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(account1, Collections.emptyList(), generationHash);
        Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());
    }

    /*
     * MLMA
     * Alice (account1): normal account
     * Bob (multisig2) - Multisig 2-1 (account1 && multisig1)
     * Charles (multisig1) - Multisig 1-1 (account2 || account3)
     * Given signatories: Account1 && Account4
     * Expecting incomplete as Bob needs 2 signatures (account1 && (account2 || account3)) but got account4
     */
    @Test
    void shouldReturnIsCompleteFalseForAggregatedCompleteTransaction2LevelsMultisig2()
        throws ExecutionException, InterruptedException {

        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            Address.createFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC"),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType, Collections
                .singletonList(transferTransaction.toAggregate(multisig2.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(account1, Collections.singletonList(account4),
                generationHash);
        Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());

    }

    /*
     * MLMA - with multiple transaction
     * Alice (account1): normal account
     * Bob (multisig2) - Multisig 2-1 (account1 && multisig1)
     * Charles (multisig1) - Multisig 1-1 (account2 || account3)
     * An extra inner transaction to account4 (just to increase the complexity)
     * Given signatories: Account1 && Account4
     * Expecting incomplete as Bob needs 2 signatures (account1 && (account2 || account3))
     */
    @Test
    void shouldReturnCorrectIsCompleteStatusForAggregatedCompleteTransaction2LevelsMultisigMultiinnerTransaction()
        throws ExecutionException, InterruptedException {
        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            Address.createFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC"),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        TransferTransaction transferTransaction2 = TransferTransactionFactory.create(
            networkType,
            account2.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType, Arrays
                .asList(transferTransaction.toAggregate(multisig2.getPublicAccount()),
                    transferTransaction2.toAggregate(account4.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(account1, Collections.singletonList(account4),
                generationHash);
        Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());
    }

    /*
     * MLMA - with multiple transaction
     * Alice (account1): normal account
     * Bob (multisig2) - Multisig 2-1 (account1 && multisig1)
     * Charles (multisig1) - Multisig 1-1 (account2 || account3)
     * An extra inner transaction to account4 (just to increase the complexity)
     * Given signatories: Account1 && Account4 && Account2
     * Expecting complete
     */
    @Test
    void shouldReturnCorrectIsCompleteStatusForAggregatedCompleteTransaction2LevelsMultisigMmultiInnerTransaction()
        throws ExecutionException, InterruptedException {
        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            account2.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        TransferTransaction transferTransaction2 = TransferTransactionFactory.create(
            networkType,
            account2.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType, Arrays
                .asList(transferTransaction.toAggregate(multisig2.getPublicAccount()),
                    transferTransaction2.toAggregate(account4.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(account1, Arrays.asList(account4, account2),
                generationHash);
        Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());


    }

    /*
     * If the inner transaction is issued to a multisig account
     * and the inner transaction itself is a ModifyMultiSigAccountTransaction - Removal
     * The validator should use minRemoval value rather than minApproval value
     * to determine if the act is complete or not
     */
    @Test
    void shouldUseMinRemovalForMultisigAccountValidationIfInnerTransactionIsModifyMultisigRemove()
        throws ExecutionException, InterruptedException {
        MultisigAccountModificationTransaction modifyMultisigTransaction = MultisigAccountModificationTransactionFactory
            .create(networkType,
                (byte) 1,
                (byte) 1,
                Collections.emptyList(),
                Collections.singletonList(account1.getPublicAccount())
            ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType, Collections
                .singletonList(modifyMultisigTransaction.toAggregate(multisig2.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signWith(account2,
                generationHash);
        Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());
    }

    /*
     * If the inner transaction is issued to a multisig account
     * and the inner transaction itself is a ModifyMultiSigAccountTransaction - Removal
     * The validator should use minRemoval value rather than minApproval value
     * to determine if the act is complete or not
     */
    @Test
    void shouldReturnCorrectIsCompleteStatusFalseForAggregatedCompleteTransactionNoneMultisig()
        throws ExecutionException, InterruptedException {
        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            account2.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType, Collections
                .singletonList(transferTransaction.toAggregate(account4.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signWith(account1,
                generationHash);
        Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());
    }

    /*
     * ACT
     * Alice (account1): normal account
     * Bob (account4) - normal account
     * Alice initiate the transaction
     * Bob sign
     */
    @Test
    void shouldReturnCorrectIsCompleteStatusTrueForAggregatedCompleteTransactionNoneMultisig()
        throws ExecutionException, InterruptedException {
        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            account2.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType,
            Collections.singletonList(transferTransaction.toAggregate(account4.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signWith(account4,
                generationHash);

        Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());
    }

    /*
     * ACT
     * Alice: account1
     * Bob: account4
     * An escrow contract is signed by all the participants (normal accounts)
     * Given Alice defined the following escrow contract:
     * | sender | recipient | type          | data |
     * | Alice  | Bob       | send-an-asset | 1 concert.ticket |
     * | Bob    | Alice     | send-an-asset | 20 euros |
     * And Bob signs the contract
     * And Alice signs the contract
     * Then the contract should appear as complete
     */
    @Test
    void shouldReturnCorrectIsCompleteStatuTrueMultipleNormalAccount()
        throws ExecutionException, InterruptedException {

        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            account1.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        TransferTransaction transferTransaction2 = TransferTransactionFactory.create(
            networkType,
            account4.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType,
            Arrays.asList(transferTransaction.toAggregate(account4.getPublicAccount()),
                transferTransaction2.toAggregate(account1.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(account1, Collections.singletonList(account4),
                generationHash);

        Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());
    }

    /*
     * ACT
     * Alice: account1
     * Bob: account4
     * An escrow contract is signed by all the participants (normal accounts)
     * Given Alice defined the following escrow contract:
     * | sender | recipient | type          | data |
     * | Alice  | Bob       | send-an-asset | 1 concert.ticket |
     * | Bob    | Alice     | send-an-asset | 20 euros |
     * And Alice signs the contract
     * Then the contract should appear as incomplete
     */
    @Test
    void shouldReturnCorrectIsCompleteStatusFalseMultipleNormalAccount()
        throws ExecutionException, InterruptedException {

        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            account1.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        TransferTransaction transferTransaction2 = TransferTransactionFactory.create(
            networkType,
            account4.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType,
            Arrays.asList(transferTransaction.toAggregate(account4.getPublicAccount()),
                transferTransaction2.toAggregate(account1.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(account1, Collections.emptyList(),
                generationHash);

        Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());

    }

    /*
     * ACT - Multisig single level
     * Alice (account1): initiate an transfer to Bob
     * Bob (multisig3): is a 2/2 multisig account (account2 && account3)
     */
    @Test
    void shouldReturnCorrectIsCompleteStatusTRUEMultisigSingleLevel()
        throws ExecutionException, InterruptedException {
        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            account4.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType,
            Collections
                .singletonList(transferTransaction.toAggregate(multisig3.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(account2, Collections.singletonList(account3),
                generationHash);

        Assertions.assertTrue(service.isComplete(signedTransaction).toFuture().get());
    }

    /*
     * ACT - Multisig single level
     * Alice (account1): initiate an transfer to Bob
     * Bob (multisig3): is a 2/2 multisig account (account2 && account3)
     */
    @Test
    void shouldReturnCorrectIsCompleteStatusFALSEMultisigSingleLevel()
        throws ExecutionException, InterruptedException {
        TransferTransaction transferTransaction = TransferTransactionFactory.create(
            networkType,
            account4.getAddress(),
            Collections.emptyList(),
            PlainMessage.create("test-message")
        ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(
            networkType,
            Collections
                .singletonList(transferTransaction.toAggregate(multisig3.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = aggregateTransaction
            .signTransactionWithCosigners(account2, Collections.emptyList(),
                generationHash);

        Assertions.assertFalse(service.isComplete(signedTransaction).toFuture().get());
    }


}
