/*
 * Copyright 2018 NEM
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

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.core.crypto.Hashes;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.AccountNames;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.mosaic.MosaicFlags;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.CosignatoryModificationActionType;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.CosignatureTransaction;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.LockHashAlgorithmType;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.MosaicRestrictionType;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.sdk.model.transaction.MultisigCosignatoryModification;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.SecretLockTransaction;
import io.nem.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//TODO BROKEN!!
class E2EIntegrationTest extends BaseIntegrationTest {

    private Account account;
    private Address recipient;
    private Account multisigAccount;
    private Account cosignatoryAccount;
    private Account cosignatoryAccount2;
    private NamespaceId rootNamespaceId;
    private MosaicId mosaicId;
    private String generationHash;
    private long timeoutSeconds;

    @BeforeAll
    void setup() {
        account = this.config().getNemesisAccount();
        recipient = this.getRecipient();
        multisigAccount = this.getTestMultisigAccount();
        cosignatoryAccount = this.getTestCosignatoryAccount();
        cosignatoryAccount2 = this.getTestCosignatoryAccount2();
        generationHash = this.getGenerationHash();
        timeoutSeconds = this.getTimeoutSeconds();
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void standaloneTransferTransaction(RepositoryType type) {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                Optional.of(this.recipient),
                Optional.empty(),
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                new PlainMessage("E2ETest:standaloneTransferTransaction:message")
            ).build();

        announceAndValidate(type, this.account, transferTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateTransferTransaction(RepositoryType type) {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                Optional.of(this.recipient),
                Optional.empty(),
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                /*new PlainMessage(
                    "E2ETest:aggregateTransferTransaction:message"), */
