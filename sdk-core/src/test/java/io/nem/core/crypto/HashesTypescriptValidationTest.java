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

package io.nem.core.crypto;

import io.nem.core.utils.ConvertUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 */
public class HashesTypescriptValidationTest {

    private static Stream<Arguments> testHashersValues() {
        List<Arguments> params = new ArrayList<>();

        List<String> inputs = Arrays.asList("", "CC", "41FB", "1F877C", "C1ECFDFC",
            "9F2FCC7C90DE090D6B87CD7E9718C1EA6CB21118FC2D5DE9F97E5DB6AC1E9C10",
            "ABCD,12,4567");

        List<String> sha3_256Result = Arrays
            .asList("A7FFC6F8BF1ED76651C14756A061D662F580FF4DE43B49FA82D80A4B80F8434A",
                "677035391CD3701293D385F037BA32796252BB7CE180B00B582DD9B20AAAD7F0",
                "39F31B6E653DFCD9CAED2602FD87F61B6254F581312FB6EEEC4D7148FA2E72AA",
                "BC22345E4BD3F792A341CF18AC0789F1C9C966712A501B19D1B6632CCD408EC5",
                "C5859BE82560CC8789133F7C834A6EE628E351E504E601E8059A0667FF62C124",
                "2F1A5F7159E34EA19CDDC70EBF9B81F1A66DB40615D7EAD3CC1F1B954D82A3AF",
                "427BC15219F70D86BAEDDC453D463C45E1F8C128872305778D6C7B0A6279AE9C");

        List<String> sha3_512Result = Arrays
            .asList(
                "A69F73CCA23A9AC5C8B567DC185A756E97C982164FE25859E0D1DCC1475C80A615B2123AF1F5F94C11E3E9402C3AC558F500199D95B6D3E301758586281DCD26",
                "3939FCC8B57B63612542DA31A834E5DCC36E2EE0F652AC72E02624FA2E5ADEECC7DD6BB3580224B4D6138706FC6E80597B528051230B00621CC2B22999EAA205",
                "AA092865A40694D91754DBC767B5202C546E226877147A95CB8B4C8F8709FE8CD6905256B089DA37896EA5CA19D2CD9AB94C7192FC39F7CD4D598975A3013C69",
                "CB20DCF54955F8091111688BECCEF48C1A2F0D0608C3A575163751F002DB30F40F2F671834B22D208591CFAF1F5ECFE43C49863A53B3225BDFD7C6591BA7658B",
                "D4B4BDFEF56B821D36F4F70AB0D231B8D0C9134638FD54C46309D14FADA92A2840186EED5415AD7CF3969BDFBF2DAF8CCA76ABFE549BE6578C6F4143617A4F1A",
                "B087C90421AEBF87911647DE9D465CBDA166B672EC47CCD4054A7135A1EF885E7903B52C3F2C3FE722B1C169297A91B82428956A02C631A2240F12162C7BC726",
                "BD4DA7781E35E2D404CA8F16D9DEDA11BD5B1CA1A433520546F921B4A58D5E92C763AACB914238575811B4D23D6200E1DBEA90D8F5C605D32796C3D88C800ADF");

        List<String> sha512Result = Arrays
            .asList(
                "CF83E1357EEFB8BDF1542850D66D8007D620E4050B5715DC83F4A921D36CE9CE47D0D13C5D85F2B0FF8318D2877EEC2F63B931BD47417A81A538327AF927DA3E",
                "62B9F8C512899CCCAD9AFB9F5AF8AE591F36E2B0588FD02510735EB543FCD5167F5058F468EC3CFB56CC4CFBBD43BDA37F3DBF2496E5895139D15A70367AB9F0",
                "F32AFB62ADEF2F4579456D2DBDA268897737C6B0185C0858BE369923E8AD40C15F9D3691837B49278DE2BAAA46DF77EEA8F3E713A7466CFD580DA9D28C73F283",
                "45BCC9D1F340CDD119FA1AFCC4F7AC657FA2D0BCA5852498EEE9C9F02F93EB2B1350E1C9567F6F18CCC5576D36812F686F31C26E2EFA6BBEE9FC7F5F5FFF7FA9",
                "0068125B83FBA7690978D5B591D5E7644BBD7ADF8011DD592D44F269DC31B3873136121872B2C8FAE70E2614266BF46ABB374006FE82CECEDFAD2644FECF140A",
                "FF6EBF72E7E9BC05E06A3DDBAC4298B68DCF50374BD74E910977A496F41270931268FABB3774B73EEC64E5D729C75D0887112E2FAD4DFA7DCEB8D1D97A3DFE44",
                "5D91F6ADB029258D66EAA67451ED4E3CAD25EC1D2D62627B0575A1DAB3A7757FD9ED02B147B27CB877B3DEE7487A095905AEA1FDB1375C30B102BCF90C6EC646");

        List<String> hash256Result = Arrays
            .asList(
                "CD372FB85148700FA88095E3492D3F9F5BEB43E555E5FF26D95F5A6ADC36F8E6",
                "806EAE44F2998AE28D879047978FC087F15D9D2E1BA720AAEAC5928210C5D741",
                "85AC9C0B89553B5CCF181AE775E7801DB8C63D4E965FA37ED780EB48CB85AA84",
                "505349EA8F166FB61E179ECC2CEA5E1A611F51C66B2961893C6502A9034171E1",
                "AFFAE7F99FD179B41248BF9E670C3FE2B532E9EC3603EB1ADAD74F8EA473B39B",
                "349640C30D9A25FD380DC895E182F6888CEAF14E71F215E1D6830373ECC1A8AE",
                "8DCC7640CD110C987F82DDD6703B8F80C129B7F2D37D4F164A09B16B034FA8D2");

        params.addAll(IntStream.range(0, inputs.size())
            .mapToObj(
                i -> Arguments.of(namedHasher(Hashes::sha3_256, "sha3_256"), inputs.get(i),
                    sha3_256Result.get(i)))
            .collect(Collectors.toList()));

        params.addAll(IntStream.range(0, inputs.size())
            .mapToObj(
                i -> Arguments.of(namedHasher(Hashes::sha3_512, "sha3_512"), inputs.get(i),
                    sha3_512Result.get(i)))
            .collect(Collectors.toList()));

        params.addAll(IntStream.range(0, inputs.size())
            .mapToObj(
                i -> Arguments
                    .of(namedHasher(Hashes::sha512, "sha512"), inputs.get(i), sha512Result.get(i)))
            .collect(Collectors.toList()));

        params.addAll(IntStream.range(0, inputs.size())
            .mapToObj(
                i -> Arguments
                    .of(namedHasher(Hashes::hash256, "hash256"), inputs.get(i),
                        hash256Result.get(i)))
            .collect(Collectors.toList()));

        return params.stream();
    }

    private static Hasher namedHasher(Hasher delegate, String name) {
        return new Hasher() {
            @Override
            public byte[] hash(byte[]... inputs) {
                return delegate.hash(inputs);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }


    @ParameterizedTest
    @MethodSource("testHashersValues")
    void testHashers(Hasher hasher, String input, String expectedHash) {
        String hash = basicHash(hasher, input);
        Assertions.assertEquals(expectedHash.toUpperCase(), hash.toUpperCase());
    }

    String basicHash(Hasher hasher, String values) {
        byte[][] inputs = Arrays.stream(values.split(",")).map(ConvertUtils::fromHexToBytes)
            .toArray(this::create);
        return ConvertUtils.toHex(hasher.hash(inputs));
    }

    @Test
     void SHA256Hash() {
        Assertions.assertEquals("E618ACB2558E1721492E4AE3BED3F4D86F26C2B0CE6AD939943A6A540855D23F",
            ConvertUtils.toHex(Hashes.sha256ForSharedKey("string-or-buffer".getBytes())).toUpperCase());
    }

    byte[][] create(int size) {
        return new byte[size][];
    }
}
