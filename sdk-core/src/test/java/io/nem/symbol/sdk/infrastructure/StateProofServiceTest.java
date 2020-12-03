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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.HashLockRepository;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.PaginationStreamer;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RestrictionAccountRepository;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.api.SecretLockRepository;
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
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.state.MerkleTree;
import io.nem.symbol.sdk.model.state.MerkleTreeLeaf;
import io.nem.symbol.sdk.model.state.MerkleTreeNodeType;
import io.nem.symbol.sdk.model.state.StateMerkleProof;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Unit tests for StateProofService. */
public class StateProofServiceTest {

  private final String serialized = "ABCD";
  private StateProofService service;
  private RepositoryFactory factory;
  private final MerkleStateInfo tree =
      new MerkleStateInfo(
          "1234",
          new MerkleTree(
              Collections.emptyList(),
              new MerkleTreeLeaf(
                  MerkleTreeNodeType.LEAF,
                  "",
                  "",
                  ConvertUtils.toHex(Hashes.sha3_256(ConvertUtils.fromHexToBytes(serialized))),
                  "")));

  @BeforeEach
  void setup() {
    factory = mock(RepositoryFactory.class);
    service = new StateProofServiceImpl(factory);
  }

  @Test
  void mosaic() throws Exception {
    MosaicRepository repository = mock(MosaicRepository.class);
    when(factory.createMosaicRepository()).thenReturn(repository);
    MosaicId id = new MosaicId(BigInteger.ONE);
    MosaicInfo state = Mockito.mock(MosaicInfo.class);
    when(state.getMosaicId()).thenReturn(id);
    when(state.serialize()).thenReturn(ConvertUtils.fromHexToBytes(serialized));
    when(repository.getMosaic(eq(id))).thenReturn(Observable.just(state));
    when(repository.getMosaicMerkle(eq(id))).thenReturn(Observable.just(tree));
    StateMerkleProof<MosaicInfo> proof = service.mosaic(id).toFuture().get();
    Assertions.assertTrue(proof.isValid());
    Assertions.assertEquals(state, proof.getState());
  }

  @Test
  void namespace() throws Exception {
    NamespaceRepository repository = mock(NamespaceRepository.class);
    when(factory.createNamespaceRepository()).thenReturn(repository);
    NamespaceId id = NamespaceId.createFromId(BigInteger.ONE);
    NamespaceInfo state = Mockito.mock(NamespaceInfo.class);
    when(state.getId()).thenReturn(id);
    when(state.serialize(any())).thenReturn(ConvertUtils.fromHexToBytes(serialized));
    when(repository.streamer()).thenReturn(new PaginationStreamer<>(repository));
    when(repository.search(any())).thenReturn(Observable.just(new Page<>(new ArrayList<>())));
    when(repository.getNamespace(eq(id))).thenReturn(Observable.just(state));
    when(repository.getNamespaceMerkle(eq(id))).thenReturn(Observable.just(tree));
    StateMerkleProof<NamespaceInfo> proof = service.namespace(id).toFuture().get();
    Assertions.assertTrue(proof.isValid());
    Assertions.assertEquals(state, proof.getState());
  }

  @Test
  void metadata() throws Exception {
    MetadataRepository repository = mock(MetadataRepository.class);
    when(factory.createMetadataRepository()).thenReturn(repository);
    String id = "hash";
    Metadata state = Mockito.mock(Metadata.class);
    when(state.getCompositeHash()).thenReturn(id);
    when(state.serialize()).thenReturn(ConvertUtils.fromHexToBytes(serialized));
    when(repository.getMetadata(eq(id))).thenReturn(Observable.just(state));
    when(repository.getMetadataMerkle(eq(id))).thenReturn(Observable.just(tree));
    StateMerkleProof<Metadata> proof = service.metadata(id).toFuture().get();
    Assertions.assertTrue(proof.isValid());
    Assertions.assertEquals(state, proof.getState());
  }

  @Test
  void hashLock() throws Exception {
    HashLockRepository repository = mock(HashLockRepository.class);
    when(factory.createHashLockRepository()).thenReturn(repository);
    String id = "hash";
    HashLockInfo state = Mockito.mock(HashLockInfo.class);
    when(state.getHash()).thenReturn(id);
    when(state.serialize()).thenReturn(ConvertUtils.fromHexToBytes(serialized));
    when(repository.getHashLock(eq(id))).thenReturn(Observable.just(state));
    when(repository.getHashLockMerkle(eq(id))).thenReturn(Observable.just(tree));
    StateMerkleProof<HashLockInfo> proof = service.hashLock(id).toFuture().get();
    Assertions.assertTrue(proof.isValid());
    Assertions.assertEquals(state, proof.getState());
  }

