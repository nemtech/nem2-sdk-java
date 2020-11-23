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
package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.catapult.builders.TransactionBuilder;
import io.nem.symbol.catapult.builders.TransactionBuilderHelper;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.BinarySerialization;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
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
public class TransactionMapperSerializationOkHttpTest {

  private final JsonHelper jsonHelper = new JsonHelperGson();

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

    String json = TestHelperOkHttp.loadResource(jsonFilename);

    TransactionInfoDTO originalTransactionInfo = jsonHelper.parse(json, TransactionInfoDTO.class);

    Transaction transactionModel = transactionMapper.mapFromDto(originalTransactionInfo);

    Integer version = jsonHelper.getInteger(originalTransactionInfo, "transaction", "version");
    Integer type = jsonHelper.getInteger(originalTransactionInfo, "transaction", "type");
    Integer network = jsonHelper.getInteger(originalTransactionInfo, "transaction", "network");
    Assertions.assertEquals(transactionModel.getVersion(), version);
    Assertions.assertEquals(transactionModel.getType().getValue(), type);
    Assertions.assertEquals(transactionModel.getNetworkType().getValue(), network);



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

    BinarySerialization serialization = new BinarySerializationImpl();
    String serialized = ConvertUtils.toHex(serialization.serialize(transactionModel));

    TransactionBuilder builder = TransactionBuilderHelper
        .loadFromBinary(SerializationUtils.toDataInput(serialization.serialize(transactionModel)));
    System.out.println(ConvertUtils.toHex(builder.serialize()));

    serialization.deserialize(serialization.serialize(transactionModel));

    Assertions.assertEquals(
        jsonHelper.prettyPrint(originalTransactionInfo),
        jsonHelper.prettyPrint(mappedTransactionInfo));

    Assertions.assertEquals(
        serialized,
        ConvertUtils.toHex(
            serialization.serialize(transactionMapper.mapFromDto(mappedTransactionInfo))));

    originalTransactionInfo.setMeta(null);
    Map<String, Object> transactionJson =
        (Map<String, Object>) originalTransactionInfo.getTransaction();
    if (transactionJson.containsKey("transactions")) {
      List<Map<String, Object>> transactionsJson =
          (List<Map<String, Object>>) transactionJson.get("transactions");
      transactionsJson.forEach(t -> t.remove("meta"));
    }
  }
}
