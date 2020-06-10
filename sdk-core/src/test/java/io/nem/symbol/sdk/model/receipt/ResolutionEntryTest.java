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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ResolutionEntryTest {

    static Address address;
    static MosaicId mosaicId;
    static ReceiptSource receiptSource;

    @BeforeAll
    public static void setup() {
        address = Address.generateRandom(NetworkType.MIJIN_TEST);
        mosaicId = new MosaicId("85BBEA6CC462B244");
        receiptSource = new ReceiptSource(1, 1);
    }

    @Test
    void shouldCreateAddressResolutionEntry() {

        ResolutionEntry<Address> resolutionEntry = ResolutionEntry.forAddress(address, receiptSource);
        assertEquals(ReceiptType.ADDRESS_ALIAS_RESOLUTION, resolutionEntry.getType());
        assertEquals(resolutionEntry.getReceiptSource(), receiptSource);
        assertEquals(resolutionEntry.getResolved(), address);
        assertEquals("0100000001000000" + address.encoded(),
            ConvertUtils.toHex(resolutionEntry.serialize()));
    }

    @Test
    void shouldCreateMosaicResolutionEntry() {

        ResolutionEntry<MosaicId> resolutionEntry = ResolutionEntry.forMosaicId(mosaicId, receiptSource);
        assertEquals(ReceiptType.MOSAIC_ALIAS_RESOLUTION, resolutionEntry.getType());
        assertEquals(resolutionEntry.getReceiptSource(), receiptSource);
        assertEquals(resolutionEntry.getResolved(), mosaicId);
        assertEquals("010000000100000044B262C46CEABB85", ConvertUtils.toHex(resolutionEntry.serialize()));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongReceiptType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ResolutionEntry<>(address, receiptSource, ReceiptType.NAMESPACE_RENTAL_FEE));

        assertEquals("Receipt type: [NAMESPACE_RENTAL_FEE] is not valid.", exception.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongResolvedType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                new ResolutionEntry("", receiptSource, ReceiptType.ADDRESS_ALIAS_RESOLUTION);
            });
        assertEquals(
            "Resolved type: [io.nem.symbol.sdk.model.account.Address] is not valid for this ResolutionEntry of type [ADDRESS_ALIAS_RESOLUTION]",
            exception.getMessage());
    }
}
