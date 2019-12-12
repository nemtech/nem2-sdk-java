/*
 * Copyright 2019 NEM
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

package io.nem.sdk.infrastructure;

import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.MetadataTransactionService;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.NamespaceMetadataTransaction;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Integration tests around namespace metadata service.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NamespaceMetadataServiceIntegrationTest extends BaseIntegrationTest {

    private Account signerAccount = config().getDefaultAccount();

    private Account targetAccount = config().getDefaultAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void setAndUpdateNamespaceMetadata(RepositoryType type) throws InterruptedException {

        NamespaceId targetNamespaceId = super
            .createRootNamespace(type, signerAccount,
                "namespace-id-metadata-service-integration-test");

        BigInteger key = BigInteger.valueOf(RandomUtils.generateRandomInt(100000));

        String originalMessage = "The original message";
        String newMessage = "The new Message";

        RepositoryFactory repositoryFactory = getRepositoryFactory(type);
        MetadataRepository metadataRepository = repositoryFactory.createMetadataRepository();

        MetadataTransactionService service = new MetadataTransactionServiceImpl(
            repositoryFactory);

        NamespaceMetadataTransaction originalTransaction = get(service
            .createNamespaceMetadataTransactionFactory(
                targetAccount.getPublicAccount(), key, originalMessage,
                signerAccount.getPublicAccount().getPublicKey(), targetNamespaceId))
            .maxFee(this.maxFee).build();

        announceAggregateAndValidate(type, originalTransaction, signerAccount);

        sleep(2000);

        assertMetadata(targetNamespaceId, key, originalMessage, metadataRepository);

        NamespaceMetadataTransaction updateTransaction = get(service
            .createNamespaceMetadataTransactionFactory(
                targetAccount.getPublicAccount(), key, newMessage,
                signerAccount.getPublicAccount().getPublicKey(), targetNamespaceId))
            .maxFee(this.maxFee).build();

        announceAggregateAndValidate(type, updateTransaction, signerAccount);

        sleep(2000);

        assertMetadata(targetNamespaceId, key, newMessage, metadataRepository);

    }

    private void assertMetadata(NamespaceId targetNamespaceId, BigInteger key,
        String value,
        MetadataRepository metadataRepository) {
        Metadata originalMetadata = get(metadataRepository
            .getNamespaceMetadataByKeyAndSender(targetNamespaceId, key,
                signerAccount.getPublicAccount().getPublicKey().toHex()));

        Assertions.assertEquals(value, originalMetadata.getMetadataEntry().getValue());
    }

}
