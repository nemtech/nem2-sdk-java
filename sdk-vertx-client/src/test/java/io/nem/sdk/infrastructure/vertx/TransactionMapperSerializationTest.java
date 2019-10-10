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

package io.nem.sdk.infrastructure.vertx;

import io.nem.sdk.api.JsonSerialization;
import io.nem.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.vertx.core.json.Json;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * This class tests how open api json transactions are serialized from and to models.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionMapperSerializationTest {


    private final JsonHelper jsonHelper = new JsonHelperJackson2(
        JsonHelperJackson2.configureMapper(Json.mapper));

    private final GeneralTransactionMapper transactionMapper = new GeneralTransactionMapper(
        jsonHelper);


    private static List<String> transactionJsonFiles() {
        return Arrays.stream(getResourceFolderFiles("json")).map(File::getName)
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

        String json = TestHelperVertx.loadResource(jsonFilename);

        TransactionInfoDTO originalTransactionInfo = jsonHelper
            .parse(json, TransactionInfoDTO.class);

        Transaction transactionModel = transactionMapper.map(originalTransactionInfo);
        Assertions.assertNotNull(transactionModel);

        TransactionInfoDTO mappedTransactionInfo = transactionMapper.map(transactionModel);

        Assertions.assertEquals(jsonHelper.prettyPrint(originalTransactionInfo),
            jsonHelper.prettyPrint(mappedTransactionInfo));

    }


}
