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

package io.nem.symbol.sdk.model.account;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PropertyType}
 *
 * @author Fernando Boucquez
 */
public class PropertyTypeTest {


    @Test
    public void rawValueOf() {
        Assertions
            .assertEquals(PropertyType.BLOCK_MOSAIC, PropertyType.rawValueOf("0x82"));
        Assertions
            .assertEquals(PropertyType.BLOCK_MOSAIC, PropertyType.rawValueOf("130"));

        Assertions
            .assertEquals(PropertyType.BLOCK_TRANSACTION, PropertyType.rawValueOf("0x84"));

        Assertions
            .assertEquals(PropertyType.BLOCK_TRANSACTION, PropertyType.rawValueOf("132"));

        Assertions.assertEquals(PropertyType.ALLOW_MOSAIC,
            PropertyType.rawValueOf("0x02"));
        Assertions
            .assertEquals(PropertyType.ALLOW_MOSAIC, PropertyType.rawValueOf("2"));

    }


    @Test
    public void rawValueOfAllVales() {
        Arrays.stream(PropertyType.values())
            .forEach(t -> Assertions
                .assertEquals(t, PropertyType.rawValueOf(t.getValue().toString())));

        Arrays.stream(PropertyType.values())
            .forEach(t -> Assertions
                .assertEquals(t,
                    PropertyType.rawValueOf("0x" + Integer.toHexString(t.getValue()))));

    }

    @Test
    void rawValueOfInvalidValueIncorrectNumber() {
        IllegalArgumentException thrown =
            Assertions.assertThrows(IllegalArgumentException.class,
                () -> PropertyType.rawValueOf("3"));

        Assertions.assertEquals("3 is not a valid value", thrown.getMessage());
    }

    @Test
    void rawValueOfInvalidValueNotANumber() {
        IllegalArgumentException thrown =
            Assertions.assertThrows(IllegalArgumentException.class,
                () -> PropertyType.rawValueOf("IMNotANumber"));

        Assertions.assertEquals("IMNotANumber is not a valid value", thrown.getMessage());
    }
}
