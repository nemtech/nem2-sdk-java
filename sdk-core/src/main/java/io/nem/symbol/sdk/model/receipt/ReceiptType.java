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
package io.nem.symbol.sdk.model.receipt;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Enum containing receipt type constants.
 *
 * @see <a href=
 *     "https://github.com/nemtech/catapult-server/blob/main/src/catapult/model/ReceiptType.h"></a>
 * @see <a href=
 *     "https://github.com/nemtech/catapult-server/blob/main/src/catapult/model/ReceiptType.cpp"></a>
 */
public enum ReceiptType {

  /**
   * The recipient, account and amount of fees received for harvesting a block. It is recorded when
   * a block is harvested.
   *
   * <p>0x2143 (8515 decimal) - Harvest_Fee.
   */
  HARVEST_FEE(8515),

  /**
   * The unresolved and resolved alias. It is recorded when a transaction indicates a valid address
   * alias instead of an address.
   *
   * <p>0xF143 (61763 decimal) - Address_Alias_Resolution.
   */
  ADDRESS_ALIAS_RESOLUTION(61763),

  /**
   * The unresolved and resolved alias. It is recorded when a transaction indicates a valid mosaic
   * alias instead of a mosaicId.
   *
   * <p>0xF243 (62019 decimal) - Mosaic_Alias_Resolution.
   */
  MOSAIC_ALIAS_RESOLUTION(62019),

  /**
   * A collection of state changes for a given source. It is recorded when a state change receipt is
   * issued.
   *
   * <p>0xE134 (57652 decimal) - Transaction_Group.
   */
  TRANSACTION_GROUP(57667),

  /**
   * The sender and recipient of the mosaicId and amount representing the cost of registering the
   * mosaic. It is recorded when a mosaic is registered.
   *
   * <p>0x124D (4685 decimal) - Mosaic_Rental_Fee.
   */
  MOSAIC_RENTAL_FEE(4685),

  /**
   * The sender and recipient of the mosaicId and amount representing the cost of extending the
   * namespace. It is recorded when a namespace is registered or its duration is extended.
   *
   * <p>0x134E (4942 decimal) - Namespace_Rental_Fee.
   */
  NAMESPACE_RENTAL_FEE(4942),

  /**
   * The haslock sender, mosaicId and amount locked that is returned. It is recorded when an
   * aggregate bonded transaction linked to the hash completes.
   *
   * <p>0x2248 (8776 decimal) - LockHash_Completed.
   */
  LOCK_HASH_COMPLETED(8776),

  /**
   * The account receiving the locked mosaic, the mosaicId and the amount. It is recorded when a *
   * lock hash expires.
   *
   * <p>0x2348 (9032 decimal) - LockHash_Expired.
   */
  LOCK_HASH_EXPIRED(9032),

  /**
   * The account receiving the locked mosaic, the mosaicId and the amount. It is recorded when a
   * secretlock expires
   *
   * <p>0x2352 (9042 decimal) - LockSecret_Expired.
   */
  LOCK_SECRET_EXPIRED(9042),

  /**
   * The lockhash sender, mosaicId and amount locked. It is recorded when a valid
   * HashLockTransaction is announced.
   *
   * <p>0x3148 (12616 decimal) - LockHash_Created.
   */
  LOCK_HASH_CREATED(12616),

  /**
   * The secretlock sender, mosaicId and amount locked. It is recorded when a valid *
   * SecretLockTransaction is announced.
   *
   * <p>0x3152 (12626 decimal) - LockSecret_Created.
   */
  LOCK_SECRET_CREATED(12626),

  /**
   * The secretlock sender, mosaicId and amount locked. It is recorded when a secretlock is proved.
   *
   * <p>0x2252 (8786 decimal) - LockSecret_Completed
   */
  LOCK_SECRET_COMPLETED(8786),

  /**
   * The mosaicId expiring in this block. It is recorded when a mosaic expires.
   *
   * <p>0x414D (16717 decimal) - Mosaic_Expired.
   */
  MOSAIC_EXPIRED(16717),

  /**
   * The identifier of the namespace expiring in this block. It is recorded when the namespace
   * lifetime elapses.
   *
   * <p>0x414E (16718 decimal) - Namespace_Expired.
   */
  NAMESPACE_EXPIRED(16718),

  /**
   * The identifier of the namespace deleted in this block. It is recorded when the namespace grace
   * period elapses.
   *
   * <p>0x424E (16974 decimal) - Namespace_Deleted.
   */
  NAMESPACE_DELETED(16974),

  /**
   * The amount of native currency mosaics created. The receipt is recorded when the network has
   * inflation configured, and a new block triggers the creation of currency mosaics.
   *
   * <p>0x5143 (20803 decimal) - Inflation.
   */
  INFLATION(20803);

  public static final Set<ReceiptType> ARTIFACT_EXPIRY =
      Collections.unmodifiableSet(EnumSet.of(MOSAIC_EXPIRED, NAMESPACE_EXPIRED, NAMESPACE_DELETED));

  public static final Set<ReceiptType> BALANCE_CHANGE =
      Collections.unmodifiableSet(
          EnumSet.of(
              HARVEST_FEE,
              LOCK_HASH_COMPLETED,
              LOCK_HASH_CREATED,
              LOCK_HASH_EXPIRED,
              LOCK_SECRET_COMPLETED,
              LOCK_SECRET_CREATED,
              LOCK_SECRET_EXPIRED));

  public static final Set<ReceiptType> BALANCE_TRANSFER =
      Collections.unmodifiableSet(EnumSet.of(MOSAIC_RENTAL_FEE, NAMESPACE_RENTAL_FEE));

  public static final Set<ReceiptType> RESOLUTION_STATEMENT =
      Collections.unmodifiableSet(EnumSet.of(ADDRESS_ALIAS_RESOLUTION, MOSAIC_ALIAS_RESOLUTION));

  private final int value;

  ReceiptType(int value) {
    this.value = value;
  }

  /**
   * Static constructor converting receipt type raw value to enum instance.
   *
   * @param value the low level int value.
   * @return {@link ReceiptType}
   */
  public static ReceiptType rawValueOf(int value) {
    return Arrays.stream(values())
        .filter(e -> e.value == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
  }

  /**
   * Returns enum value.
   *
   * @return enum value
   */
  public int getValue() {
    return this.value;
  }
}
