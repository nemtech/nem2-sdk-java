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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.symbol.core.utils.ByteUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.mosaic.IllegalIdentifierException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class IdGeneratorTest {

  static Stream<Arguments> provider() {
    return Stream.of(
        Arguments.of(
            "4AFF7B4BA8C1C26A7917575993346627CB6C80DE62CD92F7F9AEDB7064A3DE62",
            "B76FE378",
            "3AD842A8C0AFC518"),
        Arguments.of(
            "3811EDF245F1D30171FF1474B24C4366FECA365A8457AAFA084F3DE4AEA0BA60",
            "21832A2A",
            "24C54740A9F3893F"),
        Arguments.of(
            "3104D468D20491EC12C988C50CAD9282256052907415359201C46CBD7A0BCD75",
            "2ADBB332",
            "43908F2DEEA04245"),
        Arguments.of(
            "6648E16513F351E9907B0EA34377E25F579BE640D4698B28E06585A21E94CFE2",
            "B9175E0F",
            "183172772BD29E78"),
        Arguments.of(
            "1C05C40D38463FE725CF0584A3A69E3B0D6B780196A88C50624E49B921EE1404",
            "F6077DDD",
            "423DB0B12F787422"),
        Arguments.of(
            "37926B3509987093C776C8EA3E7F978E3A78142B5C96B9434C3376177DC65EFD",
            "08190C6D",
            "1F07D26B6CD352D5"),
        Arguments.of(
            "FDC6B0D415D90536263431F05C46AC492D0BD9B3CFA1B79D5A35E0F371655C0C",
            "81662AA5",
            "74511F54940729CB"),
        Arguments.of(
            "2D4EA99965477AEB3BC162C09C24C8DA4DABE408956C2F69642554EA48AAE1B2",
            "EA16BF58",
            "4C55843B6EB4A5BD"),
        Arguments.of(
            "68EB2F91E74D005A7C22D6132926AEF9BFD90A3ACA3C7F989E579A93EFF24D51",
            "E5F87A8B",
            "4D89DE2B6967666A"),
        Arguments.of(
            "3B082C0074F65D1E205643CDE72C6B0A3D0579C7ACC4D6A7E23A6EC46363B90F",
            "1E6BB49F",
            "0A96B3A44615B62F"),
        Arguments.of(
            "81245CA233B729FAD1752662EADFD73C5033E3B918CE854E01F6EB51E98CD9F1",
            "B82965E3",
            "1D6D8E655A77C4E6"),
        Arguments.of(
            "D3A2C1BFD5D48239001174BFF62A83A52BC9A535B8CDBDF289203146661D8AC4",
            "F37FB460",
            "268A3CC23ADCDA2D"),
        Arguments.of(
            "4C4CA89B7A31C42A7AB963B8AB9D85628BBB94735C999B2BD462001A002DBDF3",
            "FF6323B0",
            "51202B5C51F6A5A9"),
        Arguments.of(
            "2F95D9DCD4F18206A54FA95BD138DA1C038CA82546525A8FCC330185DA0647DC",
            "99674492",
            "5CE4E38B09F1423D"),
        Arguments.of(
            "A7892491F714B8A7469F763F695BDB0B3BF28D1CC6831D17E91F550A2D48BD12",
            "55141880",
            "5EFD001B3350C9CB"),
        Arguments.of(
            "68BBDDF5C08F54278DA516F0E4A5CCF795C10E2DE26CAF127FF4357DA7ACF686",
            "11FA5BAF",
            "179F0CDD6D2CCA7B"),
        Arguments.of(
            "014F6EF90792F814F6830D64017107534F5B718E2DD43C25ACAABBE347DEC81E",
            "6CFBF7B3",
            "53095813DEB3D108"),
        Arguments.of(
            "95A6344597E0412C51B3559F58F564F9C2DE3101E5CC1DD8B115A93CE7040A71",
            "905EADFE",
            "3551C4B12DDF067D"),
        Arguments.of(
            "0D7DDFEB652E8B65915EA734420A1233A233119BF1B0D41E1D5118CDD44447EE",
            "61F5B671",
            "696E2FB0682D3199"),
        Arguments.of(
            "FFD781A20B01D0C999AABC337B8BAE82D1E7929A9DD77CC1A71E4B99C0749684",
            "D8542F1A",
            "6C55E05D11D19FBD"));
  }

  @ParameterizedTest
  @MethodSource("provider")
  void mosaicIdGeneratesCorrectGivenNonceAndPublicKey(
      String hexPublicKey, String hexNonce, String hexExpectedMosaicId) {
    byte[] nonceBytes = ConvertUtils.fromHexToBytes(hexNonce);
    // ArrayUtils.reverse(nonceBytes);
    BigInteger id =
        IdGenerator.generateMosaicId(
            ByteUtils.bytesToInt(nonceBytes), ConvertUtils.fromHexToBytes(hexPublicKey));
    assertEquals(hexExpectedMosaicId.toUpperCase(), String.format("%016X", id));
    assertEquals(hexExpectedMosaicId.toUpperCase(), ConvertUtils.toHex(id.toByteArray()));
  }

  /*
   * @Test void namespacePathGeneratesCorrectWellKnownRootPath2() { BigInteger id =
   * IdGenerator.generateNamespaceId("nem", BigInteger.valueOf(0)); BigInteger id2 =
   * IdGenerator.generateNamespaceId2("nem", BigInteger.valueOf(0)); assertEquals(id, id2); }
   */

  @Test
  void namespacePathGeneratesCorrectWellKnownRootPath() {
    List<BigInteger> ids = IdGenerator.generateNamespacePath("nem");
    BigInteger id = IdGenerator.generateNamespaceId("nem");
    BigInteger id2 = IdGenerator.generateNamespaceId("nem", BigInteger.valueOf(0));
    assertEquals(1, ids.size());
    assertEquals(new BigInteger("9562080086528621131"), ids.get(0));
    assertEquals(id, ids.get(0));
    assertEquals(id, id2);
  }

  @Test
  void namespacePathGeneratesCorrectWellKnownChildPath() {
    List<BigInteger> ids = IdGenerator.generateNamespacePath("nem.subnem");

    assertEquals(2, ids.size());
    assertEquals(new BigInteger("9562080086528621131"), ids.get(0));
    assertEquals(new BigInteger("16440672666685223858"), ids.get(1));
  }

  @Test
  void namespacePathSupportsMultiLevelNamespaces() {
    List<BigInteger> ids = new ArrayList<BigInteger>();
    ids.add(IdGenerator.generateNamespaceId("foo", BigInteger.valueOf(0)));
    ids.add(IdGenerator.generateNamespaceId("bar", ids.get(0)));
    ids.add(IdGenerator.generateNamespaceId("baz", ids.get(1)));

    assertEquals(IdGenerator.generateNamespacePath("foo.bar.baz"), ids);
  }

  @Test
  void namespacePathSupportsMultiLevelNamespaces2() {
    List<BigInteger> ids = new ArrayList<BigInteger>();
    ids.add(IdGenerator.generateNamespaceId("foo", BigInteger.valueOf(0)));
    ids.add(IdGenerator.generateNamespaceId("bar", ids.get(0)));
    ids.add(IdGenerator.generateNamespaceId("baz", ids.get(1)));

    assertEquals(ids.get(2), IdGenerator.generateNamespaceId("baz", "foo.bar"));
  }

  @Test
  void namespacePathSupportsMultiLevelNamespaces2a() {
    List<BigInteger> ids = new ArrayList<BigInteger>();
    ids.add(IdGenerator.generateNamespaceId("nem", BigInteger.valueOf(0)));
    ids.add(IdGenerator.generateNamespaceId("subnem", ids.get(0)));
    ids.add(IdGenerator.generateNamespaceId("subsubnem", ids.get(1)));

    assertEquals(ids.get(2), IdGenerator.generateNamespaceId("subsubnem", "nem.subnem"));
  }

  @Test
  void namespacePathSupportsMultiLevelNamespaces3() {
    List<BigInteger> ids = new ArrayList<BigInteger>();
    ids.add(IdGenerator.generateNamespaceId("foo", BigInteger.valueOf(0)));
    ids.add(IdGenerator.generateNamespaceId("bar", ids.get(0)));
    ids.add(IdGenerator.generateNamespaceId("baz", ids.get(1)));

    assertEquals(ids.get(2), IdGenerator.generateNamespaceId("bar.baz", "foo"));
  }

  @Test
  void namespaceNameInvalid() {
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespaceId("foo.bar", BigInteger.valueOf(0));
        },
        "contains dot");
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespaceId("", BigInteger.valueOf(0));
        },
        "having zero length");
  }

  @Test
  void namespacePathInvalid() {
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespacePath("");
        },
        "having zero length");
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespacePath("alpha.bet@.zeta");
        },
        "invalid part");
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespacePath("a!pha.beta.zeta");
        },
        "invalid part");
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespacePath("alpha.beta.ze^a");
        },
        "invalid part");
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespacePath(".");
        },
        "invalid part");
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespacePath("..");
        },
        "invalid part");
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespacePath(".a");
        },
        "invalid part");
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespacePath("a..a");
        },
        "invalid part");
    assertThrows(
        IllegalIdentifierException.class,
        () -> {
          IdGenerator.generateNamespacePath("A");
        },
        "invalid part");
  }
}
