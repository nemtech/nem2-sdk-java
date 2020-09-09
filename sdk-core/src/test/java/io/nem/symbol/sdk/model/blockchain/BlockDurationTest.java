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
package io.nem.symbol.sdk.model.blockchain;

import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** {@link BlockDuration} tests */
public class BlockDurationTest {

  @Test
  void shouldConstruct() {
    long value = 10L;
    BlockDuration blockDuration = new BlockDuration(value);
    Assertions.assertEquals(value, blockDuration.getDuration());
    Assertions.assertEquals("10", blockDuration.toString());
  }

  @Test
  void shouldConstructBigInteger() {
    long value = 10L;
    BlockDuration blockDuration = new BlockDuration(BigInteger.valueOf(value));
    Assertions.assertEquals(value, blockDuration.getDuration());
    Assertions.assertEquals("10", blockDuration.toString());
  }

  @Test
  void shouldEquals() {
    BlockDuration blockDuration1 = new BlockDuration(BigInteger.valueOf(10));
    BlockDuration blockDuration2 = new BlockDuration(BigInteger.valueOf(10));
    BlockDuration blockDuration3 = new BlockDuration(BigInteger.valueOf(30));
    Assertions.assertEquals(blockDuration2, blockDuration1);
    Assertions.assertNotEquals(blockDuration3, blockDuration1);
    Assertions.assertEquals(blockDuration1, blockDuration2);
    Assertions.assertNotEquals(blockDuration1, blockDuration3);
    Assertions.assertNotEquals(BigInteger.valueOf(30), blockDuration3);
    Assertions.assertNotEquals(blockDuration3, BigInteger.valueOf(30));
    Assertions.assertEquals(41, blockDuration1.hashCode());
  }
}
