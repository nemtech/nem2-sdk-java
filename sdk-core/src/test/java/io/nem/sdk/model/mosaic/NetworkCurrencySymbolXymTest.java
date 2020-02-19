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

package io.nem.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class NetworkCurrencySymbolXymTest {

    private NetworkCurrency networkCurrency = NetworkCurrency.SYMBOL_XYM;

    @Test
    void shouldCreateRelativeMosaic() {
        Mosaic currency = networkCurrency.createRelative(BigInteger.valueOf(1000));
        assertEquals(BigInteger.valueOf(1000 * 1000000), currency.getAmount());
        assertEquals(networkCurrency.getNamespaceId().get(), currency.getId());
        assertEquals("E74B99BA41F4AFEE", currency.getIdAsHex());
    }

    @Test
    void shouldCreateRelativeMosaicUsingBigDecimal() {
        Mosaic currency = networkCurrency.createRelative(BigDecimal.valueOf(0.000001));
        assertEquals(BigInteger.valueOf((long) (0.000001 * 1000000)), currency.getAmount());
        assertEquals(networkCurrency.getNamespaceId().get(), currency.getId());
        assertEquals("E74B99BA41F4AFEE", currency.getIdAsHex());
    }

    @Test
    void shouldCreateAbsoluteMosaic() {
        Mosaic currency = networkCurrency.createAbsolute(BigInteger.valueOf(1));
        assertEquals(BigInteger.valueOf(1), currency.getAmount());
        assertEquals(networkCurrency.getNamespaceId().get(), currency.getId());
        assertEquals("E74B99BA41F4AFEE", currency.getIdAsHex());
    }

    @Test
    void shouldCompareNamespaceIdsForEquality() {
        NamespaceId namespaceId = NamespaceId
            .createFromId(BigInteger.valueOf(-1780160202445377554L));
        assertEquals(-1780160202445377554L, namespaceId.getIdAsLong());
        assertEquals(networkCurrency.getNamespaceId().get().getIdAsLong(),
            namespaceId.getIdAsLong());
        assertEquals(networkCurrency.getNamespaceId().get().getIdAsHex(), namespaceId.getIdAsHex());

        // Note:
        // BigInteger decimal generated from namespace path vs generated using Lower and Higher integers
        // Using namespace path:     9636553580561478212 (Decimal number)
        // Using Lower and Higher:  -8810190493148073404 (Decimal from signed 2's complement)
        // In Hexadecimal:              85bbea6cc462b244 (same for both)
    }

    @Test
    @SuppressWarnings("squid:S3415")
    void shouldHaveValidStatics() {
        assertEquals(networkCurrency.getUnresolvedMosaicId(),
            networkCurrency.getNamespaceId().get());
        assertEquals("symbol.xym", networkCurrency.getNamespaceId().get().getFullName().get());
        assertEquals(6, networkCurrency.getDivisibility());
        assertTrue(networkCurrency.isTransferable());
        assertFalse(networkCurrency.isSupplyMutable());
    }
}
