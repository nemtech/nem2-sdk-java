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

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Integration tests around account metadata.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MosaicMetadataIntegrationTest extends BaseIntegrationTest {

    private Account testAccount;

    @BeforeEach
    void setup() {
        testAccount = config().getDefaultAccount();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void addMetadataToMosaic(RepositoryType type) {

        MosaicId targetMosaicId = createMosaic(type);
        NamespaceId alias = setMosaicAlias(type, targetMosaicId,
            "mosaicalias" + targetMosaicId.getIdAsHex().toLowerCase());

        String message = "This is the message in the mosaic!";
        BigInteger key = BigInteger.TEN;
        MosaicMetadataTransaction transaction = MosaicMetadataTransactionFactory
            .create(getNetworkType(), testAccount.getAddress(), alias, key, message).maxFee(this.maxFee).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory.createComplete(getNetworkType(),
            Collections.singletonList(transaction.toAggregate(testAccount.getPublicAccount()))).maxFee(this.maxFee)
            .build();

        AggregateTransaction announceCorrectly = announceAndValidate(type, testAccount, aggregateTransaction);

        Assertions.assertEquals(aggregateTransaction.getType(), announceCorrectly.getType());
        Assertions.assertEquals(testAccount.getPublicAccount(), announceCorrectly.getSigner().get());
        Assertions.assertEquals(1, announceCorrectly.getInnerTransactions().size());
        Assertions.assertEquals(transaction.getType(), announceCorrectly.getInnerTransactions().get(0).getType());
        MosaicMetadataTransaction processedTransaction = (MosaicMetadataTransaction) announceCorrectly
            .getInnerTransactions().get(0);

        Assertions.assertEquals(transaction.getTargetMosaicId(), processedTransaction.getTargetMosaicId());
        Assertions.assertEquals(transaction.getValueSizeDelta(), processedTransaction.getValueSizeDelta());

        Assertions.assertEquals(transaction.getScopedMetadataKey(), processedTransaction.getScopedMetadataKey());

        System.out.println(targetMosaicId.getIdAsHex());
        System.out.println(key);

        sleep(5000);

        List<Metadata> metadata = get(getRepositoryFactory(type).createMetadataRepository()
            .search(new MetadataSearchCriteria().targetId(targetMosaicId).metadataType(MetadataType.MOSAIC))).getData();

        assertMetadata(targetMosaicId, transaction, metadata);

        assertMetadata(targetMosaicId, transaction, get(getRepositoryFactory(type).createMetadataRepository().search(
            new MetadataSearchCriteria().targetId(targetMosaicId).metadataType(MetadataType.MOSAIC)
                .scopedMetadataKey(key))).getData());

        assertMetadata(targetMosaicId, transaction, get(getRepositoryFactory(type).createMetadataRepository().search(
            new MetadataSearchCriteria().sourceAddress(testAccount.getAddress()).targetId(targetMosaicId)
                .metadataType(MetadataType.MOSAIC).scopedMetadataKey(key))).getData());

        assertMetadata(targetMosaicId, transaction, metadata);
        Assertions.assertEquals(message, processedTransaction.getValue());
    }


    private String assertMetadata(MosaicId targetMosaicId, MosaicMetadataTransaction transaction,
        List<Metadata> metadata) {

        Optional<Metadata> endpointMetadata = metadata.stream().filter(
            m -> m.getScopedMetadataKey().equals(transaction.getScopedMetadataKey()) && m.getMetadataType()
                .equals(MetadataType.MOSAIC) && m.getTargetAddress().equals(testAccount.getAddress())).findFirst();

        Assertions.assertTrue(endpointMetadata.isPresent());

        Assertions.assertEquals(targetMosaicId, endpointMetadata.get().getTargetId().get());

        Assertions.assertEquals(transaction.getValue(), endpointMetadata.get().getValue());
        return endpointMetadata.get().getCompositeHash();
    }

    //
    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void searchIntegration(RepositoryType type) {
        BigInteger key = MapperUtils.fromHexToBigInteger("000000000000000A");
        Address sourceAddress = Address.createFromEncoded("9852D5EAA9AB038151EEBDD34308B3B2B7D82B92955F298E");
        Address targetAddress = Address.createFromEncoded("9852D5EAA9AB038151EEBDD34308B3B2B7D82B92955F298E");
        MosaicId mosaicId = new MosaicId("213CED6E6BBA6689");
        MetadataType metadataType = MetadataType.MOSAIC;

        Page<Metadata> metadataPage = get(getRepositoryFactory(type).createMetadataRepository().search(
            new MetadataSearchCriteria().scopedMetadataKey(key).targetId(mosaicId).sourceAddress(sourceAddress)
                .targetAddress(targetAddress).metadataType(metadataType)));
        System.out.println(type + "\n" + toJson(metadataPage));
    }


    private MosaicId createMosaic(RepositoryType type) {
        return super.createMosaic(testAccount, type, BigInteger.ZERO, null);
    }
}
