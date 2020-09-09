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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import java.math.BigInteger;

/**
 * Mosaic address restriction transaction.
 *
 * <p>Enabling accounts to transact with the token is similar to the process of adding elevated
 * permissions to a user in a company computer network.
 *
 * <p>The mosaic creator can modify the permissions of an account by sending a mosaic restriction
 * transaction targeting the account address.
 *
 * <p>**MosaicAddressRestrictionTransaction can only be announced in with Aggregate Transaction
 *
 * @since 1.0
 */
public class MosaicAddressRestrictionTransaction extends Transaction
    implements TargetAddressTransaction {

  private final UnresolvedMosaicId mosaicId;
  private final BigInteger restrictionKey;
  private final UnresolvedAddress targetAddress;
  private final BigInteger previousRestrictionValue;
  private final BigInteger newRestrictionValue;

  /**
   * Constructor.
   *
   * @param factory {@link MosaicAddressRestrictionTransactionFactory}
   */
  MosaicAddressRestrictionTransaction(MosaicAddressRestrictionTransactionFactory factory) {
    super(factory);
    mosaicId = factory.getMosaicId();
    restrictionKey = factory.getRestrictionKey();
    targetAddress = factory.getTargetAddress();
    previousRestrictionValue = factory.getPreviousRestrictionValue();
    newRestrictionValue = factory.getNewRestrictionValue();
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
   * Returns the restriction key.
   *
   * @return BigInteger restrictionKey
   */
  public BigInteger getRestrictionKey() {
    return restrictionKey;
  }

  /**
   * Returns the target address.
   *
   * @return {@link Address}
   */
  @Override
  public UnresolvedAddress getTargetAddress() {
    return targetAddress;
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
   * Returns the new restriction value.
   *
   * @return BigInteger newRestrictionValue
   */
  public BigInteger getNewRestrictionValue() {
    return newRestrictionValue;
  }
}
