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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.api.NetworkRepository;
import io.nem.symbol.sdk.model.blockchain.NetworkFees;
import io.nem.symbol.sdk.model.blockchain.NetworkInfo;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NetworkRepositoryIntegrationTest extends BaseIntegrationTest {


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void testNetworkType(RepositoryType type) {
        NetworkType networkType = get(getNetworkRepository(type).getNetworkType());
        assertEquals(getNetworkType(), networkType);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNetworkInfo(RepositoryType type) {
        NetworkInfo networkInfo = get(getNetworkRepository(type).getNetworkInfo());
        assertEquals(getNetworkType().getNetworkName(), networkInfo.getName());
        Assertions.assertNotNull(networkInfo.getDescription());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getNetworkFees(RepositoryType type) {
        NetworkFees networkInfo = get(getNetworkRepository(type).getNetworkFees());
        Assertions.assertNotNull(networkInfo.getAverageFeeMultiplier());
        Assertions.assertNotNull(networkInfo.getHighestFeeMultiplier());
        Assertions.assertNotNull(networkInfo.getLowestFeeMultiplier());
        Assertions.assertNotNull(networkInfo.getMedianFeeMultiplier());
        System.out.println(jsonHelper().prettyPrint(networkInfo));
    }

    private NetworkRepository getNetworkRepository(RepositoryType type) {
        return getRepositoryFactory(type).createNetworkRepository();
    }
}
