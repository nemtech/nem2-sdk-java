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

import io.nem.symbol.sdk.api.ChainRepository;
import io.nem.symbol.sdk.model.blockchain.ChainInfo;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.ChainRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ChainInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.FinalizedBlockDTO;
import io.reactivex.Observable;
import java.math.BigInteger;

/** Chain http repository. */
public class ChainRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl
    implements ChainRepository {

  private final ChainRoutesApi client;

  public ChainRepositoryOkHttpImpl(ApiClient apiClient) {
    super(apiClient);
    client = new ChainRoutesApi(apiClient);
  }

  public ChainRoutesApi getClient() {
    return client;
  }

  /**
   * Get Block chain score
   *
   * @return io.reactivex.Observable of {@link BigInteger}
   */
  public Observable<ChainInfo> getChainInfo() {
    return call(getClient()::getChainInfo, this::toChainInfo);
  }

  private ChainInfo toChainInfo(ChainInfoDTO dto) {
    return new ChainInfo(
        dto.getHeight(),
        dto.getScoreLow(),
        dto.getScoreHigh(),
        toFinalizedBlock(dto.getLatestFinalizedBlock()));
  }

  public static FinalizedBlock toFinalizedBlock(FinalizedBlockDTO dto) {
    return new FinalizedBlock(
        dto.getFinalizationEpoch(), dto.getFinalizationPoint(), dto.getHeight(), dto.getHash());
  }
}
