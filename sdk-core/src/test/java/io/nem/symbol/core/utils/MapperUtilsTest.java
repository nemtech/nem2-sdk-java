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

import io.nem.symbol.sdk.infrastructure.RandomUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapperUtilsTest {

  private final NetworkType networkType = NetworkType.MIJIN_TEST;

  @Test
  void shouldMapToNamespaceId() {
    Assertions.assertNull(MapperUtils.toNamespaceId(null));
    Assertions.assertEquals(
        BigInteger.valueOf(1194684), MapperUtils.toNamespaceId("123ABC").getId());
  }

  @Test
  void shouldMapToAddress() {
    Assertions.assertNull(MapperUtils.toAddressFromRawAddress(null));
    Address address =
        MapperUtils.toAddressFromRawAddress("TDGRZDZEHD4M5K3JIT64DU3PEKFYNF5VWFEYDQA");
    Assertions.assertNotNull(address);
    Assertions.assertEquals("TDGRZDZEHD4M5K3JIT64DU3PEKFYNF5VWFEYDQA", address.plain());
    Assertions.assertEquals("TDGRZD-ZEHD4M-5K3JIT-64DU3P-EKFYNF-5VWFEY-DQA", address.pretty());
  }

  @Test
  void toAddressFromEncoded() {

    Assertions.assertNull(MapperUtils.toAddress(null));
    Address address = MapperUtils.toAddress("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1");
    Assertions.assertNotNull(address);

    Assertions.assertEquals("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYI", address.plain());
    Assertions.assertEquals("SBILTA-367K2L-X2FEXG-5TFWAS-7GEFYA-GY7QLF-BYI", address.pretty());
    Assertions.assertEquals("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E1", address.encoded());
  }

  @Test
  void shouldMapToMosaicId() {
    Assertions.assertNull(MapperUtils.toMosaicId(null));
    Assertions.assertEquals(BigInteger.valueOf(1194684), MapperUtils.toMosaicId("123ABC").getId());
  }

  @Test
  void toUnresolvedAddress() {
    NetworkType networkType = NetworkType.TEST_NET;
    System.out.println(networkType.getValue());
    String hex = "9960629109A48AFBC0000000000000000000000000000000";
    UnresolvedAddress value = MapperUtils.toUnresolvedAddress(hex);
    Assertions.assertEquals("C0FB8AA409916260", ((NamespaceId) value).getIdAsHex());
    Assertions.assertEquals(hex, ((NamespaceId) value).encoded(networkType));
  }

  @Test
  void toUnresolvedAddressFromNamespace() {
    Assertions.assertNull(MapperUtils.toUnresolvedAddress(null));
    NamespaceId namespaceId = NamespaceId.createFromName("some.name");

    Assertions.assertEquals(
        "91D9E338F78767ED95000000000000000000000000000000", namespaceId.encoded(networkType));
    Assertions.assertEquals(
        namespaceId.encoded(networkType),
        MapperUtils.toUnresolvedAddress(namespaceId.encoded(networkType)).encoded(networkType));
  }

  @Test
  void toUnresolvedAddressFromAddress() {
    Assertions.assertNull(MapperUtils.toUnresolvedAddress(null));
    Address address = Address.generateRandom(networkType);
    Assertions.assertEquals(address, MapperUtils.toUnresolvedAddress(address.encoded(networkType)));
  }

  @Test
  void toUnresolvedAddressZeroPadded() {

    UnresolvedAddress actual =
        MapperUtils.toUnresolvedAddress("01E7CA7E22727DDD8800000000000000000000000000000000");
    Assertions.assertTrue(actual instanceof NamespaceId);
    Assertions.assertEquals("88DD7D72227ECAE7", ((NamespaceId) actual).getIdAsHex());
  }

  @Test
  void toUnresolvedMosaicId() {
    MosaicId mosaicId = new MosaicId("11F4B1B3AC033DB5");
    NamespaceId namespaceId = NamespaceId.createFromName("some.name123");

    Assertions.assertNull(MapperUtils.toUnresolvedMosaicId(null));
    Assertions.assertEquals(mosaicId, MapperUtils.toUnresolvedMosaicId(mosaicId.getIdAsHex()));
    Assertions.assertEquals(
        namespaceId, MapperUtils.toUnresolvedMosaicId(namespaceId.getIdAsHex()));

    Assertions.assertEquals(
        new NamespaceId("9a52fde35777cd4f"), MapperUtils.toUnresolvedMosaicId("9a52fde35777cd4f"));
  }

  @Test
  public void givenUsingPlainJava_whenGeneratingRandomStringUnbounded_thenCorrect() {
    byte[] array = RandomUtils.generateRandomBytes(7);
    String generatedString = new String(array, StandardCharsets.UTF_8);
    Assertions.assertNotNull(generatedString);
  }

  @Test
  public void toUnresolvedAddressFromPlain() {
    Assertions.assertEquals(
        new NamespaceId("C0FB8AA409916260"),
        MapperUtils.toUnresolvedAddressFromPlain("C0FB8AA409916260"));

    Assertions.assertEquals(
        Address.createFromRawAddress("TAHNZXQBC57AA7KJTMGS3PJPZBXN7DV5JHJU42A"),
        MapperUtils.toUnresolvedAddressFromPlain("TAHNZXQBC57AA7KJTMGS3PJPZBXN7DV5JHJU42A"));

    IllegalArgumentException e =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {
              MapperUtils.toUnresolvedAddressFromPlain("abc");
            });
    Assertions.assertEquals("'abc' is not a valid plain address or namespace hex", e.getMessage());
  }
}
