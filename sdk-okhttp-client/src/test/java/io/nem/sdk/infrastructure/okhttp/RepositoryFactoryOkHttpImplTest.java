/*
 * Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp;

import io.nem.catapult.builders.GeneratorUtils;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.api.RepositoryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RepositoryFactoryOkHttpImpl}.
 */
public class RepositoryFactoryOkHttpImplTest {


    @Test
    public void shouldCreateRepositories() {

        String baseUrl = "https://nem.com:3000/path";

        RepositoryFactory factory = new RepositoryFactoryOkHttpImpl(
            baseUrl);
        Assertions.assertNotNull(factory.createAccountRepository());
        Assertions.assertNotNull(factory.createBlockRepository());
        Assertions.assertNotNull(factory.createReceiptRepository());
        Assertions.assertNotNull(factory.createChainRepository());
        Assertions.assertNotNull(factory.createDiagnosticRepository());
        Assertions.assertNotNull(factory.createListener());
        Assertions.assertNotNull(factory.createMosaicRepository());
        Assertions.assertNotNull(factory.createNamespaceRepository());
        Assertions.assertNotNull(factory.createNetworkRepository());
        Assertions.assertNotNull(factory.createNodeRepository());
        Assertions.assertNotNull(factory.createTransactionRepository());
        Assertions.assertNotNull(factory.createMetadataRepository());
        Assertions.assertNotNull(factory.createRestrictionAccountRepository());
        Assertions.assertNotNull(factory.createRestrictionMosaicRepository());
        Assertions.assertNotNull(factory.createJsonSerialization());
        factory.close();
        factory.close();
        factory.close();
    }

    @Test
    public void getNetworkTypeFailWhenInvalidServer() {
        String baseUrl = "https://localhost:1934/path";

        RepositoryCallException e = Assertions.assertThrows(RepositoryCallException.class,
            () -> GeneratorUtils.propagate(
                () -> new RepositoryFactoryOkHttpImpl(baseUrl).getNetworkType().toFuture().get()));

        Assertions.assertEquals(
            "ApiException: java.net.ConnectException: Failed to connect to localhost/127.0.0.1:1934 - 0",
            e.getMessage());
    }


    @Test
    public void getGenerationHashFailWhenInvalidServer() {
        String baseUrl = "https://localhost:1934/path";

        RepositoryCallException e = Assertions.assertThrows(RepositoryCallException.class,
            () -> GeneratorUtils.propagate(
                () -> new RepositoryFactoryOkHttpImpl(baseUrl).getGenerationHash().toFuture().get()));

        Assertions.assertEquals(
            "ApiException: java.net.ConnectException: Failed to connect to localhost/127.0.0.1:1934 - 0",
            e.getMessage());
    }

}
