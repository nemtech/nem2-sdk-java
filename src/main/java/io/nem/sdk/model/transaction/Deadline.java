/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.transaction;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.temporal.ChronoUnit;

import java.math.BigInteger;

/**
 * The deadline of the transaction. The deadline is given as the number of seconds elapsed since the creation of the nemesis block.
 * If a transaction does not get included in a block before the deadline is reached, it is deleted.
 *
 * @since 1.0
 */
public class Deadline {

    /**
     * Nemesis block timestamp.
     */
    public static Instant TIMESTAMP_NEMSIS_BLOCK = Instant.ofEpochSecond(1459468800);
    private final Instant instant;

    /**
     * Constructor
     *
     * @param units      int
     * @param chronoUnit Chrono unit
     */
    public Deadline(int units, ChronoUnit chronoUnit) {
        instant = Instant.now().plus(units, chronoUnit);
    }

    /**
     * Constructor
     *
     * @param units      int
     * @param chronoUnit Chrono unit
     */
    public Deadline(int units, java.time.temporal.ChronoUnit chronoUnit) {
        ChronoUnit threetenChronoUnit = ChronoUnit.valueOf(chronoUnit.name());
        instant = Instant.now().plus(units, threetenChronoUnit);
    }


    /**
     * Constructor
     *
     * @param input Deadline in BigInteger format
     */
    public Deadline(BigInteger input) {
        instant = Instant.ofEpochMilli(input.longValue());
    }

    /**
     * Create deadline model.
     *
     * @param units      int
     * @param chronoUnit Chrono unit
     * @return {@link Deadline}
     */
    public static Deadline create(int units, ChronoUnit chronoUnit) {
        return new Deadline(units, chronoUnit);
    }

    /**
     * Returns number of seconds elapsed since the creation of the nemesis block.
     *
     * @return long
     */
    public long getInstant() {
        return instant.toEpochMilli() - Deadline.TIMESTAMP_NEMSIS_BLOCK.toEpochMilli();
    }

    /**
     * Returns deadline as local date time in a given timezone.
     *
     * @param zoneId Timezone
     * @return LocalDateTime
     */
    public LocalDateTime getLocalDateTime(ZoneId zoneId) {
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    /**
     * Returns deadline as local date time.
     *
     * @return LocalDateTime
     */
    public LocalDateTime getLocalDateTime() {
        return getLocalDateTime(ZoneId.systemDefault());
    }
}
