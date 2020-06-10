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

package io.nem.symbol.sdk.model.namespace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class AliasTest {

    Address address = Address.generateRandom(NetworkType.MIJIN_TEST);
    Address address2 = Address.generateRandom(NetworkType.MIJIN_TEST);
    MosaicId mosaicId = new MosaicId("0dc67fbe1cad29e3");
    MosaicId mosaicId2 = new MosaicId("0dc67fbd1cad29e2");

    @Test
    void shouldCreateAEmptyAlias() {
        Alias alias = new EmptyAlias();
        assertEquals(AliasType.NONE, alias.getType());
        assertTrue(alias.isEmpty());
    }

    @Test
    void shouldCreateAAddressAlias() {
        AddressAlias addressAlias = new AddressAlias(address);
        assertEquals(AliasType.ADDRESS, addressAlias.getType());
        assertEquals(address, addressAlias.getAliasValue());
        assertFalse(addressAlias.isEmpty());
    }

    @Test
    void shouldCreateAMosaicAlias() {
        MosaicAlias mosaicAlias = new MosaicAlias(mosaicId);
        assertEquals(AliasType.MOSAIC, mosaicAlias.getType());
        assertEquals(mosaicId, mosaicAlias.getAliasValue());
        assertFalse(mosaicAlias.isEmpty());
    }

    @Test
    void shouldCompareAddressInAdressAlias() {
        AddressAlias addressAlias = new AddressAlias(address);
        AddressAlias addressAlias1 = new AddressAlias(address);
        AddressAlias addressAlias2 = new AddressAlias(address2);
        assertEquals(addressAlias.getAliasValue(), addressAlias1.getAliasValue());
        assertNotEquals(addressAlias.getAliasValue(), addressAlias2.getAliasValue());
        assertEquals(addressAlias, addressAlias);
        assertEquals(addressAlias, addressAlias1);
        assertEquals(addressAlias.hashCode(), addressAlias1.hashCode());
        assertNotEquals(addressAlias, addressAlias2);
        assertNotEquals( addressAlias2, BigInteger.valueOf(-1));
        assertNotEquals(addressAlias.hashCode(), addressAlias2.hashCode());
    }

    @Test
    void shouldCompareMosaicIdInMosaicAlias() {
        MosaicAlias mosaicAlias = new MosaicAlias(mosaicId);
        MosaicAlias mosaicAlias1 = new MosaicAlias(mosaicId);
        MosaicAlias mosaicAlias2 = new MosaicAlias(mosaicId2);
        assertEquals(mosaicAlias.getAliasValue(), mosaicAlias1.getAliasValue());
        assertNotEquals(mosaicAlias.getAliasValue(), mosaicAlias2.getAliasValue());
    }
}
