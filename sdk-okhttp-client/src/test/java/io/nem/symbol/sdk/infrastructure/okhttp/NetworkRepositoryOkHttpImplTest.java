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

import com.google.gson.JsonObject;
import io.nem.symbol.sdk.model.network.NetworkConfiguration;
import io.nem.symbol.sdk.model.network.NetworkInfo;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.network.RentalFees;
import io.nem.symbol.sdk.model.network.TransactionFees;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NetworkConfigurationDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NetworkTypeDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NodeInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.RentalFeesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionFeesDTO;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link NetworkRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class NetworkRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private NetworkRepositoryOkHttpImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new NetworkRepositoryOkHttpImpl(apiClientMock);
    }

    @Test
    void shouldGetNetworkType() throws Exception {

        NodeInfoDTO dto = new NodeInfoDTO();
        dto.setNetworkIdentifier(NetworkType.MIJIN_TEST.getValue());

        mockRemoteCall(dto);

        NetworkType info = repository.getNetworkType().toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(NetworkType.MIJIN_TEST, info);

    }

    @Test
    void shouldGetNetworkInfo() throws Exception {

        NetworkTypeDTO networkTypeDTO = new NetworkTypeDTO();
        networkTypeDTO.setName("mijinTest");
        networkTypeDTO.setDescription("some description");

        mockRemoteCall(networkTypeDTO);

        NetworkInfo info = repository.getNetworkInfo().toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals("mijinTest", info.getName());
        Assertions.assertEquals("some description", info.getDescription());

    }

    @Test
    void getTransactionFees() throws Exception {

        TransactionFeesDTO dto = new TransactionFeesDTO();
        dto.setAverageFeeMultiplier(1);
        dto.setMedianFeeMultiplier(2);
        dto.setLowestFeeMultiplier(3);
        dto.setHighestFeeMultiplier(4);

        mockRemoteCall(dto);

        TransactionFees info = repository.getTransactionFees().toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(dto.getAverageFeeMultiplier(), info.getAverageFeeMultiplier());
        Assertions.assertEquals(dto.getMedianFeeMultiplier(), info.getMedianFeeMultiplier());
        Assertions.assertEquals(dto.getLowestFeeMultiplier(), info.getLowestFeeMultiplier());
        Assertions.assertEquals(dto.getHighestFeeMultiplier(), info.getHighestFeeMultiplier());

    }

    @Test
    void getRentalFees() throws Exception {

        RentalFeesDTO dto = new RentalFeesDTO();
        dto.setEffectiveChildNamespaceRentalFee(BigInteger.valueOf(1));
        dto.setEffectiveMosaicRentalFee(BigInteger.valueOf(2));
        dto.setEffectiveRootNamespaceRentalFeePerBlock(BigInteger.valueOf(3));

        mockRemoteCall(dto);

        RentalFees info = repository.getRentalFees().toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(dto.getEffectiveChildNamespaceRentalFee(),
            info.getEffectiveChildNamespaceRentalFee());
        Assertions
            .assertEquals(dto.getEffectiveMosaicRentalFee(), info.getEffectiveMosaicRentalFee());
        Assertions.assertEquals(dto.getEffectiveRootNamespaceRentalFeePerBlock(),
            info.getEffectiveRootNamespaceRentalFeePerBlock());

    }

    @Test
    void getNetworkProperties() throws Exception {

        NetworkConfigurationDTO dto = TestHelperOkHttp
            .loadResource("network-configuration.json", NetworkConfigurationDTO.class);
        Assertions.assertNotNull(dto);

        JsonObject plain = TestHelperOkHttp
            .loadResource("network-configuration.json", JsonObject.class);
        Assertions.assertNotNull(plain);

        Assertions.assertEquals(jsonHelper.prettyPrint(dto), jsonHelper.prettyPrint(plain));

        mockRemoteCall(dto);

        NetworkConfiguration configuration = repository.getNetworkProperties().toFuture().get();

        Assertions.assertNotNull(configuration);

        plain.get("network").getAsJsonObject().addProperty("nodeEqualityStrategy", "PUBLIC_KEY");
        Assertions
            .assertEquals(jsonHelper.prettyPrint(plain), jsonHelper.prettyPrint(configuration));

    }

    @Override
    public NetworkRepositoryOkHttpImpl getRepository() {
        return repository;
    }
}
