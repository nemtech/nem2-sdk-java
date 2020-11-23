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
package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.BlockRepository;
import io.nem.symbol.sdk.api.ChainRepository;
import io.nem.symbol.sdk.api.FinalizationRepository;
import io.nem.symbol.sdk.api.HashLockRepository;
import io.nem.symbol.sdk.api.JsonSerialization;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.api.NodeRepository;
import io.nem.symbol.sdk.api.ReceiptRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RepositoryFactoryConfiguration;
import io.nem.symbol.sdk.api.RestrictionAccountRepository;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.api.SecretLockRepository;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionStatusRepository;
import io.nem.symbol.sdk.infrastructure.RepositoryFactoryBase;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
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
    this.apiClient =
        new ApiClient(vertx, new JsonObject().put("basePath", getBaseUrl())) {
          @Override
          public synchronized WebClient getWebClient() {
            return webClient;
          }
        };
    // Note: For some reason the generated code use to mapper instances.
    JsonHelperJackson2.configureMapper(apiClient.getObjectMapper());
    JsonHelperJackson2.configureMapper(Json.mapper);
  }

  @Override
  public AccountRepository createAccountRepository() {
    return new AccountRepositoryVertxImpl(apiClient);
  }

  @Override
  public MultisigRepository createMultisigRepository() {
    return new MultisigRepositoryVertxImpl(apiClient);
  }

  @Override
  public BlockRepository createBlockRepository() {
    return new BlockRepositoryVertxImpl(apiClient);
  }

  @Override
  public ReceiptRepository createReceiptRepository() {
    return new ReceiptRepositoryVertxImpl(apiClient);
  }

  @Override
  public ChainRepository createChainRepository() {
    return new ChainRepositoryVertxImpl(apiClient);
  }

  @Override
  public MosaicRepository createMosaicRepository() {
    return new MosaicRepositoryVertxImpl(apiClient);
  }

  @Override
  public NamespaceRepository createNamespaceRepository() {
    return new NamespaceRepositoryVertxImpl(apiClient);
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
  public TransactionStatusRepository createTransactionStatusRepository() {
    return new TransactionStatusRepositoryVertxImpl(apiClient);
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
  public HashLockRepository createHashLockRepository() {
    return new HashLockRepositoryVertxImpl(apiClient);
  }

  @Override
  public SecretLockRepository createSecretLockRepository() {
    return new SecretLockRepositoryVertxImpl(apiClient);
  }

  @Override
  public FinalizationRepository createFinalizationRepository() {
    return new FinalizationRepositoryVertxImpl(apiClient);
  }

  @Override
  public Listener createListener() {
    return new ListenerVertx(
        vertx.createHttpClient(), getBaseUrl(), createNamespaceRepository(), getNetworkType());
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
      // Failing quietly
    }
  }
}
