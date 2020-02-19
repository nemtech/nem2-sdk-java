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

import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.ChainRepository;
import io.nem.sdk.api.JsonSerialization;
import io.nem.sdk.api.Listener;
import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.api.MultisigRepository;
import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.api.ReceiptRepository;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.RepositoryFactoryConfiguration;
import io.nem.sdk.api.RestrictionAccountRepository;
import io.nem.sdk.api.RestrictionMosaicRepository;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.infrastructure.RepositoryFactoryBase;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * Vertx implementation of a {@link RepositoryFactory}
 *
 * @author Fernando Boucquez
 */

public class RepositoryFactoryVertxImpl extends RepositoryFactoryBase {

    private final ApiClient apiClient;

    private final WebClient webClient;

    private final Vertx vertx;

    public RepositoryFactoryVertxImpl(String baseUrl) {
        this(new RepositoryFactoryConfiguration(baseUrl));
    }

    public RepositoryFactoryVertxImpl(RepositoryFactoryConfiguration configuration) {
        super(configuration);
        vertx = Vertx.vertx();
        webClient = WebClient.create(vertx);
        this.apiClient = new ApiClient(vertx, new JsonObject().put("basePath", getBaseUrl())) {
            @Override
            public synchronized WebClient getWebClient() {
                return webClient;
            }
        };
        //Note: For some reason the generated code use to mapper instances.
        JsonHelperJackson2.configureMapper(apiClient.getObjectMapper());
        JsonHelperJackson2.configureMapper(Json.mapper);
    }


    @Override
    public AccountRepository createAccountRepository() {
        return new AccountRepositoryVertxImpl(apiClient);
    }

    @Override
    public MultisigRepository createMultisigRepository() {
        return new MultisigRepositoryVertxImpl(apiClient, getNetworkType());
    }

    @Override
    public BlockRepository createBlockRepository() {
        return new BlockRepositoryVertxImpl(apiClient);
    }

    @Override
    public ReceiptRepository createReceiptRepository() {
        return new ReceiptRepositoryVertxImpl(apiClient, getNetworkType());
    }

    @Override
    public ChainRepository createChainRepository() {
        return new ChainRepositoryVertxImpl(apiClient);
    }

    @Override
    public MosaicRepository createMosaicRepository() {
        return new MosaicRepositoryVertxImpl(apiClient, getNetworkType());
    }

    @Override
    public NamespaceRepository createNamespaceRepository() {
        return new NamespaceRepositoryVertxImpl(apiClient, getNetworkType());
    }

    @Override
    public NetworkRepository createNetworkRepository() {
        return new NetworkRepositoryVertxImpl(apiClient);
    }

    @Override
    public NodeRepository createNodeRepository() {
        return new NodeRepositoryVertxImpl(apiClient);
    }

    @Override
    public TransactionRepository createTransactionRepository() {
        return new TransactionRepositoryVertxImpl(apiClient);
    }

    @Override
    public MetadataRepository createMetadataRepository() {
        return new MetadataRepositoryVertxImpl(apiClient);
    }

    @Override
    public RestrictionAccountRepository createRestrictionAccountRepository() {
        return new RestrictionAccountRepositoryVertxImpl(apiClient);
    }

    @Override
    public RestrictionMosaicRepository createRestrictionMosaicRepository() {
        return new RestrictionMosaicRepositoryVertxImpl(apiClient);
    }

    @Override
    public Listener createListener() {
        return new ListenerVertx(vertx.createHttpClient(), getBaseUrl());
    }

    @Override
    public JsonSerialization createJsonSerialization() {
        return new JsonSerializationVertx(apiClient.getObjectMapper());
    }

    @Override
    public void close() {

        vertx.close();
        try {
            webClient.close();
        } catch (IllegalStateException e) {
            //Failing quietly
        }
    }
}
