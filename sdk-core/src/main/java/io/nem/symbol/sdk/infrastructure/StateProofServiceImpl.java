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

  private static final String VERSION = "0100"; // TODO: to add version in catbuffer
  /** Repository factory used to load the merkle information */
  private final RepositoryFactory repositoryFactory;

  public StateProofServiceImpl(RepositoryFactory repositoryFactory) {
    this.repositoryFactory = repositoryFactory;
  }

  @Override
  public Observable<StateMerkleProof<MosaicId>> mosaic(MosaicInfo state) {
    MosaicId id = state.getMosaicId();
    return this.repositoryFactory
        .createMosaicRepository()
        .getMosaicMerkle(id)
        .map(merkle -> toStateMerkleProof(id, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<String>> mosaicRestriction(MosaicRestriction<?> state) {
    String id = state.getCompositeHash();
    return this.repositoryFactory
        .createRestrictionMosaicRepository()
        .getMosaicRestrictionsMerkle(id)
        .map(merkle -> toStateMerkleProof(id, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<String>> hashLock(HashLockInfo state) {
    String id = state.getHash();
    return this.repositoryFactory
        .createHashLockRepository()
        .getHashLockMerkle(id)
        .map(merkle -> toStateMerkleProof(id, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<String>> secretLock(SecretLockInfo state) {
    String id = state.getCompositeHash();
    return this.repositoryFactory
        .createSecretLockRepository()
        .getSecretLockMerkle(id)
        .map(merkle -> toStateMerkleProof(id, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<String>> metadata(Metadata state) {
    String id = state.getCompositeHash();
    return this.repositoryFactory
        .createMetadataRepository()
        .getMetadataMerkle(id)
        .map(merkle -> toStateMerkleProof(id, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<Address>> accountRestrictions(AccountRestrictions state) {
    Address id = state.getAddress();
    return this.repositoryFactory
        .createRestrictionAccountRepository()
        .getAccountRestrictionsMerkle(id)
        .map(merkle -> toStateMerkleProof(id, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<Address>> account(AccountInfo state) {
    Address id = state.getAddress();
    return this.repositoryFactory
        .createAccountRepository()
        .getAccountInfoMerkle(id)
        .map(merkle -> toStateMerkleProof(id, merkle, state.serialize()));
  }

  @Override
  public Observable<StateMerkleProof<NamespaceId>> namespace(NamespaceInfo state) {
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
                    .map(s -> toStateMerkleProof(id, merkle, s)));
  }

  private <ID> StateMerkleProof<ID> toStateMerkleProof(
      ID id, MerkleStateInfo merkle, byte[] serialized) {
    if (merkle.getRaw().isEmpty()) {
      throw new IllegalStateException("Merkle tree is empty!");
    }
    String hex = VERSION + ConvertUtils.toHex(serialized);
    String stateHash = ConvertUtils.toHex(Hashes.sha3_256(ConvertUtils.fromHexToBytes(hex)));
    return new StateMerkleProof<>(id, stateHash, merkle.getTree(), merkle.getRaw());
  }
}
