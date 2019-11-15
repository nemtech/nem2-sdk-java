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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.blockchain.MerkelProofInfo;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.openapi.okhttp_gson.model.MerklePathItemDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MerkleProofInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.ResolutionStatementBodyDTO;
import io.nem.sdk.openapi.okhttp_gson.model.ResolutionStatementDTO;
import io.nem.sdk.openapi.okhttp_gson.model.StatementsDTO;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link NetworkRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class ReceiptRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private ReceiptRepositoryOkHttpImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new ReceiptRepositoryOkHttpImpl(apiClientMock, networkTypeObservable);
    }

    @Test
    public void shouldGetReceiptReceipts() throws Exception {

        StatementsDTO dto = new StatementsDTO();
        ResolutionStatementDTO addressResolutionStatement = new ResolutionStatementDTO();

        ResolutionStatementBodyDTO statement1 = new ResolutionStatementBodyDTO();
        addressResolutionStatement.setStatement(statement1);
        statement1.setUnresolved("9050b9837efab4bbe8a4b9bb32d812f9885c00d8fc1650e142");
        statement1.setHeight(BigInteger.valueOf(6L));
        dto.setAddressResolutionStatements(Collections.singletonList(addressResolutionStatement));

        ResolutionStatementBodyDTO statement2 = new ResolutionStatementBodyDTO();
        ResolutionStatementDTO mosaicResolutionStatement = new ResolutionStatementDTO();
        mosaicResolutionStatement.setStatement(statement2);
        statement2.setUnresolved("9");
        statement2.setHeight(BigInteger.valueOf(7L));
        dto.setMosaicResolutionStatements(Collections.singletonList(mosaicResolutionStatement));

        mockRemoteCall(dto);

        BigInteger height = BigInteger.valueOf(10L);
        Statement info = repository.getBlockReceipts(height).toFuture().get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(1, info.getAddressResolutionStatements().size());
        Assertions.assertEquals(BigInteger.valueOf(6L),
            info.getAddressResolutionStatements().get(0).getHeight());
        Assertions.assertEquals(
            MapperUtils.toAddressFromRawAddress("SBILTA367K2LX2FEXG5TFWAS7GEFYAGY7QLFBYKC"),
            info.getAddressResolutionStatements().get(0).getUnresolved());

        Assertions.assertEquals(1, info.getMosaicResolutionStatement().size());
        Assertions.assertEquals(BigInteger.valueOf(7L),
            info.getMosaicResolutionStatement().get(0).getHeight());
        Assertions.assertEquals(BigInteger.valueOf(9L),
            info.getMosaicResolutionStatement().get(0).getUnresolved().getId());

    }


    @Test
    public void shouldGetMerkleReceipts() throws Exception {
        MerkleProofInfoDTO merkleProofInfoDTO = new MerkleProofInfoDTO();
        MerklePathItemDTO marklePathItem = new MerklePathItemDTO();
        marklePathItem.setHash("SomeHash");
        marklePathItem.setPosition(123);
        merkleProofInfoDTO.setMerklePath(Collections.singletonList(marklePathItem));

        mockRemoteCall(merkleProofInfoDTO);

        BigInteger height = BigInteger.valueOf(10L);
        MerkelProofInfo info = repository.getMerkleReceipts(height, "AnotherHash").toFuture()
            .get();

        Assertions.assertNotNull(info);

        Assertions.assertEquals(1, info.getMerklePath().size());
        Assertions.assertEquals(marklePathItem.getHash(), info.getMerklePath().get(0).getHash());
        Assertions
            .assertEquals(marklePathItem.getPosition(), info.getMerklePath().get(0).getPosition());

    }

    @Override
    public ReceiptRepositoryOkHttpImpl getRepository() {
        return repository;
    }
}