  @Test
  void secretLock() throws Exception {
    SecretLockRepository repository = mock(SecretLockRepository.class);
    when(factory.createSecretLockRepository()).thenReturn(repository);
    String id = "secret";
    SecretLockInfo state = Mockito.mock(SecretLockInfo.class);
    when(state.getCompositeHash()).thenReturn(id);
    when(state.serialize()).thenReturn(ConvertUtils.fromHexToBytes(serialized));
    when(repository.getSecretLock(eq(id))).thenReturn(Observable.just(state));
    when(repository.getSecretLockMerkle(eq(id))).thenReturn(Observable.just(tree));
    StateMerkleProof<SecretLockInfo> proof = service.secretLock(id).toFuture().get();
    Assertions.assertTrue(proof.isValid());
    Assertions.assertEquals(state, proof.getState());
  }

  @Test
  void mosaicRestriction() throws Exception {
    RestrictionMosaicRepository repository = mock(RestrictionMosaicRepository.class);
    when(factory.createRestrictionMosaicRepository()).thenReturn(repository);
    String id = "hash";
    MosaicRestriction<?> state = Mockito.mock(MosaicRestriction.class);
    when(state.getCompositeHash()).thenReturn(id);
    when(state.serialize()).thenReturn(ConvertUtils.fromHexToBytes(serialized));
    when(repository.getMosaicRestrictions(eq(id))).thenReturn(Observable.just(state));
    when(repository.getMosaicRestrictionsMerkle(eq(id))).thenReturn(Observable.just(tree));
    StateMerkleProof<MosaicRestriction<?>> proof = service.mosaicRestriction(id).toFuture().get();
    Assertions.assertTrue(proof.isValid());
    Assertions.assertEquals(state, proof.getState());
  }

  @Test
  void account() throws Exception {
    AccountRepository repository = mock(AccountRepository.class);
    when(factory.createAccountRepository()).thenReturn(repository);
    Address id = Address.generateRandom(NetworkType.MIJIN_TEST);
    AccountInfo state = Mockito.mock(AccountInfo.class);
    when(state.getAddress()).thenReturn(id);
    when(state.serialize()).thenReturn(ConvertUtils.fromHexToBytes(serialized));
    when(repository.getAccountInfo(eq(id))).thenReturn(Observable.just(state));
    when(repository.getAccountInfoMerkle(eq(id))).thenReturn(Observable.just(tree));
    StateMerkleProof<AccountInfo> proof = service.account(id).toFuture().get();
    Assertions.assertTrue(proof.isValid());
    Assertions.assertEquals(state, proof.getState());
  }

  @Test
  void multisig() throws Exception {
    MultisigRepository repository = mock(MultisigRepository.class);
    when(factory.createMultisigRepository()).thenReturn(repository);
    Address id = Address.generateRandom(NetworkType.MIJIN_TEST);
    MultisigAccountInfo state = Mockito.mock(MultisigAccountInfo.class);
    when(state.getAccountAddress()).thenReturn(id);
    when(state.serialize()).thenReturn(ConvertUtils.fromHexToBytes(serialized));
    when(repository.getMultisigAccountInfo(eq(id))).thenReturn(Observable.just(state));
    when(repository.getMultisigAccountInfoMerkle(eq(id))).thenReturn(Observable.just(tree));
    StateMerkleProof<MultisigAccountInfo> proof = service.multisig(id).toFuture().get();
    Assertions.assertTrue(proof.isValid());
    Assertions.assertEquals(state, proof.getState());
  }

  @Test
  void accountRestrictions() throws Exception {
    RestrictionAccountRepository repository = mock(RestrictionAccountRepository.class);
    when(factory.createRestrictionAccountRepository()).thenReturn(repository);
    Address id = Address.generateRandom(NetworkType.MIJIN_TEST);
    AccountRestrictions state = Mockito.mock(AccountRestrictions.class);
    when(state.getAddress()).thenReturn(id);
    when(state.serialize()).thenReturn(ConvertUtils.fromHexToBytes(serialized));
    when(repository.getAccountRestrictions(eq(id))).thenReturn(Observable.just(state));
    when(repository.getAccountRestrictionsMerkle(eq(id))).thenReturn(Observable.just(tree));
    StateMerkleProof<AccountRestrictions> proof = service.accountRestrictions(id).toFuture().get();
    Assertions.assertTrue(proof.isValid());
    Assertions.assertEquals(state, proof.getState());
  }
}
