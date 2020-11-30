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
package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountRestrictions;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.state.StateMerkleProof;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.reactivex.Observable;

/** Service used to validate if states have valid merkle proofs. */
public interface StateProofService {

  /**
   * It returns the StateMerkleProof of a mosaic.
   *
   * @param mosaicId the mosaic id of the state to be loaded
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<MosaicInfo>> mosaic(MosaicId mosaicId);

  /**
   * It returns the StateMerkleProof of a mosaic.
   *
   * @param state the mosaic state
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<MosaicInfo>> mosaic(MosaicInfo state);

  /**
   * It returns the StateMerkleProof of a mosaic restriction.
   *
   * @param compositeHash the restriction id of the state to be loaded
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<MosaicRestriction<?>>> mosaicRestriction(String compositeHash);

  /**
   * It returns the StateMerkleProof of a mosaic restriction.
   *
   * @param state the mosaic restriction state
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<MosaicRestriction<?>>> mosaicRestriction(MosaicRestriction<?> state);

  /**
   * It returns the StateMerkleProof of a hash lock.
   *
   * @param hash the hash lock id of the state to be loaded
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<HashLockInfo>> hashLock(String hash);

  /**
   * It returns the StateMerkleProof of a hash lock.
   *
   * @param state the hash lock state.
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<HashLockInfo>> hashLock(HashLockInfo state);

  /**
   * It returns the StateMerkleProof of a secret lock.
   *
   * @param compositeHash the secret lock id of the state to be loaded
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<SecretLockInfo>> secretLock(String compositeHash);

  /**
   * It returns the StateMerkleProof of a secret lock.
   *
   * @param state the secret lock state
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<SecretLockInfo>> secretLock(SecretLockInfo state);

  /**
   * It returns the StateMerkleProof of a metadata.
   *
   * @param compositeHash the metadata id of the state to be loaded
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<Metadata>> metadata(String compositeHash);

  /**
   * It returns the StateMerkleProof of a metadata.
   *
   * @param state the metadata state
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<Metadata>> metadata(Metadata state);

  /**
   * It returns the StateMerkleProof of an account restriction.
   *
   * @param address the account restriction address.
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<AccountRestrictions>> accountRestrictions(Address address);

  /**
   * It returns the StateMerkleProof of an account restriction.
   *
   * @param state the account restriction state.
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<AccountRestrictions>> accountRestrictions(AccountRestrictions state);

  /**
   * It returns the StateMerkleProof of an account.
   *
   * @param address the account address.
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<AccountInfo>> account(Address address);

  /**
   * It returns the StateMerkleProof of an account.
   *
   * @param state the account state.
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<AccountInfo>> account(AccountInfo state);

  /**
   * It returns the StateMerkleProof of a multisig account.
   *
   * @param address the multisig account address.
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<MultisigAccountInfo>> multisig(Address address);

  /**
   * It returns the StateMerkleProof of a multisig account.
   *
   * @param state the multisig account state.
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<MultisigAccountInfo>> multisig(MultisigAccountInfo state);

  /**
   * It returns the StateMerkleProof of a namespace.
   *
   * @param namespaceId the namespace id.
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<NamespaceInfo>> namespace(NamespaceId namespaceId);

  /**
   * It returns the StateMerkleProof of a namespace.
   *
   * @param state the namespace state.
   * @return the {@link StateMerkleProof}
   */
  Observable<StateMerkleProof<NamespaceInfo>> namespace(NamespaceInfo state);
}
