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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AddressResolutionStatementTest {

    private final BigInteger height = BigInteger.valueOf(1473L);
    private final BigInteger height2 = BigInteger.valueOf(1500L);
    private final NetworkType networkType = NetworkType.MIJIN_TEST;

    private List<AddressResolutionStatement> addressResolutionStatements;

    private final NamespaceId mosaicNamespace1 = NamespaceId.createFromName("mosaicnamespace1");
    private final NamespaceId mosaicNamespace3 = NamespaceId.createFromName("mosaicnamespace3");
    private final NamespaceId mosaicNamespace4 = NamespaceId.createFromName("mosaicnamespace4");
    private final Address address1 = Account.generateNewAccount(networkType).getAddress();
    private final NamespaceId addressNamespace1 = NamespaceId.createFromName("addressnamespace1");


    @BeforeEach
    void setupStatement() {

        addressResolutionStatements = Collections.singletonList(
            new AddressResolutionStatement("abc", height, addressNamespace1, Collections.singletonList(
                new ResolutionEntry<>(address1, new ReceiptSource(1, 0), ReceiptType.ADDRESS_ALIAS_RESOLUTION))));
    }


    @Test
    void shouldGetResolvedEntryWhenPrimaryIdIsGreaterThanMaxAddress() {
        Optional<Address> resolved = AddressResolutionStatement
            .getResolvedAddress(addressResolutionStatements, height, addressNamespace1, 4, 0);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(address1, resolved.get());
    }

    @Test
    void shouldNotResolveAddressWhenInvalidHeight() {
        Optional<Address> resolved = AddressResolutionStatement
            .getResolvedAddress(addressResolutionStatements, height2, addressNamespace1, 4, 0);
        Assertions.assertFalse(resolved.isPresent());
    }

    @Test
    void shouldGetResolvedEntryRealAddress() {
        Optional<Address> resolved = AddressResolutionStatement
            .getResolvedAddress(addressResolutionStatements, height, address1, 4, 0);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(address1, resolved.get());
    }

    @Test
    void shouldGetResolvedEntryWhenPrimaryIdMatchesButSecondaryIdLessThanMinimum() {
        Optional<Address> resolved = AddressResolutionStatement
            .getResolvedAddress(addressResolutionStatements, height, mosaicNamespace1, 0, 6);
        Assertions.assertFalse(resolved.isPresent());
    }

    @Test
    void shouldReturnUndefinedAddress() {
        Optional<Address> resolved = AddressResolutionStatement
            .getResolvedAddress(addressResolutionStatements, height, addressNamespace1, 0, 6);
        Assertions.assertFalse(resolved.isPresent());
    }

    @Test
    void resolutionChangeInTheBlockMoreThanOneAggregate() {
        Optional<Address> resolved = AddressResolutionStatement
            .getResolvedAddress(addressResolutionStatements, height2, addressNamespace1, 0, 6);
        Assertions.assertFalse(resolved.isPresent());
    }
}
