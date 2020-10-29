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

import io.nem.symbol.sdk.model.finalization.FinalizationProof;
import io.reactivex.Observable;
import java.math.BigInteger;

/** Repository for finalization objects */
public interface FinalizationRepository {
  /**
   * Gets finalization proof for the greatest height associated with the given epoch.
   *
   * @param epoch the epoch
   * @return the observable of FinalizationProof.
   */
  Observable<FinalizationProof> getFinalizationProofAtEpoch(long epoch);

  /**
   * Gets finalization proof at the given height.
   *
   * @param height Block height
   * @return the observable of FinalizationProof.
   */
  Observable<FinalizationProof> getFinalizationProofAtHeight(BigInteger height);
}
