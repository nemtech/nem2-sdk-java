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
import io.nem.symbol.sdk.api.OrderBy;
import io.nem.symbol.sdk.api.PaginationStreamer;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RestrictionAccountRepository;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.api.SearchCriteria;
import io.nem.symbol.sdk.api.SecretLockPaginationStreamer;
import io.nem.symbol.sdk.api.SecretLockRepository;
import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountRestrictions;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.state.StateMerkleProof;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.reactivex.Observable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(Lifecycle.PER_CLASS)
public class MerkleLoaderIntegrationTest extends BaseIntegrationTest {

  public static final int TAKE_COUNT = 10;
  public static final OrderBy ORDER_BY = OrderBy.DESC;

  //  public static void main(String[] args) {
  //    RepositoryFactory repositoryFactory =
  //        new
  // RepositoryFactoryOkHttpImpl("http://api-01.us-west-2.0.10.0.x.symboldev.network:3000");
  //    List<Map<String, String>> merkles = MerkleLoader.getMerkles(repositoryFactory);
  //    System.out.println(new JsonHelperJackson2().prettyPrint(merkles));
  //  }
  public List<Arguments> mosaicRestriction() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    RestrictionMosaicRepository repository = repositoryFactory.createRestrictionMosaicRepository();
    MosaicRestrictionPaginationStreamer streamer =
        new MosaicRestrictionPaginationStreamer(repository);
    return getArguments(streamer, new MosaicRestrictionSearchCriteria());
  }

  @ParameterizedTest
  @MethodSource("mosaicRestriction")
  void mosaicRestrictionMerkles(MosaicRestriction<?> state, RepositoryType repositoryType) {
    StateProofServiceImpl service = new StateProofServiceImpl(getRepositoryFactory(repositoryType));
    StateMerkleProof<MosaicRestriction<?>> proof = get(service.mosaicRestriction(state));
    Assertions.assertTrue(proof.isValid(), "Invalid proof " + proof.getState().getCompositeHash());
  }

  public List<Arguments> hashLocks() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    HashLockRepository repository = repositoryFactory.createHashLockRepository();
    HashLockPaginationStreamer streamer = new HashLockPaginationStreamer(repository);
    return getArguments(streamer, new HashLockSearchCriteria().order(OrderBy.DESC));
  }

  @ParameterizedTest
  @MethodSource("hashLocks")
  void hashLocksMerkles(HashLockInfo state, RepositoryType repositoryType) {
    StateProofServiceImpl service = new StateProofServiceImpl(getRepositoryFactory(repositoryType));
    StateMerkleProof<HashLockInfo> proof = get(service.hashLock(state));
    Assertions.assertTrue(proof.isValid(), "Invalid proof " + proof.getState().getHash());
  }

  public List<Arguments> accountRestrictions() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    RestrictionAccountRepository repository =
        repositoryFactory.createRestrictionAccountRepository();
    AccountRestrictionsPaginationStreamer streamer =
        new AccountRestrictionsPaginationStreamer(repository);
    return getArguments(streamer, new AccountRestrictionSearchCriteria().order(OrderBy.DESC));
  }

  @ParameterizedTest
  @MethodSource("accountRestrictions")
  void accountRestrictionsMerkles(AccountRestrictions state, RepositoryType repositoryType) {
    StateProofServiceImpl service = new StateProofServiceImpl(getRepositoryFactory(repositoryType));
    StateMerkleProof<AccountRestrictions> proof = get(service.accountRestrictions(state));
    Assertions.assertTrue(
        proof.isValid(), "Invalid proof " + proof.getState().getAddress().plain());
  }

  @Test
  void multisigMerkles() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    MultisigAccountInfo state =
        get(
            repositoryFactory
                .createMultisigRepository()
                .getMultisigAccountInfo(
                    Address.createFromRawAddress("TCFAEINOWAAPSGT2OCBCZYMH2Q3PGHQPEYTIUKI")));
    StateProofServiceImpl service = new StateProofServiceImpl(repositoryFactory);
    StateMerkleProof<MultisigAccountInfo> proof = get(service.multisig(state));
    Assertions.assertTrue(
        proof.isValid(), "Invalid proof " + proof.getState().getAccountAddress().plain());
  }

  public List<Arguments> secretLocks() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    SecretLockRepository repository = repositoryFactory.createSecretLockRepository();
    SecretLockPaginationStreamer streamer = new SecretLockPaginationStreamer(repository);
    return getArguments(streamer, new SecretLockSearchCriteria().order(ORDER_BY));
  }

  @ParameterizedTest
  @MethodSource("secretLocks")
  void secretLocksMerkles(SecretLockInfo state, RepositoryType repositoryType) {
    StateProofServiceImpl service = new StateProofServiceImpl(getRepositoryFactory(repositoryType));
    StateMerkleProof<SecretLockInfo> proof = get(service.secretLock(state));
    Assertions.assertTrue(proof.isValid(), "Invalid proof " + proof.getState().getCompositeHash());
  }

  public List<Arguments> accounts() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    AccountRepository repository = repositoryFactory.createAccountRepository();
    AccountPaginationStreamer streamer = new AccountPaginationStreamer(repository);
    return getArguments(streamer, new AccountSearchCriteria().order(ORDER_BY));
  }

  @ParameterizedTest
  @MethodSource("accounts")
  void accountsMerkles(AccountInfo state, RepositoryType repositoryType) {
    StateProofServiceImpl service = new StateProofServiceImpl(getRepositoryFactory(repositoryType));
    StateMerkleProof<AccountInfo> proof = get(service.account(state));
    Assertions.assertTrue(
        proof.isValid(), "Invalid proof " + proof.getState().getAddress().plain());
  }

  public List<Arguments> mosaics() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    MosaicRepository repository = repositoryFactory.createMosaicRepository();
    MosaicPaginationStreamer streamer = new MosaicPaginationStreamer(repository);
    return getArguments(streamer, new MosaicSearchCriteria().order(ORDER_BY));
  }

  @ParameterizedTest
  @MethodSource("mosaics")
  void mosaicsMerkles(MosaicInfo state, RepositoryType repositoryType) {
    StateProofServiceImpl service = new StateProofServiceImpl(getRepositoryFactory(repositoryType));
    StateMerkleProof<MosaicInfo> proof = get(service.mosaic(state));
    Assertions.assertTrue(
        proof.isValid(), "Invalid proof " + proof.getState().getMosaicId().getIdAsHex());
  }

  public List<Arguments> namespaces() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    NamespaceRepository repository = repositoryFactory.createNamespaceRepository();
    NamespacePaginationStreamer streamer = new NamespacePaginationStreamer(repository);
    return getArguments(
        streamer,
        new NamespaceSearchCriteria()
            .order(ORDER_BY)
            .registrationType(NamespaceRegistrationType.ROOT_NAMESPACE));
  }

  @ParameterizedTest
  @MethodSource("namespaces")
  void namespacesMerkles(NamespaceInfo state, RepositoryType repositoryType) {
    RepositoryFactory repositoryFactory = getRepositoryFactory(repositoryType);
    StateProofServiceImpl service = new StateProofServiceImpl(repositoryFactory);
    StateMerkleProof<NamespaceInfo> proof = get(service.namespace(state));
    Assertions.assertTrue(
        proof.isValid(), "Invalid proof " + proof.getState().getId().getIdAsHex());
  }

  public List<Arguments> metadatas() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    MetadataRepository repository = repositoryFactory.createMetadataRepository();
    MetadataPaginationStreamer streamer = new MetadataPaginationStreamer(repository);
    return getArguments(streamer, new MetadataSearchCriteria().order(ORDER_BY));
  }

  @ParameterizedTest
  @MethodSource("metadatas")
  void metadatasMerkles(Metadata state, RepositoryType repositoryType) {
    StateProofServiceImpl service = new StateProofServiceImpl(getRepositoryFactory(repositoryType));
    StateMerkleProof<Metadata> proof = get(service.metadata(state));
    Assertions.assertTrue(proof.isValid(), "Invalid proof " + proof.getState().getCompositeHash());
  }

  private <E, C extends SearchCriteria<C>> List<Arguments> getArguments(
      PaginationStreamer<E, C> streamer, C criteria) {
    return get(
        streamer
            .search(criteria)
            .take(TAKE_COUNT)
            .flatMap(
                state ->
                    Observable.fromIterable(
                        Arrays.stream(RepositoryType.values())
                            .map(r -> Arguments.of(state, r))
                            .collect(Collectors.toList())))
            .toList()
            .toObservable());
  }
}
