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
package io.nem.symbol.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests of {@link HttpStatus} */
public class HttpStatusTest {

  @Test
  void testAllTheValues() {
    Assertions.assertEquals(66, HttpStatus.values().length);
  }

  @Test
  void testGetReasonPhrase() {
    Assertions.assertEquals("Accepted", HttpStatus.ACCEPTED.getReasonPhrase());
  }

  @Test
  void testValue() {
    Assertions.assertEquals(404, HttpStatus.NOT_FOUND.value());
  }

  @Test
  void testValueOfInvalidValue() {
    Assertions.assertEquals(
        "No matching constant for [10]",
        Assertions.assertThrows(IllegalArgumentException.class, () -> HttpStatus.valueOf(10))
            .getMessage());
  }

  @Test
  void testValueOf() {
    Assertions.assertEquals(HttpStatus.NOT_FOUND, HttpStatus.valueOf(404));
  }

  @Test
  void testToString() {
    Assertions.assertEquals("404", HttpStatus.valueOf(404).toString());
  }
}
