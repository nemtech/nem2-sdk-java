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

package io.nem.sdk.infrastructure;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.metadata.MetadataType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
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
public class MosaicMetadataIntegrationTest extends BaseIntegrationTest {

    private Account testAccount = config().getDefaultAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void addMetadataToMosaic(RepositoryType type) throws InterruptedException {

        MosaicId targetMosaicId = createMosaic(type);
        NamespaceId alias = setMosaicAlias(type, targetMosaicId,
            "mosaicalias" + targetMosaicId.getIdAsHex().toLowerCase());

        String message = "This is the message in the mosaic!";
        BigInteger key = BigInteger.TEN;
        MosaicMetadataTransaction transaction =
            MosaicMetadataTransactionFactory.create(
                getNetworkType(), testAccount.getPublicAccount(), alias,
                key, message
            ).maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory
            .createComplete(getNetworkType(),
                Collections.singletonList(transaction.toAggregate(testAccount.getPublicAccount())))
            .maxFee(this.maxFee).build();

        AggregateTransaction announceCorrectly = announceAndValidate(type, testAccount,
            aggregateTransaction);

        Assertions.assertEquals(aggregateTransaction.getType(), announceCorrectly.getType());
        Assertions
            .assertEquals(testAccount.getPublicAccount(), announceCorrectly.getSigner().get());
        Assertions.assertEquals(1, announceCorrectly.getInnerTransactions().size());
        Assertions
            .assertEquals(transaction.getType(),
                announceCorrectly.getInnerTransactions().get(0).getType());
        MosaicMetadataTransaction processedTransaction = (MosaicMetadataTransaction) announceCorrectly
            .getInnerTransactions()
            .get(0);

        Assertions.assertEquals(transaction.getTargetMosaicId(),
            processedTransaction.getTargetMosaicId());
        Assertions.assertEquals(transaction.getValueSizeDelta(),
            processedTransaction.getValueSizeDelta());

        Assertions.assertEquals(transaction.getScopedMetadataKey(),
            processedTransaction.getScopedMetadataKey());

        sleep(2000);
        List<Metadata> metadata = get(getRepositoryFactory(type).createMetadataRepository()
            .getMosaicMetadata(targetMosaicId,
                Optional.empty()));

        assertMetadata(targetMosaicId, transaction, metadata);

        assertMetadata(targetMosaicId, transaction,
            get(getRepositoryFactory(type).createMetadataRepository()
                .getMosaicMetadataByKey(targetMosaicId, key)));

        assertMetadata(targetMosaicId, transaction,
            Collections.singletonList(get(getRepositoryFactory(type).createMetadataRepository()
                .getMosaicMetadataByKeyAndSender(targetMosaicId, key,
                    testAccount.getPublicKey()))));

        assertMetadata(targetMosaicId, transaction, metadata);
        Assertions.assertEquals(message, processedTransaction.getValue());
    }


    private String assertMetadata(MosaicId targetMosaicId, MosaicMetadataTransaction transaction,
        List<Metadata> metadata) {

        Optional<Metadata> endpointMetadata = metadata.stream().filter(
            m -> m.getMetadataEntry().getScopedMetadataKey()
                .equals(transaction.getScopedMetadataKey()) &&
                m.getMetadataEntry().getMetadataType()
                    .equals(MetadataType.MOSAIC) &&
                m.getMetadataEntry()
                    .getTargetPublicKey().equals(testAccount.getPublicKey())).findFirst();

        Assertions.assertTrue(endpointMetadata.isPresent());
        System.out.println(endpointMetadata.get().getId());

        Assertions.assertEquals(targetMosaicId,
            endpointMetadata.get().getMetadataEntry().getTargetId().get());

        Assertions.assertEquals(transaction.getValue(),
            endpointMetadata.get().getMetadataEntry().getValue());
        return endpointMetadata.get().getId();
    }


    private MosaicId createMosaic(RepositoryType type) throws InterruptedException {
        return super.createMosaic(testAccount, type, BigInteger.ZERO, null);
    }
}
