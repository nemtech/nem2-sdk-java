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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.BinarySerialization;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.vertx.core.json.Json;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** This class tests how open api json transactions are serialized from and to models. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionMapperSerializationVertxTest {

  private final JsonHelper jsonHelper =
      new JsonHelperJackson2(JsonHelperJackson2.configureMapper(Json.mapper));

  private final GeneralTransactionMapper transactionMapper =
      new GeneralTransactionMapper(jsonHelper);

  private static List<String> transactionJsonFiles() {
    return Arrays.stream(getResourceFolderFiles("json"))
        .filter(f -> f.getName().startsWith("transaction-"))
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
  @MethodSource("transactionJsonFiles")
  void testDtoToModelMapping(String jsonFilename) {

    String json = TestHelperVertx.loadResource(jsonFilename);

    TransactionInfoDTO originalTransactionInfo = jsonHelper.parse(json, TransactionInfoDTO.class);

    Transaction transactionModel = transactionMapper.mapFromDto(originalTransactionInfo);
    Assertions.assertNotNull(transactionModel);

    TransactionInfoDTO mappedTransactionInfo =
        (TransactionInfoDTO) transactionMapper.mapToDto(transactionModel);

    Map<String, Object> transactionMap =
        jsonHelper.convert(mappedTransactionInfo.getTransaction(), Map.class);

    Map<String, Object> originalTransactionMap =
        jsonHelper.convert(originalTransactionInfo.getTransaction(), Map.class);
    originalTransactionMap.put("size", transactionModel.getSize());
    originalTransactionInfo.setTransaction(originalTransactionMap);

    // Patching the sort
    mappedTransactionInfo.setTransaction(transactionMap);

    mappedTransactionInfo.setMeta(jsonHelper.convert(mappedTransactionInfo.getMeta(), Map.class));

    Assertions.assertEquals(
        jsonHelper.prettyPrint(originalTransactionInfo),
        jsonHelper.prettyPrint(mappedTransactionInfo));

    BinarySerialization serialization = new BinarySerializationImpl();
    Transaction transaction = transactionMapper.mapFromDto(mappedTransactionInfo);
    byte[] serialize1 = serialization.serialize(transactionModel);
    byte[] serialize2 = serialization.serialize(transaction);

    Assertions.assertEquals(ConvertUtils.toHex(serialize1), ConvertUtils.toHex(serialize2));

    removeMeta(originalTransactionInfo);

    byte[] serialize3 = serialization.serialize(transactionModel);
    Assertions.assertEquals(ConvertUtils.toHex(serialize1), ConvertUtils.toHex(serialize3));

    Transaction deserialized3 = serialization.deserialize(serialize3);

    TransactionInfoDTO deserializedTransaction =
        (TransactionInfoDTO) transactionMapper.mapToDto(deserialized3, false);

    deserializedTransaction.setTransaction(
        jsonHelper.convert(deserializedTransaction.getTransaction(), Map.class));

    removeMeta(deserializedTransaction);
    Assertions.assertEquals(
        jsonHelper.prettyPrint(originalTransactionInfo),
        jsonHelper.prettyPrint(deserializedTransaction));
  }

  private void removeMeta(TransactionInfoDTO originalTransactionInfo) {
    originalTransactionInfo.setMeta(null);
    originalTransactionInfo.setId(null);
    Map<String, Object> transactionJson =
        (Map<String, Object>) originalTransactionInfo.getTransaction();
    if (transactionJson.containsKey("transactions")) {
      List<Map<String, Object>> transactionsJson =
          (List<Map<String, Object>>) transactionJson.get("transactions");
      transactionsJson.forEach(t -> t.remove("meta"));
      transactionsJson.forEach(t -> t.remove("id"));
    }
  }
}
