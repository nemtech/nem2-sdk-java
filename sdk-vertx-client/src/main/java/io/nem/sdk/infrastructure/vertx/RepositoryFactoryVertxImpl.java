/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import io.nem.core.crypto.SignSchema;
import io.nem.core.utils.Suppliers;
import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.ChainRepository;
import io.nem.sdk.api.DiagnosticRepository;
import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.infrastructure.Listener;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Vertx implementation of a {@link RepositoryFactory}
 *
 * @author Fernando Boucquez
 */

public class RepositoryFactoryVertxImpl implements RepositoryFactory {

    /**
     * The low level open api generated client. The repositories use the client to perform the rest
     * calls.
     */
    private final ApiClient apiClient;

    /**
     * The lazily load network type configuration.
     */
    private final Supplier<NetworkType> networkType;

    /**
     * The base url of the Catapult server.
     */
    private final String baseUrl;

    /**
     * The sign schema used to generate Addresses. At the moment the user needs to configure it or
     * use the default. The server should provide how's the configuration.
     */
    private final SignSchema signSchema;

    public RepositoryFactoryVertxImpl(String baseUrl, SignSchema signSchema) {
        this.baseUrl = baseUrl;
        this.signSchema = signSchema;
        Vertx vertx = Vertx.vertx();
        this.apiClient = new ApiClient(vertx, new JsonObject().put("basePath", baseUrl)) {
            @Override
            public synchronized WebClient getWebClient() {
                return WebClient.create(vertx);
            }
        };
        //Note: For some reason the genereated code use to mapper instances.
        JsonHelperJackson2.configureMapper(apiClient.getObjectMapper());
        JsonHelperJackson2.configureMapper(Json.mapper);

        this.networkType = Suppliers.memoize(this::loadNetworkType);
        networkType.get();
    }

    protected NetworkType loadNetworkType() {
        try {
            return io.nem.core.utils.ExceptionUtils.propagate(() -> {
                NetworkRepositoryVertxImpl networkRepository = new NetworkRepositoryVertxImpl(
                    apiClient, signSchema);
                return networkRepository.getNetworkType().toFuture().get(10, TimeUnit.SECONDS);
            });
        } catch (Exception e) {
            throw new RepositoryCallException(
                "Unable to load NetworkType. Error: " + ExceptionUtils.getMessage(e), e);
        }
    }


    @Override
    public AccountRepository createAccountRepository() {
        return new AccountRepositoryVertxImpl(apiClient, networkType, signSchema);
    }

    @Override
    public BlockRepository createBlockRepository() {
        return new BlockRepositoryVertxImpl(apiClient, networkType, signSchema);
    }

    @Override
    public ChainRepository createChainRepository() {
        return new ChainRepositoryVertxImpl(apiClient, networkType, signSchema);
    }

    @Override
    public DiagnosticRepository createDiagnosticRepository() {
        return new DiagnosticRepositoryVertxImpl(apiClient, networkType, signSchema);
    }

    @Override
    public MosaicRepository createMosaicRepository() {
        return new MosaicRepositoryVertxImpl(apiClient, networkType, signSchema);
    }

    @Override
    public NamespaceRepository createNamespaceRepository() {
        return new NamespaceRepositoryVertxImpl(apiClient, networkType, signSchema);
    }

    @Override
    public NetworkRepository createNetworkRepository() {
        return new NetworkRepositoryVertxImpl(apiClient, signSchema);
    }

    @Override
    public NodeRepository createNodeRepository() {
        return new NodeRepositoryVertxImpl(apiClient, networkType, signSchema);
    }

    @Override
    public TransactionRepository createTransactionRepository() {
        return new TransactionRepositoryVertxImpl(apiClient, networkType, signSchema);
    }

    @Override
    public Listener createListener() {
        return new ListenerVertx(baseUrl, signSchema);
    }
}
