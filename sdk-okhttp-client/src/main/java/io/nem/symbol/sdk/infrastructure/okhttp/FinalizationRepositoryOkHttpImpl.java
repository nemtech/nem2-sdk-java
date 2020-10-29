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
package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.sdk.api.FinalizationRepository;
import io.nem.symbol.sdk.model.finalization.BmTreeSignature;
import io.nem.symbol.sdk.model.finalization.FinalizationProof;
import io.nem.symbol.sdk.model.finalization.FinalizationStage;
import io.nem.symbol.sdk.model.finalization.MessageGroup;
import io.nem.symbol.sdk.model.finalization.ParentPublicKeySignaturePair;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.FinalizationRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.FinalizationProofDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Implements {@link FinalizationRepository}
 *
 * @author Fernando Boucquez
 */
public class FinalizationRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements FinalizationRepository {

  private final FinalizationRoutesApi client;

  public FinalizationRepositoryOkHttpImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new FinalizationRoutesApi(apiClient);
  }

  public FinalizationRoutesApi getClient() {
    return client;
  }

  @Override
  public Observable<FinalizationProof> getFinalizationProofAtEpoch(long epoch) {
    Callable<FinalizationProofDTO> callback = () -> getClient().getFinalizationProofAtEpoch(epoch);
    return this.call(callback, this::toFinalizationProof);
  }

  @Override
  public Observable<FinalizationProof> getFinalizationProofAtHeight(BigInteger height) {
    Callable<FinalizationProofDTO> callback =
        () -> getClient().getFinalizationProofAtHeight(height);
    return this.call(callback, this::toFinalizationProof);
  }

  private FinalizationProof toFinalizationProof(FinalizationProofDTO dto) {
    List<MessageGroup> messageGroups =
        dto.getMessageGroups().stream().map(this::toMessageGroup).collect(Collectors.toList());
    return new FinalizationProof(
        dto.getVersion(),
        dto.getFinalizationEpoch(),
        dto.getFinalizationPoint(),
        dto.getHeight(),
        dto.getHash(),
        messageGroups);
  }

  private MessageGroup toMessageGroup(
      io.nem.symbol.sdk.openapi.okhttp_gson.model.MessageGroup dto) {
    return new MessageGroup(
        FinalizationStage.rawValueOf(dto.getStage().getValue()),
        dto.getHeight(),
        dto.getHashes(),
        dto.getSignatures().stream().map(this::toBmTreeSignature).collect(Collectors.toList()));
  }

  private BmTreeSignature toBmTreeSignature(
      io.nem.symbol.sdk.openapi.okhttp_gson.model.BmTreeSignature dto) {
    return new BmTreeSignature(
        toParentPublicKeySignaturePair(dto.getRoot()),
        toParentPublicKeySignaturePair(dto.getTop()),
        toParentPublicKeySignaturePair(dto.getBottom()));
  }

  private ParentPublicKeySignaturePair toParentPublicKeySignaturePair(
      io.nem.symbol.sdk.openapi.okhttp_gson.model.ParentPublicKeySignaturePair dto) {
    return new ParentPublicKeySignaturePair(dto.getParentPublicKey(), dto.getSignature());
  }
}
