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

package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.RepositoryCallException;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Integration tests of MetadataRepository
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetadataRepositoryIntegrationTest extends BaseIntegrationTest {


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenAccountMetadataDoesNotExist(RepositoryType type) {
        BigInteger metadataKey = BigInteger.valueOf(100000);
        MetadataRepository metadataRepository = this.getRepositoryFactory(type)
            .createMetadataRepository();
        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> get(metadataRepository
                .getAccountMetadataByKeyAndSender(getRecipient(), metadataKey,
                    getTestAccount().getAddress())));
        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '152,2,245,29,21,107,94,139,109,154,112,80,158,70,82,43,162,79,185,29,57,232,10,95'",
            exception.getMessage());
        Assertions.assertEquals(404, exception.getStatusCode());
    }
}
