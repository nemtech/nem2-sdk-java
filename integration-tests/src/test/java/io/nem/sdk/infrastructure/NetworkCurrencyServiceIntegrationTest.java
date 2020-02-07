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

package io.nem.sdk.infrastructure;

import io.nem.sdk.api.NetworkCurrencyService;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.NetworkCurrency;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NetworkCurrencyServiceIntegrationTest extends BaseIntegrationTest {


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNetworkCurrencies(RepositoryType type) {

        RepositoryFactory repositoryFactory = getRepositoryFactory(type);
        NetworkCurrencyService service = new NetworkCurrencyServiceImpl(repositoryFactory);

        List<NetworkCurrency> networkCurrencies = get(
            service.getNetworkCurrenciesFromNemesis());

        System.out.println(toJson(networkCurrencies));

        Assertions.assertTrue(networkCurrencies.size() > 0);
        Assertions.assertTrue(networkCurrencies.size() < 3);

        Assertions
            .assertTrue(networkCurrencies.contains(get(repositoryFactory.getNetworkCurrency())));
        Assertions
            .assertTrue(networkCurrencies.contains(get(repositoryFactory.getHarvestCurrency())));

        networkCurrencies.forEach(networkCurrency -> {

            Assertions.assertTrue(networkCurrency.getMosaicId().isPresent());
            Assertions.assertTrue(networkCurrency.getNamespaceId().isPresent());
            NetworkCurrency loadedFromMosaicId = get(
                service.getNetworkCurrencyFromMosaicId(networkCurrency.getMosaicId().get()));

            Assertions.assertEquals(toJson(loadedFromMosaicId), toJson(networkCurrency));
            Assertions.assertEquals(loadedFromMosaicId, networkCurrency);

            NetworkCurrency loadedFromNamespaceId = get(
                service.getNetworkCurrencyFromNamespaceId(networkCurrency.getNamespaceId().get()));

            Assertions.assertEquals(toJson(loadedFromNamespaceId), toJson(networkCurrency));
            Assertions.assertEquals(loadedFromNamespaceId, networkCurrency);
        });


    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void testNetworkCurrencyInvalidNamespaceId(RepositoryType type) {
        RepositoryFactory repositoryFactory = getRepositoryFactory(type);
        NetworkCurrencyService service = new NetworkCurrencyServiceImpl(repositoryFactory);

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> {
                get(service
                    .getNetworkCurrencyFromNamespaceId(
                        NamespaceId.createFromName("invalid.currency")));
            });

        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id 'c1df8a076d934a50'",
            exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void testNetworkCurrencyInvaliMosaicId(RepositoryType type) {
        RepositoryFactory repositoryFactory = getRepositoryFactory(type);
        NetworkCurrencyService service = new NetworkCurrencyServiceImpl(repositoryFactory);

        RepositoryCallException exception = Assertions
            .assertThrows(RepositoryCallException.class, () -> {
                get(service
                    .getNetworkCurrencyFromMosaicId(
                        new MosaicId(BigInteger.TEN)));
            });

        Assertions.assertEquals(
            "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '000000000000000a'",
            exception.getMessage());

    }


}
