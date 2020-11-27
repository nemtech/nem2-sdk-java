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

public interface StateProofService {

  Observable<StateMerkleProof<MosaicId>> mosaic(MosaicInfo state);

  Observable<StateMerkleProof<String>> mosaicRestriction(MosaicRestriction<?> state);

  Observable<StateMerkleProof<String>> hashLock(HashLockInfo state);

  Observable<StateMerkleProof<String>> secretLock(SecretLockInfo state);

  Observable<StateMerkleProof<String>> metadata(Metadata state);

  Observable<StateMerkleProof<Address>> accountRestrictions(AccountRestrictions state);

  Observable<StateMerkleProof<Address>> account(AccountInfo state);

  Observable<StateMerkleProof<Address>> multisig(MultisigAccountInfo state);

  Observable<StateMerkleProof<NamespaceId>> namespace(NamespaceInfo state);
}
