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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.infrastructure.okhttp.RepositoryFactoryOkHttpImpl;
import io.nem.sdk.infrastructure.vertx.RepositoryFactoryVertxImpl;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for all the repository integration tests.
 *
 * In general, the test ares parametrized so multiple implementations of a repository can be tested
 * at the same time.
 */
public abstract class BaseIntegrationTest {

    protected static SignSchema signSchema = SignSchema.DEFAULT;

    /**
     * Known implementations of repositories that the integration tests use.
     */
    public enum RepositoryType {
        VERTX, OKHTTP
    }


    private static final Config CONFIG = Config.getInstance();
    private NetworkType networkType;
    private Account testAccount;
    private Account testMultisigAccount;
    private Account testCosignatoryAccount;
    private Account testCosignatoryAccount2;
    private PublicAccount testPublicAccount;
    private Address testAccountAddress;
    private Address testRecipient; // Test Account2 Address
    private String generationHash;
    private Long timeoutSeconds;
    private Map<RepositoryType, RepositoryFactory> repositoryFactoryMap = new HashMap<>();


    /**
     * Method that create a {@link RepositoryFactory} based on the {@link RepositoryType} if
     * necessary. The created repository factories are being cached for performance and multithread
     * testing.
     */
    public RepositoryFactory getRepositoryFactory(RepositoryType type) {
        return repositoryFactoryMap.computeIfAbsent(type, this::createRepositoryFactory);
    }

    /**
     * Method that creates a {@link RepositoryFactory} based on the {@link RepositoryType}.
     */
    private RepositoryFactory createRepositoryFactory(RepositoryType type) {

        switch (type) {
            case VERTX:
                return new RepositoryFactoryVertxImpl(getApiUrl(), signSchema);
            case OKHTTP:
                return new RepositoryFactoryOkHttpImpl(getApiUrl(), signSchema);
            default:
                throw new IllegalStateException("Invalid Repository type " + type);
        }
    }

    public Config config() {
        return BaseIntegrationTest.CONFIG;
    }

    public String getApiUrl() {
        return this.config().getApiUrl() + "/";
    }

    public NetworkType getNetworkType() {
        if (this.networkType == null) {
            this.networkType = NetworkType.valueOf(this.config().getNetworkType());
        }
        return this.networkType;
    }

    public Account getTestAccount() {
        if (this.testAccount == null) {
            this.testAccount =
                Account.createFromPrivateKey(
                    this.config().getTestAccountPrivateKey(), this.getNetworkType(), signSchema);
        }
        return this.testAccount;
    }

    public PublicAccount getTestPublicAccount() {
        if (this.testPublicAccount == null) {
            this.testPublicAccount =
                PublicAccount.createFromPublicKey(
                    this.config().getTestAccountPublicKey(), this.getNetworkType(), SignSchema.DEFAULT);
        }
        return this.testPublicAccount;
    }

    public Address getTestAccountAddress() {
        if (this.testAccountAddress == null) {
            this.testAccountAddress = Address
                .createFromRawAddress(this.config().getTestAccountAddress());
        }
        return this.testAccountAddress;
    }

    public Account getTestMultisigAccount() {
        if (this.testMultisigAccount == null) {
            this.testMultisigAccount =
                Account.createFromPrivateKey(
                    this.config().getMultisigAccountPrivateKey(), this.getNetworkType(), signSchema);
        }
        return this.testMultisigAccount;
    }

    public Account getTestCosignatoryAccount() {
        if (this.testCosignatoryAccount == null) {
            this.testCosignatoryAccount =
                Account.createFromPrivateKey(
                    this.config().getCosignatoryAccountPrivateKey(), this.getNetworkType(),
                    signSchema);
        }
        return this.testCosignatoryAccount;
    }

    public Account getTestCosignatoryAccount2() {
        if (this.testCosignatoryAccount2 == null) {
            this.testCosignatoryAccount2 =
                Account.createFromPrivateKey(
                    this.config().getCosignatory2AccountPrivateKey(), this.getNetworkType(),
                    signSchema);
        }
        return this.testCosignatoryAccount2;
    }

    public Address getRecipient() {
        if (this.testRecipient == null) {
            this.testRecipient = Address
                .createFromRawAddress(this.config().getTestAccount2Address());
        }
        return this.testRecipient;
    }

    public String getGenerationHash() {
        if (this.generationHash == null) {
            this.generationHash = this.config().getGenerationHash();
        }
        return this.generationHash;
    }

    public Long getTimeoutSeconds() {
        if (this.timeoutSeconds == null) {
            this.timeoutSeconds = this.config().getTimeoutSeconds();
        }
        return this.timeoutSeconds;
    }
}
