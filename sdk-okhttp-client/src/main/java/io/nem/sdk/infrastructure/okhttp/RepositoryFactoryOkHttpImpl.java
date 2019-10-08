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
import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.RestrictionRepository;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.infrastructure.Listener;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.ByteArrayAdapter;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.DateTypeAdapter;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.LocalDateTypeAdapter;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.OffsetDateTimeTypeAdapter;
import io.nem.sdk.openapi.okhttp_gson.invoker.JSON.SqlDateTypeAdapter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import okhttp3.OkHttpClient;
import org.apache.commons.io.IOUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;

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
        return new AccountRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public BlockRepository createBlockRepository() {
        return new BlockRepositoryOkHttpImpl(apiClient);
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
    public RestrictionRepository createRestrictionRepository() {
        return new RestrictionRepositoryOkHttpImpl(apiClient);
    }

    @Override
    public Listener createListener() {
        return new ListenerOkHttp(apiClient.getHttpClient(), baseUrl, apiClient.getJSON());
    }

    @Override
    public void close() {
        OkHttpClient client = apiClient.getHttpClient();
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        IOUtils.closeQuietly(client.cache());
    }
}
