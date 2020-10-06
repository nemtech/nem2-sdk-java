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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/** Factory of {@link MosaicGlobalRestrictionTransaction} */
public class MosaicGlobalRestrictionTransactionFactory
    extends TransactionFactory<MosaicGlobalRestrictionTransaction> {

  private final UnresolvedMosaicId mosaicId;
  private final BigInteger restrictionKey;
  private final BigInteger newRestrictionValue;
  private final MosaicRestrictionType newRestrictionType;
  private BigInteger previousRestrictionValue = BigInteger.ZERO;
  private MosaicRestrictionType previousRestrictionType = MosaicRestrictionType.NONE;
  private UnresolvedMosaicId referenceMosaicId = new MosaicId(BigInteger.ZERO);

  /**
   * Create a mosaic global restriction transaction object with factory build and modifier methods.
   *
   * @param networkType {@link NetworkType}
   * @param deadline the transaction deadline.
   * @param mosaicId {@link UnresolvedMosaicId}
   * @param restrictionKey BigInteger
   * @param newRestrictionValue BigInteger
   * @param newRestrictionType {@link MosaicRestrictionType}
   */
  private MosaicGlobalRestrictionTransactionFactory(
      NetworkType networkType,
      Deadline deadline,
      UnresolvedMosaicId mosaicId,
      BigInteger restrictionKey,
      BigInteger newRestrictionValue,
      MosaicRestrictionType newRestrictionType) {
    super(TransactionType.MOSAIC_GLOBAL_RESTRICTION, networkType, deadline);
    Validate.notNull(mosaicId, "RestrictedMosaicId must not be null");
    Validate.notNull(restrictionKey, "RestrictionKey must not be null");
    Validate.notNull(newRestrictionValue, "NewRestrictionValue must not be null");
    Validate.notNull(newRestrictionType, "NewRestrictionType must not be null");
    ConvertUtils.validateNotNegative(restrictionKey);
    ConvertUtils.validateNotNegative(newRestrictionValue);
    this.mosaicId = mosaicId;
    this.restrictionKey = restrictionKey;
    this.newRestrictionValue = newRestrictionValue;
    this.newRestrictionType = newRestrictionType;
  }

  /**
   * Static create method for factory.
   *
   * @param networkType {@link NetworkType}
   * @param deadline the deadline
   * @param mosaicId {@link UnresolvedMosaicId}
   * @param restrictionKey Restriction key.
   * @param newRestrictionValue New restriction value.
   * @param newRestrictionType {@link MosaicRestrictionType} New restriction type.
   * @return Mosaic global restriction transaction.
   */
  public static MosaicGlobalRestrictionTransactionFactory create(
      NetworkType networkType,
      Deadline deadline,
      UnresolvedMosaicId mosaicId,
      BigInteger restrictionKey,
      BigInteger newRestrictionValue,
      MosaicRestrictionType newRestrictionType) {
    return new MosaicGlobalRestrictionTransactionFactory(
        networkType, deadline, mosaicId, restrictionKey, newRestrictionValue, newRestrictionType);
  }

  @Override
  public MosaicGlobalRestrictionTransaction build() {
    return new MosaicGlobalRestrictionTransaction(this);
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
   * @return BigInteger
   */
  public BigInteger getRestrictionKey() {
    return restrictionKey;
  }

  /**
   * Returns the previous restriction value.
   *
   * @return BigInteger
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

  /**
   * This method sets referenceMosaicId.
   *
   * @param referenceMosaicId the new referenceMosaicId
   * @return this factory.
   */
  public MosaicGlobalRestrictionTransactionFactory referenceMosaicId(
      UnresolvedMosaicId referenceMosaicId) {
    Validate.notNull(referenceMosaicId, "ReferenceMosaicId must not be null");
    this.referenceMosaicId = referenceMosaicId;
    return this;
  }

  /**
   * This method changes previousRestrictionType.
   *
   * @param previousRestrictionType the new previousRestrictionType
   * @return this factory.
   */
  public MosaicGlobalRestrictionTransactionFactory previousRestrictionType(
      MosaicRestrictionType previousRestrictionType) {
    Validate.notNull(previousRestrictionType, "PreviousRestrictionType must not be null");
    this.previousRestrictionType = previousRestrictionType;
    return this;
  }

  /**
   * This method changes previousRestrictionValue.
   *
   * @param previousRestrictionValue the new previousRestrictionValue
   * @return this factory.
   */
  public MosaicGlobalRestrictionTransactionFactory previousRestrictionValue(
      BigInteger previousRestrictionValue) {
    Validate.notNull(previousRestrictionValue, "PreviousRestrictionValue must not be null");
    this.previousRestrictionValue = previousRestrictionValue;
    return this;
  }
}
