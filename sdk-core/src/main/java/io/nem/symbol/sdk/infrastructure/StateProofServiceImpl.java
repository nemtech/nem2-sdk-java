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

import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.NamespacePaginationStreamer;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NamespaceSearchCriteria;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.StateProofService;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountRestrictions;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.state.StateMerkleProof;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.reactivex.Observable;

/** Service used for state proofing */
public class StateProofServiceImpl implements StateProofService {

  /** Repository factory used to load the merkle information */
  private final RepositoryFactory repositoryFactory;

  public StateProofServiceImpl(RepositoryFactory repositoryFactory) {
    this.repositoryFactory = repositoryFactory;
  }

  @Override
  public Observable<StateMerkleProof<MosaicInfo>> mosaic(MosaicId mosaicId) {
    return repositoryFactory.createMosaicRepository().getMosaic(mosaicId).flatMap(this::mosaic);
  }

  @Override
  public Observable<StateMerkleProof<MosaicInfo>> mosaic(MosaicInfo state) {
    MosaicId id = state.getMosaicId();
    return this.repositoryFactory
        .createMosaicRepository()
        .getMosaicMerkle(id)
        .map(merkle -> toStateMerkleProof(state, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<MosaicRestriction<?>>> mosaicRestriction(
      String compositeHash) {
    return repositoryFactory
        .createRestrictionMosaicRepository()
        .getMosaicRestrictions(compositeHash)
        .flatMap(this::mosaicRestriction);
  }

  @Override
  public Observable<StateMerkleProof<MosaicRestriction<?>>> mosaicRestriction(
      MosaicRestriction<?> state) {
    String id = state.getCompositeHash();
    return this.repositoryFactory
        .createRestrictionMosaicRepository()
        .getMosaicRestrictionsMerkle(id)
        .map(merkle -> toStateMerkleProof(state, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<HashLockInfo>> hashLock(String hash) {
    return repositoryFactory.createHashLockRepository().getHashLock(hash).flatMap(this::hashLock);
  }

  @Override
  public Observable<StateMerkleProof<HashLockInfo>> hashLock(HashLockInfo state) {
    String id = state.getHash();
    return this.repositoryFactory
        .createHashLockRepository()
        .getHashLockMerkle(id)
        .map(merkle -> toStateMerkleProof(state, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<SecretLockInfo>> secretLock(String compositeHash) {
    return repositoryFactory
        .createSecretLockRepository()
        .getSecretLock(compositeHash)
        .flatMap(this::secretLock);
  }

  @Override
  public Observable<StateMerkleProof<SecretLockInfo>> secretLock(SecretLockInfo state) {
    String id = state.getCompositeHash();
    return this.repositoryFactory
        .createSecretLockRepository()
        .getSecretLockMerkle(id)
        .map(merkle -> toStateMerkleProof(state, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<Metadata>> metadata(String compositeHash) {
    return repositoryFactory
        .createMetadataRepository()
        .getMetadata(compositeHash)
        .flatMap(this::metadata);
  }

  @Override
  public Observable<StateMerkleProof<Metadata>> metadata(Metadata state) {
    String id = state.getCompositeHash();
    return this.repositoryFactory
        .createMetadataRepository()
        .getMetadataMerkle(id)
        .map(merkle -> toStateMerkleProof(state, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<AccountRestrictions>> accountRestrictions(Address address) {
    return repositoryFactory
        .createRestrictionAccountRepository()
        .getAccountRestrictions(address)
        .flatMap(this::accountRestrictions);
  }

  @Override
  public Observable<StateMerkleProof<AccountRestrictions>> accountRestrictions(
      AccountRestrictions state) {
    Address id = state.getAddress();
    return this.repositoryFactory
        .createRestrictionAccountRepository()
        .getAccountRestrictionsMerkle(id)
        .map(merkle -> toStateMerkleProof(state, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<AccountInfo>> account(Address address) {
    return repositoryFactory
        .createAccountRepository()
        .getAccountInfo(address)
        .flatMap(this::account);
  }

  @Override
  public Observable<StateMerkleProof<AccountInfo>> account(AccountInfo state) {
    Address id = state.getAddress();
    return this.repositoryFactory
        .createAccountRepository()
        .getAccountInfoMerkle(id)
        .map(merkle -> toStateMerkleProof(state, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<MultisigAccountInfo>> multisig(Address address) {
    return repositoryFactory
        .createMultisigRepository()
        .getMultisigAccountInfo(address)
        .flatMap(this::multisig);
  }

  @Override
  public Observable<StateMerkleProof<MultisigAccountInfo>> multisig(MultisigAccountInfo state) {
    Address id = state.getAccountAddress();
    return this.repositoryFactory
        .createMultisigRepository()
        .getMultisigAccountInfoMerkle(id)
        .map(merkle -> toStateMerkleProof(state, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<NamespaceInfo>> namespace(NamespaceId namespaceId) {
    return repositoryFactory
        .createNamespaceRepository()
        .getNamespace(namespaceId)
        .flatMap(this::namespace);
  }

  @Override
  public Observable<StateMerkleProof<NamespaceInfo>> namespace(NamespaceInfo state) {
    NamespaceId id = state.getId();
    NamespaceRepository namespaceRepository = this.repositoryFactory.createNamespaceRepository();
    NamespacePaginationStreamer streamer = new NamespacePaginationStreamer(namespaceRepository);
    return namespaceRepository
        .getNamespaceMerkle(id)
        .flatMap(
            merkle ->
                streamer
                    .search(
                        new NamespaceSearchCriteria()
                            .level0(state.getId().getIdAsHex())
                            .registrationType(NamespaceRegistrationType.SUB_NAMESPACE))
                    .toList()
                    .toObservable()
                    .map(state::serialize)
                    .map(s -> toStateMerkleProof(state, merkle, s)));
  }

  private <S> StateMerkleProof<S> toStateMerkleProof(
      S state, MerkleStateInfo merkle, byte[] serialized) {
    if (merkle.getRaw().isEmpty()) {
      throw new IllegalStateException("Merkle tree is empty!");
    }
    String stateHash = ConvertUtils.toHex(Hashes.sha3_256(serialized));
    return new StateMerkleProof<>(state, stateHash, merkle.getTree(), merkle.getRaw());
  }
}
