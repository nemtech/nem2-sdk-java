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

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.ChainRepository;
import io.nem.sdk.api.DiagnosticRepository;
import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.infrastructure.Listener;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.ByteArrayAdapter;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.DateTypeAdapter;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.LocalDateTypeAdapter;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.OffsetDateTimeTypeAdapter;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.SqlDateTypeAdapter;
import java.util.Collection;
import java.util.Date;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;

/**
 * Vertx implementation of a {@link RepositoryFactory}
 *
 * @author Fernando Boucquez
 */

public class RepositoryFactoryOkHttpImpl implements RepositoryFactory {

    /**
     * The low level open api generated client. The repositories use the client to perform the rest
     * calls.
     */
    private final ApiClient apiClient;

    /**
     * The base url of the Catapult server.
     */
    private final String baseUrl;

    /**
     * The sign schema used to generate Addresses. At the moment the user needs to configure it or
     * use the default. The server should provide how's the configuration.
     */
    private final SignSchema signSchema;

    public RepositoryFactoryOkHttpImpl(String baseUrl, SignSchema signSchema) {
        this.baseUrl = baseUrl;
        this.signSchema = signSchema;
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(baseUrl);

        JSON json = apiClient.getJSON();

        DateTypeAdapter dateTypeAdapter = new DateTypeAdapter();
        SqlDateTypeAdapter sqlDateTypeAdapter = new SqlDateTypeAdapter();
        OffsetDateTimeTypeAdapter offsetDateTimeTypeAdapter = new OffsetDateTimeTypeAdapter();
        LocalDateTypeAdapter localDateTypeAdapter = json.new LocalDateTypeAdapter();
        ByteArrayAdapter byteArrayAdapter = json.new ByteArrayAdapter();

        json.setGson(JSON.createGson().registerTypeHierarchyAdapter(
            Collection.class, new CollectionAdapter())
            .registerTypeAdapter(Date.class, dateTypeAdapter)
            .registerTypeAdapter(java.sql.Date.class, sqlDateTypeAdapter)
            .registerTypeAdapter(OffsetDateTime.class, offsetDateTimeTypeAdapter)
            .registerTypeAdapter(LocalDate.class, localDateTypeAdapter)
            .registerTypeAdapter(byte[].class, byteArrayAdapter)
            .create());
    }

    @Override
    public AccountRepository createAccountRepository() {
        return new AccountRepositoryOkHttpImpl(apiClient, signSchema);
    }

    @Override
    public BlockRepository createBlockRepository() {
        return new BlockRepositoryOkHttpImpl(apiClient, signSchema);
    }

    @Override
    public ChainRepository createChainRepository() {
        return new ChainRepositoryOkHttpImpl(apiClient, signSchema);
    }

    @Override
    public DiagnosticRepository createDiagnosticRepository() {
        return new DiagnosticRepositoryOkHttpImpl(apiClient, signSchema);
    }

    @Override
    public MosaicRepository createMosaicRepository() {
        return new MosaicRepositoryOkHttpImpl(apiClient, signSchema);
    }

    @Override
    public NamespaceRepository createNamespaceRepository() {
        return new NamespaceRepositoryOkHttpImpl(apiClient, signSchema);
    }

    @Override
    public NetworkRepository createNetworkRepository() {
        return new NetworkRepositoryOkHttpImpl(apiClient, signSchema);
    }

    @Override
    public NodeRepository createNodeRepository() {
        return new NodeRepositoryOkHttpImpl(apiClient, signSchema);
    }

    @Override
    public TransactionRepository createTransactionRepository() {
        return new TransactionRepositoryOkHttpImpl(apiClient, signSchema);
    }

    @Override
    public Listener createListener() {
        return new ListenerOkHttp(apiClient.getHttpClient(), baseUrl, apiClient.getJSON(),
            signSchema);
    }
}
