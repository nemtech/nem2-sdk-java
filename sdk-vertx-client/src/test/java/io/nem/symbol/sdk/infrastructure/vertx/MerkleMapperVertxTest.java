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
package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.vertx.model.MerkleStateInfoDTO;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** This class tests how open api json merkle are serialized from and to models. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MerkleMapperVertxTest {

  private final JsonHelper jsonHelper = new JsonHelperJackson2();

  private static List<String> merkleJsonFiles() {
    return Arrays.stream(getResourceFolderFiles("json"))
        .filter(f -> f.getName().startsWith("merkle-"))
        .map(File::getName)
        .collect(Collectors.toList());
  }

  private static File[] getResourceFolderFiles(String folder) {
    String resName = "../sdk-core/src/test/resources/" + folder;
    File file = new File(resName);
    if (file.exists()) return file.listFiles();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(folder);
    String path = url.getPath();
    return new File(path).listFiles();
  }

  @ParameterizedTest
  @MethodSource("merkleJsonFiles")
  void testDtoToModelMapping(String jsonFilename) {
    String json = TestHelperVertx.loadResource(jsonFilename);
    MerkleMapper merkleMapper = new MerkleMapper(jsonHelper);
    MerkleStateInfoDTO dto = jsonHelper.parse(json, MerkleStateInfoDTO.class);
    MerkleStateInfo merkleStateInfo = merkleMapper.toMerkleStateInfo(dto);
    Assertions.assertFalse(merkleStateInfo.getTree().getBranches().isEmpty());
    Assertions.assertNotNull(merkleStateInfo.getTree().getLeaf());
  }
}
