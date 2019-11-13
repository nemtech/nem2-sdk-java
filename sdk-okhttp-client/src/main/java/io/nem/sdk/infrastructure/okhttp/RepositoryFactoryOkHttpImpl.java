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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.ChainRepository;
import io.nem.sdk.api.DiagnosticRepository;
import io.nem.sdk.api.JsonSerialization;
import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.api.MultisigRepository;
import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.api.ReceiptRepository;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.RestrictionAccountRepository;
import io.nem.sdk.api.RestrictionMosaicRepository;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.infrastructure.Listener;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import okhttp3.OkHttpClient;
import org.apache.commons.io.IOUtils;

/**
 * Vertx implementation of a {@link RepositoryFactory}
 *
 * @author Fernando Boucquez
 */

public class RepositoryFactoryOkHttpImpl implements RepositoryFactory {


    private final ApiClient apiClient;

    private final String baseUrl;

    public RepositoryFactoryOkHttpImpl(String baseUrl) {
        this.baseUrl = baseUrl;
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(baseUrl);
        apiClient.getJSON().setGson(JsonHelperGson.creatGson(false));
    }

    @Override
    public AccountRepository createAccountRepository() {
        return new AccountRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public MultisigRepository createMultisigRepository() {
        return new MultisigRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public BlockRepository createBlockRepository() {
        return new BlockRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public ReceiptRepository createReceiptRepository() {
        return new ReceiptRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public ChainRepository createChainRepository() {
        return new ChainRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public DiagnosticRepository createDiagnosticRepository() {
        return new DiagnosticRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public MosaicRepository createMosaicRepository() {
        return new MosaicRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public NamespaceRepository createNamespaceRepository() {
        return new NamespaceRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public NetworkRepository createNetworkRepository() {
        return new NetworkRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public NodeRepository createNodeRepository() {
        return new NodeRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public TransactionRepository createTransactionRepository() {
        return new TransactionRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public MetadataRepository createMetadataRepository() {
        return new MetadataRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public RestrictionAccountRepository createRestrictionAccountRepository() {
        return new RestrictionAccountRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public RestrictionMosaicRepository createRestrictionMosaicRepository() {
        return new RestrictionMosaicRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public Listener createListener() {
        return new ListenerOkHttp(apiClient.getHttpClient(), baseUrl, apiClient.getJSON());
    }

    @Override
    public JsonSerialization createJsonSerialization() {
        return new JsonSerializationOkHttp(apiClient.getJSON().getGson());
    }

    @Override
    public void close() {
        OkHttpClient client = apiClient.getHttpClient();
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        IOUtils.closeQuietly(client.cache());
    }
}
