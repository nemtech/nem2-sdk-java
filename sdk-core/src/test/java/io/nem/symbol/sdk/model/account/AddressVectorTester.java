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

import io.nem.symbol.core.utils.AbstractVectorTester;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests that uses the vector tests from github as inputs.
 */
class AddressVectorTester extends AbstractVectorTester {

    private static Stream<Arguments> testAddress() {
        return createArguments("1.test-address.json", AddressVectorTester::extractArgumentsSha,
            0, 5
        );
    }

    private static List<Arguments> extractArgumentsSha(Map<String, String> entry) {
        List<Arguments> arguments = new ArrayList<>();
        arguments.add(extractArguments(entry, NetworkType.MAIN_NET, "address_public"));
        arguments.add(extractArguments(entry, NetworkType.TEST_NET, "address_public_test"));
        arguments.add(extractArguments(entry, NetworkType.MIJIN, "address_mijin"));
        arguments.add(extractArguments(entry, NetworkType.MIJIN_TEST, "address_mijin_test"));
        return arguments;
    }

    private static Arguments extractArguments(
        Map<String, String> entry, NetworkType networkType, String addressField) {
        String address = entry.get(addressField);
        if (address == null) {
            return null;
        }
        return Arguments.of(networkType, entry.get("publicKey"), address);
    }


    @ParameterizedTest
    @MethodSource("testAddress")
    void testAddress(NetworkType networkType, String publicKey, String encoded) {
        Address address = Address.createFromPublicKey(publicKey, networkType);
        Assertions.assertEquals(encoded, address.plain());
        Assertions.assertEquals(networkType, address.getNetworkType());
        Assertions.assertTrue(Address.isValidPlainAddress(encoded));
        Assertions.assertTrue(Address.isValidEncodedAddress(address.encoded()));
    }

}
