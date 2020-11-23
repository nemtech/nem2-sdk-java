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

import io.nem.symbol.sdk.api.AccountPaginationStreamer;
import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.AccountRestrictionSearchCriteria;
import io.nem.symbol.sdk.api.AccountRestrictionsPaginationStreamer;
import io.nem.symbol.sdk.api.AccountSearchCriteria;
import io.nem.symbol.sdk.api.HashLockPaginationStreamer;
import io.nem.symbol.sdk.api.HashLockRepository;
import io.nem.symbol.sdk.api.HashLockSearchCriteria;
import io.nem.symbol.sdk.api.MetadataPaginationStreamer;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.MosaicPaginationStreamer;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.MosaicRestrictionPaginationStreamer;
import io.nem.symbol.sdk.api.MosaicRestrictionSearchCriteria;
import io.nem.symbol.sdk.api.MosaicSearchCriteria;
import io.nem.symbol.sdk.api.NamespacePaginationStreamer;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NamespaceSearchCriteria;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RestrictionAccountRepository;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.api.SecretLockPaginationStreamer;
import io.nem.symbol.sdk.api.SecretLockRepository;
import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.infrastructure.okhttp.RepositoryFactoryOkHttpImpl;
import io.nem.symbol.sdk.infrastructure.vertx.JsonHelperJackson2;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class MerkleLoaderIntegrationTest extends BaseIntegrationTest {

//  public static void main(String[] args) {
//    RepositoryFactory repositoryFactory =
//        new RepositoryFactoryOkHttpImpl("http://api-01.us-west-2.0.10.0.x.symboldev.network:3000");
//    List<Map<String, String>> merkles = MerkleLoader.getMerkles(repositoryFactory);
//    System.out.println(new JsonHelperJackson2().prettyPrint(merkles));
//  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMerkles(RepositoryType type) {
    List<Map<String, String>> merkles = getMerkles(getRepositoryFactory(type));
    Assertions.assertFalse(merkles.isEmpty());
  }

  public static List<Map<String, String>> getMerkles(RepositoryFactory repositoryFactory) {
    List<Map<String, String>> list = new ArrayList<>();

    int takeCount = 4;
    {
      MosaicRepository repository = repositoryFactory.createMosaicRepository();
      MosaicPaginationStreamer streamer = new MosaicPaginationStreamer(repository);

      streamer
          .search(new MosaicSearchCriteria())
          .flatMap(t -> repository.getMosaicMerkle(t.getMosaicId()).map(m -> new Pair<>(t, m)))
          .take(takeCount)
          .subscribe(
              pair -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("mosaicId", pair.getFirst().getMosaicId().getIdAsHex());
                map.put("raw", pair.getSecond().getRaw());
                list.add(map);
              });
    }

    {
      NamespaceRepository repository = repositoryFactory.createNamespaceRepository();
      NamespacePaginationStreamer streamer = new NamespacePaginationStreamer(repository);

      streamer
          .search(new NamespaceSearchCriteria())
          .flatMap(t -> repository.getNamespaceMerkle(t.getId()).map(m -> new Pair<>(t, m)))
          .take(takeCount)
          .subscribe(
              pair -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("namespaceId", pair.getFirst().getId().getIdAsHex());
                map.put("raw", pair.getSecond().getRaw());
                list.add(map);
              });
    }

    {
      AccountRepository repository = repositoryFactory.createAccountRepository();
      AccountPaginationStreamer streamer = new AccountPaginationStreamer(repository);

      streamer
          .search(new AccountSearchCriteria())
          .flatMap(t -> repository.getAccountInfoMerkle(t.getAddress()).map(m -> new Pair<>(t, m)))
          .take(takeCount)
          .subscribe(
              pair -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("address", pair.getFirst().getAddress().encoded());
                map.put("raw", pair.getSecond().getRaw());
                list.add(map);
              });
    }
    {
      HashLockRepository repository = repositoryFactory.createHashLockRepository();
      HashLockPaginationStreamer streamer = new HashLockPaginationStreamer(repository);

      streamer
          .search(new HashLockSearchCriteria())
          .flatMap(t -> repository.getHashLockMerkle(t.getHash()).map(m -> new Pair<>(t, m)))
          .take(takeCount)
          .subscribe(
              pair -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("hashLockHash", pair.getFirst().getHash());
                map.put("raw", pair.getSecond().getRaw());
                list.add(map);
              });
    }

    {
      SecretLockRepository repository = repositoryFactory.createSecretLockRepository();
      SecretLockPaginationStreamer streamer = new SecretLockPaginationStreamer(repository);

      streamer
          .search(new SecretLockSearchCriteria())
          .flatMap(
              t -> repository.getSecretLockMerkle(t.getCompositeHash()).map(m -> new Pair<>(t, m)))
          .take(takeCount)
          .subscribe(
              pair -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("secretLockCompositeHash", pair.getFirst().getCompositeHash());
                map.put("raw", pair.getSecond().getRaw());
                list.add(map);
              });
    }

    {
      MetadataRepository repository = repositoryFactory.createMetadataRepository();
      MetadataPaginationStreamer streamer = new MetadataPaginationStreamer(repository);

      streamer
          .search(new MetadataSearchCriteria())
          .flatMap(
              t -> repository.getMetadataMerkle(t.getCompositeHash()).map(m -> new Pair<>(t, m)))
          .take(takeCount)
          .subscribe(
              pair -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("metadataCompositeHash", pair.getFirst().getCompositeHash());
                map.put("raw", pair.getSecond().getRaw());
                list.add(map);
              });
    }

    {
      RestrictionAccountRepository repository =
          repositoryFactory.createRestrictionAccountRepository();
      AccountRestrictionsPaginationStreamer streamer =
          new AccountRestrictionsPaginationStreamer(repository);

      streamer
          .search(new AccountRestrictionSearchCriteria())
          .flatMap(
              t ->
                  repository
                      .getAccountRestrictionsMerkle(t.getAddress())
                      .map(m -> new Pair<>(t, m)))
          .take(takeCount)
          .subscribe(
              pair -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("accountRestrictionsAddress", pair.getFirst().getAddress().encoded());
                map.put("raw", pair.getSecond().getRaw());
                list.add(map);
              });
    }

    {
      RestrictionMosaicRepository repository =
          repositoryFactory.createRestrictionMosaicRepository();
      MosaicRestrictionPaginationStreamer streamer =
          new MosaicRestrictionPaginationStreamer(repository);

      streamer
          .search(new MosaicRestrictionSearchCriteria())
          .flatMap(
              t ->
                  repository
                      .getMosaicRestrictionsMerkle(t.getCompositeHash())
                      .map(m -> new Pair<>(t, m)))
          .take(takeCount)
          .subscribe(
              pair -> {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("mosaicRestrictionsCompositeHash", pair.getFirst().getCompositeHash());
                map.put("raw", pair.getSecond().getRaw());
                list.add(map);
              });
    }

    return list;
  }
}
