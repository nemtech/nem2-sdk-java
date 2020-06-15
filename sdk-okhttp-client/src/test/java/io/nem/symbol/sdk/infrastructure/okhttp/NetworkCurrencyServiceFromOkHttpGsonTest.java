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

import static io.nem.symbol.sdk.infrastructure.okhttp.TestHelperOkHttp.loadResource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import io.nem.symbol.sdk.api.JsonSerialization;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NetworkCurrencyService;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.TransactionRepository;
import io.nem.symbol.sdk.api.TransactionSearchCriteria;
import io.nem.symbol.sdk.infrastructure.NetworkCurrencyServiceImpl;
import io.nem.symbol.sdk.model.mosaic.NetworkCurrency;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests of {@link NetworkCurrencyServiceImpl}
 */
public class NetworkCurrencyServiceFromOkHttpGsonTest {

    private NamespaceRepository namespaceRepository;
    private TransactionRepository transactionRepository;
    private MosaicRepository mosaicRepository;
    private NetworkCurrencyService service;

    @BeforeEach
    void setup() {

        RepositoryFactory factory = mock(RepositoryFactory.class);

        namespaceRepository = mock(NamespaceRepository.class);
        when(factory.createNamespaceRepository()).thenReturn(namespaceRepository);

        transactionRepository = mock(TransactionRepository.class);
        when(factory.createTransactionRepository()).thenReturn(transactionRepository);

        mosaicRepository = mock(MosaicRepository.class);
        when(factory.createMosaicRepository()).thenReturn(mosaicRepository);

        service = new NetworkCurrencyServiceImpl(factory);
    }

    @Test
    void getBlockTransactions() throws Exception {

        String transactionJsonList = loadResource("nemesis-transactions.json");
        Gson gson = JsonHelperGson.creatGson(false);

        JsonSerialization serialization = new JsonSerializationOkHttp(gson);
        Stream<String> stream = gson.fromJson(transactionJsonList, List.class).stream().map(Object::toString);
        List<Transaction> transactions = stream.map(serialization::jsonToTransaction)
            .collect(Collectors.toList());

        TransactionSearchCriteria ctieria = new TransactionSearchCriteria(TransactionGroup.CONFIRMED).height(BigInteger.ONE)
            .pageNumber(1);

        when(transactionRepository.search(Mockito.eq(ctieria)))
            .thenReturn(Observable.just(new Page<>(transactions, 1, 1, 1, 1)));

        List<NetworkCurrency> networkCurrencies = service.getNetworkCurrenciesFromNemesis()
            .toFuture().get();

        Assertions.assertEquals(2, networkCurrencies.size());

        NetworkCurrency networkCurrency1 = networkCurrencies.get(0);
        NetworkCurrency networkCurrency2 = networkCurrencies.get(1);

        Assertions.assertEquals(networkCurrency1.getMosaicId().get(),
            networkCurrency1.getUnresolvedMosaicId());
        Assertions.assertEquals("cat.harvest",
            networkCurrency1.getNamespaceId().get().getFullName().get());

        Assertions.assertEquals(3, networkCurrency1.getDivisibility());
        Assertions.assertTrue(networkCurrency1.isSupplyMutable());
        Assertions.assertTrue(networkCurrency1.isTransferable());

        Assertions.assertEquals(networkCurrency2.getMosaicId().get(),
            networkCurrency2.getUnresolvedMosaicId());
        Assertions.assertEquals("cat.currency",
            networkCurrency2.getNamespaceId().get().getFullName().get());
        Assertions.assertFalse(networkCurrency2.isSupplyMutable());
        Assertions.assertTrue(networkCurrency2.isTransferable());
    }
}
