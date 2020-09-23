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
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Unit tests that uses the vector tests from github as inputs. */
class MosaicVectorTester extends AbstractVectorTester {

  private static Stream<Arguments> testMosaics() {
    return createArguments("5.test-mosaic-id.json", MosaicVectorTester::extractArgumentsSha, 50);
  }

  private static List<Arguments> extractArgumentsSha(Map<String, String> entry) {
    List<Arguments> arguments = new ArrayList<>();
    arguments.add(
        extractArguments(entry, NetworkType.MAIN_NET, "address_Public", "mosaicId_Public"));
    arguments.add(
        extractArguments(entry, NetworkType.TEST_NET, "address_PublicTest", "mosaicId_PublicTest"));
    arguments.add(extractArguments(entry, NetworkType.MIJIN, "address_Mijin", "mosaicId_Mijin"));
    arguments.add(
        extractArguments(entry, NetworkType.MIJIN_TEST, "address_MijinTest", "mosaicId_MijinTest"));
    return arguments;
  }

  private static Arguments extractArguments(
      Map<String, String> entry, NetworkType networkType, String addressField, String mosaicField) {
    String address = entry.get(addressField);
    String mosaicId = entry.get(mosaicField);
    return Arguments.of(networkType, entry.get("mosaicNonce"), address, mosaicId);
  }

  @ParameterizedTest
  @MethodSource("testMosaics")
  void testMosaics(NetworkType networkType, long mosaicNonce, String plain, String mosaicHex) {
    Address address = Address.createFromRawAddress(plain);
    Assertions.assertEquals(networkType, address.getNetworkType());
    MosaicNonce nonce = MosaicNonce.createFromBigInteger(BigInteger.valueOf(mosaicNonce));
    MosaicNonce nonce2 = MosaicNonce.createFromInteger((int) mosaicNonce);
    Assertions.assertEquals(mosaicNonce, nonce.getNonceAsLong());
    Assertions.assertEquals(mosaicNonce, nonce2.getNonceAsLong());
    Assertions.assertEquals(nonce2, nonce);
    MosaicId mosaicId = MosaicId.createFromNonce(nonce, address);
    Assertions.assertEquals(mosaicHex, mosaicId.getIdAsHex());
  }
}
