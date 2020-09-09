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
package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import java.math.BigInteger;

/**
 * In case a mosaic has the flag 'supplyMutable' set to true, the creator of the mosaic can change
 * the supply, i.e. increase or decrease the supply.
 *
 * @since 1.0
 */
public class MosaicSupplyChangeTransaction extends Transaction {

  private final UnresolvedMosaicId mosaicId;
  private final MosaicSupplyChangeActionType action;
  private final BigInteger delta;

  MosaicSupplyChangeTransaction(MosaicSupplyChangeTransactionFactory factory) {
    super(factory);
    this.mosaicId = factory.getMosaicId();
    this.action = factory.getAction();
    this.delta = factory.getDelta();
  }

  /**
   * Returns mosaic id.
   *
   * @return BigInteger
   */
  public UnresolvedMosaicId getMosaicId() {
    return mosaicId;
  }

  /**
   * Returns mosaic supply type.
   *
   * @return {@link MosaicSupplyChangeActionType}
   */
  public MosaicSupplyChangeActionType getAction() {
    return action;
  }

  /**
   * Returns amount of mosaics added or removed.
   *
   * @return BigInteger
   */
  public BigInteger getDelta() {
    return delta;
  }
}
