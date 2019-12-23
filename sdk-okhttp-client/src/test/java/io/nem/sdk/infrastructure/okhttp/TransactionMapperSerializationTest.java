/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp;

import io.nem.sdk.api.BinarySerialization;
import io.nem.sdk.infrastructure.BinarySerializationImpl;
import io.nem.sdk.infrastructure.okhttp.mappers.GeneralTransactionMapper;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * This class tests how open api json transactions are serialized from and to models.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionMapperSerializationTest {

    private final JsonHelper jsonHelper = new JsonHelperGson();

    private final GeneralTransactionMapper transactionMapper = new GeneralTransactionMapper(
        jsonHelper);

    private static List<String> transactionJsonFiles() {
        return Arrays.stream(getResourceFolderFiles("json"))
            .filter(f -> f.getName().contains("Transaction")).map(File::getName)
            .collect(Collectors.toList());
    }

    private static File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }

    @ParameterizedTest
    @MethodSource("transactionJsonFiles")
    void testDtoToModelMapping(String jsonFilename) {

        String json = TestHelperOkHttp.loadResource(jsonFilename);

        TransactionInfoDTO originalTransactionInfo = jsonHelper
            .parse(json, TransactionInfoDTO.class);

        Transaction transactionModel = transactionMapper.map(originalTransactionInfo);
        Assertions.assertNotNull(transactionModel);

        TransactionInfoDTO mappedTransactionInfo = transactionMapper.map(transactionModel);

        //Patching the sort
        mappedTransactionInfo
            .setTransaction(jsonHelper.convert(mappedTransactionInfo.getTransaction(),
                Map.class));

        Assertions.assertEquals(jsonHelper.prettyPrint(originalTransactionInfo),
            jsonHelper.prettyPrint(mappedTransactionInfo));

        BinarySerialization serialization = new BinarySerializationImpl();
        Assertions.assertEquals(Hex.toHexString(serialization.serialize(transactionModel)),
            Hex.toHexString(serialization.serialize(transactionMapper.map(mappedTransactionInfo))));

        originalTransactionInfo.setMeta(null);
        Map<String, Object> transactionJson = (Map<String, Object>) originalTransactionInfo
            .getTransaction();
        if (transactionJson.containsKey("transactions")) {
            List<Map<String, Object>> transactionsJson = (List<Map<String, Object>>) transactionJson
                .get("transactions");
            transactionsJson.forEach(t -> t.remove("meta"));
        }


    }

}
