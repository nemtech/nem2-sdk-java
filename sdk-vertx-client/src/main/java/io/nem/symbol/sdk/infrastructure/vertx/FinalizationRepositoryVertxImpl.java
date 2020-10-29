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

import io.nem.symbol.sdk.api.FinalizationRepository;
import io.nem.symbol.sdk.model.finalization.BmTreeSignature;
import io.nem.symbol.sdk.model.finalization.FinalizationProof;
import io.nem.symbol.sdk.model.finalization.FinalizationStage;
import io.nem.symbol.sdk.model.finalization.MessageGroup;
import io.nem.symbol.sdk.model.finalization.ParentPublicKeySignaturePair;
import io.nem.symbol.sdk.openapi.vertx.api.FinalizationRoutesApi;
import io.nem.symbol.sdk.openapi.vertx.api.FinalizationRoutesApiImpl;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.model.FinalizationProofDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class FinalizationRepositoryVertxImpl extends AbstractRepositoryVertxImpl
    implements FinalizationRepository {

  private final FinalizationRoutesApi client;

  public FinalizationRepositoryVertxImpl(ApiClient apiClient) {
    super(apiClient);
    this.client = new FinalizationRoutesApiImpl(apiClient);
  }

  @Override
  public Observable<FinalizationProof> getFinalizationProofAtEpoch(long epoch) {
    return this.call(
        (h) -> getClient().getFinalizationProofAtEpoch(epoch, h), this::toFinalizationProof);
  }

  @Override
  public Observable<FinalizationProof> getFinalizationProofAtHeight(BigInteger height) {

    return this.call(
        (h) -> getClient().getFinalizationProofAtHeight(height, h), this::toFinalizationProof);
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

  private MessageGroup toMessageGroup(io.nem.symbol.sdk.openapi.vertx.model.MessageGroup dto) {
    return new MessageGroup(
        FinalizationStage.rawValueOf(dto.getStage().getValue()),
        dto.getHeight(),
        dto.getHashes(),
        dto.getSignatures().stream().map(this::toBmTreeSignature).collect(Collectors.toList()));
  }

  private BmTreeSignature toBmTreeSignature(
      io.nem.symbol.sdk.openapi.vertx.model.BmTreeSignature dto) {
    return new BmTreeSignature(
        toParentPublicKeySignaturePair(dto.getRoot()),
        toParentPublicKeySignaturePair(dto.getTop()),
        toParentPublicKeySignaturePair(dto.getBottom()));
  }

  private ParentPublicKeySignaturePair toParentPublicKeySignaturePair(
      io.nem.symbol.sdk.openapi.vertx.model.ParentPublicKeySignaturePair dto) {
    return new ParentPublicKeySignaturePair(dto.getParentPublicKey(), dto.getSignature());
  }

  public FinalizationRoutesApi getClient() {
    return client;
  }
}
