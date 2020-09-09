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

import io.nem.symbol.core.utils.MapperUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests of {@link AccountRestrictionTargetType} */
public class AccountRestrictionTargetTypeTest {

  @Test
  public void shouldGetModelObjectFromFromString() {
    Assertions.assertEquals(
        MapperUtils.toUnresolvedAddress("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1"),
        AccountRestrictionTargetType.ADDRESS.fromString(
            "9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1"));

    Assertions.assertEquals(
        TransactionType.MOSAIC_DEFINITION,
        AccountRestrictionTargetType.TRANSACTION_TYPE.fromString("16717"));

    Assertions.assertEquals(
        MapperUtils.toMosaicId("ABC"), AccountRestrictionTargetType.MOSAIC_ID.fromString("ABC"));
  }

  @Test
  public void shouldFailWhenInvalidAddress() {

    Assertions.assertEquals(
        "Value 'TTTT' cannot be converted to ADDRESS. Error: IllegalArgumentException: org.apache.commons.codec.DecoderException: Illegal hexadecimal character T at index 0",
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> AccountRestrictionTargetType.ADDRESS.fromString("TTTT"))
            .getMessage());
  }
}
