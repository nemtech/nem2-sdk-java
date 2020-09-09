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

import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import java.math.BigInteger;

/**
 * Mosaic global restriction transaction.
 *
 * <p>The mosaic global restrictions are the network-wide rules that will determine whether an
 * account will be able to transact a given mosaic.
 *
 * <p>Only accounts tagged with the key identifiers and values that meet the conditions will be able
 * to execute transactions involving the mosaic.
 *
 * <p>Additionally, the mosaic creator can define restrictions that depend directly on global
 * restrictions set on another mosaic - known as **reference mosaic**. The referenced mosaic and the
 * restricted mosaic do not necessarily have to be created by the same account, enabling the
 * delegation of mosaic permissions to a third party.
 *
 * @since 1.0
 */
public class MosaicGlobalRestrictionTransaction extends Transaction {

  private final UnresolvedMosaicId mosaicId;
  private final UnresolvedMosaicId referenceMosaicId;
  private final BigInteger restrictionKey;
  private final BigInteger previousRestrictionValue;
  private final MosaicRestrictionType previousRestrictionType;
  private final BigInteger newRestrictionValue;
  private final MosaicRestrictionType newRestrictionType;

  /**
   * Constructor.
   *
   * @param factory {@link MosaicGlobalRestrictionTransactionFactory}
   */
  MosaicGlobalRestrictionTransaction(MosaicGlobalRestrictionTransactionFactory factory) {
    super(factory);
    mosaicId = factory.getMosaicId();
    referenceMosaicId = factory.getReferenceMosaicId();
    restrictionKey = factory.getRestrictionKey();
    previousRestrictionValue = factory.getPreviousRestrictionValue();
    previousRestrictionType = factory.getPreviousRestrictionType();
    newRestrictionValue = factory.getNewRestrictionValue();
    newRestrictionType = factory.getNewRestrictionType();
  }

  /**
   * Returns the mosaic id.
   *
   * @return {@link UnresolvedMosaicId}
   */
  public UnresolvedMosaicId getMosaicId() {
    return mosaicId;
  }

  /**
   * Returns the reference mosaic id.
   *
   * @return {@link UnresolvedMosaicId}
   */
  public UnresolvedMosaicId getReferenceMosaicId() {
    return referenceMosaicId;
  }

  /**
   * Returns the restriction key.
   *
   * @return BigInteger restrictionKey
   */
  public BigInteger getRestrictionKey() {
    return restrictionKey;
  }

  /**
   * Returns the previous restriction value.
   *
   * @return BigInteger previousRestrictionValue
   */
  public BigInteger getPreviousRestrictionValue() {
    return previousRestrictionValue;
  }

  /**
   * Returns the previous mosaic restriction type.
   *
   * @return {@link MosaicRestrictionType}
   */
  public MosaicRestrictionType getPreviousRestrictionType() {
    return previousRestrictionType;
  }

  /**
   * Returns the new restriction value.
   *
   * @return BigInteger newRestrictionValue
   */
  public BigInteger getNewRestrictionValue() {
    return newRestrictionValue;
  }

  /**
   * Returns the new mosaic restriction type.
   *
   * @return {@link MosaicRestrictionType}
   */
  public MosaicRestrictionType getNewRestrictionType() {
    return newRestrictionType;
  }
}
