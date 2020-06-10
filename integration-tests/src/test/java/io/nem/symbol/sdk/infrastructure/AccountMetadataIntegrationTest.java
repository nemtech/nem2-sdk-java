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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Integration tests around account metadata.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountMetadataIntegrationTest extends BaseIntegrationTest {

    private Account testAccount = config().getTestAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void addMetadataToAccount(RepositoryType type) {
        BigInteger key = BigInteger.valueOf(RandomUtils.generateRandomInt(100000));

        String message = "This is the message for this account! 汉字" + key;
        System.out.println(
            "Storing message '" + message + "' in account metadata " + testAccount.getAddress()
                .plain());

        AccountMetadataTransaction transaction =
            AccountMetadataTransactionFactory.create(
                getNetworkType(), testAccount.getAddress(),
                key,
                message
            ).maxFee(this.maxFee).build();

        AccountMetadataTransaction processedTransaction = announceAggregateAndValidate(type,
            transaction, testAccount).getLeft();

        Assertions.assertEquals(transaction.getValueSizeDelta(),
            processedTransaction.getValueSizeDelta());

        Assertions.assertEquals(transaction.getScopedMetadataKey(),
            processedTransaction.getScopedMetadataKey());

        sleep(1000);

        Metadata metadata = assertMetadata(transaction,
            get(getRepositoryFactory(type).createMetadataRepository()
                .getAccountMetadata(testAccount.getAddress(),
                    Optional.empty())));

        assertMetadata(transaction, get(getRepositoryFactory(type).createMetadataRepository()
            .getAccountMetadataByKey(testAccount.getAddress(),
                metadata.getMetadataEntry().getScopedMetadataKey())));

        assertMetadata(transaction,
            Collections.singletonList(get(getRepositoryFactory(type).createMetadataRepository()
                .getAccountMetadataByKeyAndSender(testAccount.getAddress(), key,
                    testAccount.getAddress()))));

        Assertions.assertEquals(message, processedTransaction.getValue());
    }


    private Metadata assertMetadata(AccountMetadataTransaction transaction,
        List<Metadata> metadata) {

        Optional<Metadata> endpointMetadata = metadata.stream().filter(
            m -> m.getMetadataEntry().getScopedMetadataKey()
                .equals(transaction.getScopedMetadataKey()) &&
                m.getMetadataEntry().getMetadataType()
                    .equals(MetadataType.ACCOUNT) &&
                m.getMetadataEntry()
                    .getTargetAddress().equals(testAccount.getAddress())).findFirst();

        Assertions.assertTrue(endpointMetadata.isPresent());
        Assertions.assertEquals(transaction.getValue(),
            endpointMetadata.get().getMetadataEntry().getValue());
        return endpointMetadata.get();
    }
}
