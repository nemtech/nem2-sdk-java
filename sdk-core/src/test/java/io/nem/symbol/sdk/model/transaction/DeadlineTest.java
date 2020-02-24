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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DeadlineTest {

    @Test
    void shouldCreateADeadlineForTwoHoursFromNow() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        Deadline deadline = Deadline.create();
        assertTrue(now.isBefore(deadline.getLocalDateTime()), "now is before deadline localtime");
        assertTrue(
            now.plusHours(2).minusSeconds(1).isBefore(deadline.getLocalDateTime()),
            "now plus 2 hours is before deadline localtime");
        assertTrue(
            now.plusMinutes(2 * 60 + 2).isAfter(deadline.getLocalDateTime()),
            "now plus 2 hours and 2 seconds is after deadline localtime");
    }

    @Test
    void shouldCreateADeadlineForTwoHoursFromNowWithStaticConstructor() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        Deadline deadline = Deadline.create(2, ChronoUnit.HOURS);
        assertTrue(now.isBefore(deadline.getLocalDateTime()), "now is before deadline localtime");
        assertTrue(
            now.plusHours(2).minusSeconds(1).isBefore(deadline.getLocalDateTime()),
            "now plus 2 hours is before deadline localtime");
        assertTrue(
            now.plusMinutes(2 * 60 + 2).isAfter(deadline.getLocalDateTime()),
            "now plus 2 hours and 2 seconds is after deadline localtime");
    }

    @Test
    void fromToBigInteger() {
        BigInteger originalValue = BigInteger.valueOf(System.currentTimeMillis());
        Assertions.assertEquals(originalValue, new Deadline(originalValue).toBigInteger());
    }
}
