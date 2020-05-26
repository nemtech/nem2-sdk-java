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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoExtendedDTO;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Assertions;

/**
 * Helper class for the vertx tests.
 */
public class TestHelperVertx {

    private static JsonHelper jsonHelper;

    static {
        jsonHelper = new JsonHelperJackson2(JsonHelperJackson2.configureMapper(new ObjectMapper()));
    }

    private TestHelperVertx() {
    }

    public static TransactionInfoExtendedDTO loadTransactionInfoExtendedDTO(String name) {
        String resourceName = "TransactionMapping-" + name;
        return loadResource(resourceName, TransactionInfoExtendedDTO.class);
    }

    public static TransactionInfoDTO loadTransactionInfoDTO(String name) {
        return loadTransactionInfoDTO(name, TransactionInfoDTO.class);
    }

    public static <T> T  loadTransactionInfoDTO(String name, Class<T> clazz) {
        String resourceName = "transaction-" + name;
        return loadResource(resourceName, clazz);
    }

    public static <T> T loadResource(String resourceName, Class<T> clazz) {
        return jsonHelper.parse(loadResource(resourceName), clazz);
    }

    public static String loadResource(String resourceName) {

        String resName = "json/" + resourceName;
        Assertions.assertNotNull(TestHelperVertx.class.getClassLoader()
            .getResourceAsStream(resName), resName + " not found in classpath");
        try (InputStream resourceAsStream = TestHelperVertx.class.getClassLoader()
            .getResourceAsStream(resName)) {
            return IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(
                "Cannot open resource " + resName + ". Error: " + ExceptionUtils.getMessage(e),
                e);
        }
    }
}