// short message for debugging
                new PlainMessage(
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
                        "oooooooong")
                // Use long message to test if size of inner transaction is calculated correctly
            ).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    transferTransaction.toAggregate(this.account.getPublicAccount()))).build();

        announceAndValidate(type, this.account, aggregateTransaction);
    }

    private TransactionRepository getTransactionRepository(
        RepositoryType type) {
        return getRepositoryFactory(type).createTransactionRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneRootRegisterNamespaceTransaction(RepositoryType type) {
        String namespaceName =
            "test-root-namespace-" + new Double(Math.floor(Math.random() * 10000)).intValue();

        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(),
                namespaceName,
                BigInteger.valueOf(100)).build();

        this.rootNamespaceId = namespaceRegistrationTransaction.getNamespaceId();

        announceAndValidate(type, this.account, namespaceRegistrationTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateRootRegisterNamespaceTransaction(RepositoryType type) {
        String namespaceName =
            "test-root-namespace-" + new Double(Math.floor(Math.random() * 10000)).intValue();

        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createRootNamespace(
                getNetworkType(),
                namespaceName,
                BigInteger.valueOf(100)).build();

        this.rootNamespaceId = namespaceRegistrationTransaction.getNamespaceId();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(getNetworkType(),
                Collections.singletonList(
                    namespaceRegistrationTransaction.toAggregate(this.account.getPublicAccount()))
            ).build();

        announceAndValidate(type, this.account, aggregateTransaction);
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneSubNamespaceRegisterNamespaceTransaction(RepositoryType type) {

        this.standaloneRootRegisterNamespaceTransaction(type);

        String namespaceName =
            "test-sub-namespace-" + new Double(Math.floor(Math.random() * 10000)).intValue();

        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createSubNamespace(
                getNetworkType(),
                namespaceName,
                this.rootNamespaceId).build();

        announceAndValidate(type, this.account, namespaceRegistrationTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateSubNamespaceRegisterNamespaceTransaction(RepositoryType type) {

        this.aggregateRootRegisterNamespaceTransaction(type);

        String namespaceName =
            "test-sub-namespace-" + new Double(Math.floor(Math.random() * 10000)).intValue();

        NamespaceRegistrationTransaction namespaceRegistrationTransaction =
            NamespaceRegistrationTransactionFactory.createSubNamespace(
                getNetworkType(),
                namespaceName,
                this.rootNamespaceId).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    namespaceRegistrationTransaction.toAggregate(this.account.getPublicAccount()))
            ).build();

        announceAndValidate(type, this.account, aggregateTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneMosaicDefinitionTransaction(RepositoryType type) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        this.mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransactionFactory.create(
                getNetworkType(),
                nonce,
                this.mosaicId,
                MosaicFlags.create(true, true, true),
                4, new BlockDuration(100)).build();

        announceAndValidate(type, this.account, mosaicDefinitionTransaction);
    }


    private MosaicId createMosaic(RepositoryType type) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        MosaicId mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransactionFactory.create(getNetworkType(),
                nonce,
                mosaicId,
                MosaicFlags.create(true, true, true),
                4, new BlockDuration(100)).build();

        return announceAndValidate(type, account, mosaicDefinitionTransaction).getMosaicId();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateMosaicDefinitionTransaction(RepositoryType type) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        this.mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransactionFactory.create(
                getNetworkType(),
                nonce,
                this.mosaicId,
                MosaicFlags.create(true, false, true),
                4, new BlockDuration(100)).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    mosaicDefinitionTransaction.toAggregate(this.account.getPublicAccount()))
            ).build();

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getTransactionRepository(type).announce(signedTransaction));
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneMosaicSupplyChangeTransaction(RepositoryType type) {
        this.standaloneMosaicDefinitionTransaction(type);

        MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
            MosaicSupplyChangeTransactionFactory.create(getNetworkType(),
                this.mosaicId,
                MosaicSupplyChangeActionType.INCREASE,
                BigInteger.valueOf(11)
            ).build();

        SignedTransaction signedTransaction =
            this.account.sign(mosaicSupplyChangeTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getTransactionRepository(type).announce(signedTransaction));
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateMosaicSupplyChangeTransaction(RepositoryType type) {
        this.aggregateMosaicDefinitionTransaction(type);

        MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
            MosaicSupplyChangeTransactionFactory.create(
                getNetworkType(),
                this.mosaicId,
                MosaicSupplyChangeActionType.INCREASE,
                BigInteger.valueOf(12)).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    mosaicSupplyChangeTransaction.toAggregate(this.account.getPublicAccount()))
            ).build();

        announceAndValidate(type, this.account, aggregateTransaction);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateMosaicAddressRestrictionTransaction(RepositoryType type) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        this.mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicAddressRestrictionTransaction mosaicAddressRestrictionTransaction =
            MosaicAddressRestrictionTransactionFactory.create(
                getNetworkType(),
                this.mosaicId, // restricted MosaicId
                BigInteger.valueOf(1), // restrictionKey
                this.account.getAddress(),  // targetAddress
                // previousRestrictionValue
                BigInteger.valueOf(8)  // newRestrictionValue
            ).previousRestrictionValue(BigInteger.valueOf(9)).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    mosaicAddressRestrictionTransaction
                        .toAggregate(this.account.getPublicAccount()))
            ).build();

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getTransactionRepository(type).announce(signedTransaction));
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneMosaicGlobalRestrictionTransaction(RepositoryType type) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        this.mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction =
            MosaicGlobalRestrictionTransactionFactory.create(
                getNetworkType(),
                this.mosaicId, // restrictedMosaicId
                BigInteger.valueOf(1),    // restrictionKey
                BigInteger.valueOf(8),    // newRestrictionValue
                MosaicRestrictionType.GE  // newRestrictionType
            ).previousRestrictionValue(BigInteger.valueOf(9))
                .previousRestrictionType(MosaicRestrictionType.EQ).build();

        SignedTransaction signedTransaction =
            this.account.sign(mosaicGlobalRestrictionTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getTransactionRepository(type).announce(signedTransaction));
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateMosaicGlobalRestrictionTransaction(RepositoryType type) {
        MosaicNonce nonce = MosaicNonce.createRandom();
        this.mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction =
            MosaicGlobalRestrictionTransactionFactory.create(
                getNetworkType(),
                this.mosaicId, // restrictedMosaicId
                BigInteger.valueOf(1),    // restrictionKey
                BigInteger.valueOf(8),    // newRestrictionValue
                MosaicRestrictionType.GE  // newRestrictionType
            ).previousRestrictionValue(BigInteger.valueOf(9))
                .previousRestrictionType(MosaicRestrictionType.EQ).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    mosaicGlobalRestrictionTransaction.toAggregate(this.account.getPublicAccount()))
            ).build();

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getTransactionRepository(type).announce(signedTransaction));
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneLockFundsTransaction(RepositoryType type) {
        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createBonded(
                getNetworkType(),
                Collections.emptyList()).build();
        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);
        HashLockTransaction lockFundstx =
            HashLockTransactionFactory.create(getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction
            ).build();

        SignedTransaction lockFundsTransactionSigned = this.account
            .sign(lockFundstx, generationHash);
        get(getTransactionRepository(type).announce(lockFundsTransactionSigned));

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateLockFundsTransaction(RepositoryType type) {
        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createBonded(
                getNetworkType(),
                Collections.emptyList()).build();
        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);
        HashLockTransaction lockFundstx =
            HashLockTransactionFactory.create(
                getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction).build();

        AggregateTransaction lockFundsAggregatetx =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(lockFundstx.toAggregate(this.account.getPublicAccount()))
            ).build();

        SignedTransaction lockFundsTransactionSigned =
            this.account.sign(lockFundsAggregatetx, generationHash);

        get(getTransactionRepository(type).announce(lockFundsTransactionSigned));

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneSecretLockTransaction(RepositoryType type) {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        SecretLockTransaction secretLocktx =
            SecretLockTransactionFactory.create(getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM")
            ).build();

        SignedTransaction secretLockTransactionSigned = this.account
            .sign(secretLocktx, generationHash);

        get(getTransactionRepository(type).announce(secretLockTransactionSigned));

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretLockTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateSecretLockTransaction(RepositoryType type) {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        SecretLockTransaction secretLocktx =
            SecretLockTransactionFactory.create(
                getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM")
            ).build();

        AggregateTransaction secretLockAggregatetx =
            AggregateTransactionFactory.createComplete(getNetworkType(),
                Collections.singletonList(secretLocktx.toAggregate(this.account.getPublicAccount()))
            ).build();

        SignedTransaction secretLockTransactionSigned =
            this.account.sign(secretLockAggregatetx, generationHash);

        get(getTransactionRepository(type).announce(secretLockTransactionSigned));

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretLockTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneSecretProofTransaction(RepositoryType type) {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        String proof = Hex.encodeHexString(secretBytes);
        SecretLockTransaction secretLocktx =
            SecretLockTransactionFactory.create(
                getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM")
            ).build();

        SignedTransaction lockFundsTransactionSigned = this.account
            .sign(secretLocktx, generationHash);

        get(getTransactionRepository(type).announce(lockFundsTransactionSigned));

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash(), type);

        SecretProofTransaction secretProoftx =
            SecretProofTransactionFactory.create(
                getNetworkType(),
                LockHashAlgorithmType.SHA3_256,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"),
                secret,
                proof).build();

        SignedTransaction secretProofTransactionSigned =
            this.account.sign(secretProoftx, generationHash);

        get(getTransactionRepository(type).announce(secretProofTransactionSigned));

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretProofTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateSecretProofTransaction(RepositoryType type) {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        String proof = Hex.encodeHexString(secretBytes);
        SecretLockTransaction secretLocktx =
            SecretLockTransactionFactory.create(
                getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                LockHashAlgorithmType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM")).build();

        SignedTransaction lockFundsTransactionSigned = this.account
            .sign(secretLocktx, generationHash);

        get(getTransactionRepository(type).announce(lockFundsTransactionSigned));

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash(), type);

        SecretProofTransaction secretProoftx =
            SecretProofTransactionFactory.create(getNetworkType(),
                LockHashAlgorithmType.SHA3_256,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"),
                secret,
                proof
            ).build();

        AggregateTransaction secretProofAggregatetx =
            AggregateTransactionFactory.createComplete(getNetworkType(),
                Collections
                    .singletonList(secretProoftx.toAggregate(this.account.getPublicAccount()))
            ).build();

        SignedTransaction secretProofTransactionSigned =
            this.account.sign(secretProofAggregatetx, generationHash);

        get(getTransactionRepository(type).announce(secretProofTransactionSigned));

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretProofTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldSignModifyMultisigAccountTransactionWithCosignatories(RepositoryType type) {
        MultisigAccountModificationTransaction multisigAccountModificationTransaction =
            MultisigAccountModificationTransactionFactory.create(
                getNetworkType(),
                (byte) 0,
                (byte) 0,
                Collections.singletonList(
                    new MultisigCosignatoryModification(
                        CosignatoryModificationActionType.ADD,
                        this.cosignatoryAccount2.getPublicAccount()))).build();
        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    multisigAccountModificationTransaction.toAggregate(
                        this.multisigAccount.getPublicAccount()))
            ).build();

        SignedTransaction signedTransaction =
            this.cosignatoryAccount.signTransactionWithCosignatories(
                aggregateTransaction,
                Collections.singletonList(this.cosignatoryAccount2),
                generationHash);

        HashLockTransaction hashLockTransaction =
            HashLockTransactionFactory.create(
                getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction).build();

        SignedTransaction lockFundsSignedTransaction =
            this.cosignatoryAccount.sign(hashLockTransaction, generationHash);

        get(getTransactionRepository(type).announce(lockFundsSignedTransaction));

        get(getListener(type).confirmed(this.cosignatoryAccount.getAddress()).take(1));

        get(getTransactionRepository(type).announceAggregateBonded(signedTransaction));

        this.validateAggregateBondedTransactionAnnounceCorrectly(
            this.cosignatoryAccount.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void cosignatureTransaction(RepositoryType type) {
        TransferTransaction transferTransaction =
            TransferTransactionFactory.create(
                getNetworkType(),
                Optional.of(new Address("SDRDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY", getNetworkType())),
                Optional.empty(),
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                PlainMessage.create("test-message")
            ).build();

        AggregateTransaction aggregateTransaction =
            AggregateTransactionFactory.createComplete(
                getNetworkType(),
                Collections.singletonList(
                    transferTransaction.toAggregate(this.multisigAccount.getPublicAccount())))
                .build();
        SignedTransaction signedTransaction =
            this.cosignatoryAccount.sign(aggregateTransaction, generationHash);

        HashLockTransaction hashLockTransaction =
            HashLockTransactionFactory.create(getNetworkType(),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction).build();

        SignedTransaction lockFundsSignedTransaction =
            this.cosignatoryAccount.sign(hashLockTransaction, generationHash);

        get(getTransactionRepository(type).announce(lockFundsSignedTransaction));

        get(getListener(type).confirmed(this.cosignatoryAccount.getAddress()).take(1));

        get(getTransactionRepository(type).announceAggregateBonded(signedTransaction));

        this.validateAggregateBondedTransactionAnnounceCorrectly(
            this.cosignatoryAccount.getAddress(), signedTransaction.getHash(), type);

        CosignatureTransaction cosignatureTransaction =
            CosignatureTransaction.create(aggregateTransaction);

        CosignatureSignedTransaction cosignatureSignedTransaction =
            this.cosignatoryAccount2.signCosignatureTransaction(cosignatureTransaction);

        get(getTransactionRepository(type)
            .announceAggregateBondedCosignature(cosignatureSignedTransaction));

        this.validateAggregateBondedCosignatureTransactionAnnounceCorrectly(
            this.cosignatoryAccount.getAddress(), cosignatureSignedTransaction.getParentHash(),
            type);
    }


}
