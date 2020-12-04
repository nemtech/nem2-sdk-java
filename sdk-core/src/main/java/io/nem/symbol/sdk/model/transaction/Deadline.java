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

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * The deadline of the transaction. The deadline is given as the number of seconds elapsed since the
 * creation of the nemesis block. If a transaction does not get included in a block before the
 * deadline is reached, it is deleted.
 *
 * @since 1.0
 */
public class Deadline {

  /** number of millis elapsed since the creation of the nemesis block */
  private final BigInteger value;

  /**
   * Constructor
   *
   * @param input Deadline in BigInteger format
   */
  public Deadline(BigInteger input) {
    this.value = input;
  }

  /**
   * Create deadline model.
   *
   * @param epochAdjustment the network's epoch adjustment. Defined in the network/properties.
   * @param units int
   * @param chronoUnit Chrono unit
   * @return {@link Deadline}
   */
  public static Deadline create(Duration epochAdjustment, int units, ChronoUnit chronoUnit) {
    long millis =
        Instant.now()
            .plus(units, chronoUnit)
            .minus(epochAdjustment)
            .toEpochMilli();
    return new Deadline(BigInteger.valueOf(millis));
  }

  /**
   * Create the default deadline of 2 hours.
   *
   * @param epochAdjustment the network's epoch adjustment. Defined in the network/properties.
   * @return {@link Deadline}
   */
  public static Deadline create(Duration epochAdjustment) {
    return create(epochAdjustment, 2, ChronoUnit.HOURS);
  }

  /** @return the BigInteger representation of the duration. */
  public BigInteger toBigInteger() {
    return value;
  }

  /**
   * Returns number of seconds elapsed since the creation of the nemesis block in milliseconds.
   *
   * @return long
   */
  public long getValue() {
    return value.longValue();
  }

  /**
   * Returns the value as instant
   *
   * @param epochAdjustment the network's epoch adjustment. Defined in the network/properties.
   * @return the instant time from the creation of the nemesis block.
   */
  public Instant getInstant(Duration epochAdjustment) {
    return Instant.ofEpochMilli(value.longValue()).plusMillis(epochAdjustment.toMillis());
  }

  /**
   * Returns deadline as local date time in a given timezone.
   *
   * @param epochAdjustment the network's epoch adjustment. Defined in the network/properties.
   * @param zoneId Timezone
   * @return LocalDateTime
   */
  public LocalDateTime getLocalDateTime(Duration epochAdjustment, ZoneId zoneId) {
    return LocalDateTime.ofInstant(getInstant(epochAdjustment), zoneId);
  }

  /**
   * Returns deadline as local date time.
   *
   * @param epochAdjustment the network's epoch adjustment. Defined in the network/properties.
   * @return LocalDateTime
   */
  public LocalDateTime getLocalDateTime(Duration epochAdjustment) {
    return getLocalDateTime(epochAdjustment, ZoneId.systemDefault());
  }
}
