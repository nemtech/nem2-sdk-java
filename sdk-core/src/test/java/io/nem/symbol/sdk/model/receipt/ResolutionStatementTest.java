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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ResolutionStatementTest {


    private static NetworkType networkType = NetworkType.MIJIN_TEST;

    static Address address;
    static MosaicId mosaicId;
    static NamespaceId namespaceId;
    static ReceiptSource receiptSource;
    static ResolutionEntry<Address> addressResolutionEntry;
    static ResolutionEntry<MosaicId> mosaicResolutionEntry;

    @BeforeAll
    public static void setup() {
        address = new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR2",
            networkType);
        mosaicId = new MosaicId("85BBEA6CC462B244");
        namespaceId = NamespaceId.createFromName("money");
        receiptSource = new ReceiptSource(1, 1);
        addressResolutionEntry =
            new ResolutionEntry<>(address, receiptSource, ReceiptType.ADDRESS_ALIAS_RESOLUTION);
        mosaicResolutionEntry =
            new ResolutionEntry<>(mosaicId, receiptSource, ReceiptType.MOSAIC_ALIAS_RESOLUTION);
    }

    @Test
    void shouldCreateAddressResolutionStatement() {
        List<ResolutionEntry<Address>> resolutionEntries = new ArrayList<>();
        resolutionEntries.add(addressResolutionEntry);
        AddressResolutionStatement resolutionStatement =
            new AddressResolutionStatement(BigInteger.TEN, address,
                resolutionEntries);
        assertEquals(BigInteger.TEN, resolutionStatement.getHeight());
        assertEquals(resolutionStatement.getUnresolved(), address);
        assertEquals(resolutionStatement.getResolutionEntries(), resolutionEntries);
    }

    @Test
    void shouldCreateAliasAddressResolutionStatement() {
        List<ResolutionEntry<Address>> resolutionEntries = new ArrayList<>();
        resolutionEntries.add(addressResolutionEntry);
        AddressResolutionStatement resolutionStatement =
            new AddressResolutionStatement(BigInteger.TEN, namespaceId,
                resolutionEntries);
        assertEquals(BigInteger.TEN, resolutionStatement.getHeight());
        assertEquals(resolutionStatement.getUnresolved(), namespaceId);
        assertEquals(resolutionStatement.getResolutionEntries(), resolutionEntries);
    }

    @Test
    void shouldCreateMosaicResolutionStatement() {
        List<ResolutionEntry<MosaicId>> resolutionEntries = new ArrayList<>();
        resolutionEntries.add(mosaicResolutionEntry);
        MosaicResolutionStatement resolutionStatement =
            new MosaicResolutionStatement(BigInteger.TEN, mosaicId, resolutionEntries);
        assertEquals(BigInteger.TEN, resolutionStatement.getHeight());
        assertEquals(resolutionStatement.getUnresolved(), mosaicId);
        assertEquals(resolutionStatement.getResolutionEntries(), resolutionEntries);
    }

    @Test
    void shouldCreateAliasMosaicResolutionStatement() {
        List<ResolutionEntry<MosaicId>> resolutionEntries = new ArrayList<>();
        resolutionEntries.add(mosaicResolutionEntry);
        MosaicResolutionStatement resolutionStatement =
            new MosaicResolutionStatement(BigInteger.TEN, namespaceId, resolutionEntries);
        assertEquals(BigInteger.TEN, resolutionStatement.getHeight());
        assertEquals(resolutionStatement.getUnresolved(), namespaceId);
        assertEquals(resolutionStatement.getResolutionEntries(), resolutionEntries);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongUnResolvedType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                List<ResolutionEntry<MosaicId>> resolutionEntries = new ArrayList<>();
                resolutionEntries.add(mosaicResolutionEntry);
                new ResolutionStatement<Address, MosaicId>(ResolutionType.ADDRESS, BigInteger.TEN,
                    address, resolutionEntries) {
                };
            });
        Assertions.assertEquals(
            "Resolution Type: [ADDRESS] does not match ResolutionEntry's type: [MOSAIC_ALIAS_RESOLUTION] for this ResolutionStatement",
            exception.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithMismatchedUnresolvedAndResolvedType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> {
                List<ResolutionEntry<MosaicId>> resolutionEntries = new ArrayList<>();
                resolutionEntries.add(mosaicResolutionEntry);
                new ResolutionStatement<MosaicId, MosaicId>(ResolutionType.ADDRESS, BigInteger.TEN,
                    mosaicId, resolutionEntries) {
                };
            });
        Assertions.assertEquals(
            "Unresolved Type: [io.nem.symbol.sdk.model.account.UnresolvedAddress] is not valid for this ResolutionEntry type ADDRESS",
            exception.getMessage());
    }

    @Test
    void shouldGenerateHashAddress() {
        List<ResolutionEntry<Address>> resolutionEntries = new ArrayList<>();
        resolutionEntries.add(addressResolutionEntry);
        AddressResolutionStatement resolutionStatement = new AddressResolutionStatement(
            BigInteger.TEN, address, resolutionEntries);

        String hash = resolutionStatement.generateHash(networkType);

        assertFalse(hash.isEmpty());
        assertEquals("083827D961D3CE3AF734865D9D645E6FBC1256B921E485013F455C7385AC72F9", hash);
    }

    @Test
    void shouldGenerateHashMosaic() {
        List<ResolutionEntry<MosaicId>> resolutionEntries = new ArrayList<>();
        resolutionEntries.add(mosaicResolutionEntry);
        MosaicResolutionStatement resolutionStatement = new MosaicResolutionStatement(
            BigInteger.TEN, mosaicId, resolutionEntries);

        String hash = resolutionStatement.generateHash(networkType);

        assertFalse(hash.isEmpty());
        assertEquals("9BB7E01FAEA831E790E4A2DE8DBEDB32F73889493F6B1BC02031457CB655F6D0", hash);
    }

}
