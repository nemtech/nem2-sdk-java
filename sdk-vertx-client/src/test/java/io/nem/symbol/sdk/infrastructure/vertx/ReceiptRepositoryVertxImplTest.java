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

package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.sdk.api.ResolutionStatementSearchCriteria;
import io.nem.symbol.sdk.api.TransactionStatementSearchCriteria;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.openapi.vertx.model.Pagination;
import io.nem.symbol.sdk.openapi.vertx.model.ResolutionStatementDTO;
import io.nem.symbol.sdk.openapi.vertx.model.ResolutionStatementInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.ResolutionStatementPage;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionStatementInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionStatementPage;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link BlockRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class ReceiptRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

    private ReceiptRepositoryVertxImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new ReceiptRepositoryVertxImpl(apiClientMock);
    }


    @Test
    public void searchReceipts() throws Exception {

        List<TransactionStatementInfoDTO> transactionStatementInfoDTOS = jsonHelper
            .parseList(TestHelperVertx.loadResource("Recipient-TransactionResolutionStatement.json"),
                TransactionStatementInfoDTO.class);

        mockRemoteCall(toPage(transactionStatementInfoDTOS));

        BigInteger height = BigInteger.valueOf(10L);
        List<TransactionStatement> transactionStatements = repository.searchReceipts(
            new TransactionStatementSearchCriteria().height(height)
                .receiptTypes(Collections.singletonList(ReceiptType.MOSAIC_ALIAS_RESOLUTION))).toFuture().get()
            .getData();

        Assertions.assertEquals(transactionStatementInfoDTOS.size(), transactionStatements.size());
        Assertions.assertEquals("82FEFFC329618ECF56B8A6FDBCFCF1BF0A4B6747AB6A5746B195CEEB810F335C",
            transactionStatements.get(0).generateHash().toUpperCase());
    }


    private TransactionStatementPage toPage(List<TransactionStatementInfoDTO> dtos) {
        return new TransactionStatementPage().data(dtos).pagination(new Pagination().pageNumber(1).pageSize(2));
    }

    @Test
    public void searchAddressResolutionStatements() throws Exception {

        ResolutionStatementInfoDTO addressResolutionStatement = new ResolutionStatementInfoDTO();
        Address address = Address.generateRandom(this.networkType);
        ResolutionStatementDTO statement1 = new ResolutionStatementDTO();
        addressResolutionStatement.setStatement(statement1);
        statement1.setUnresolved(address.encoded());
        statement1.setHeight(BigInteger.valueOf(6L));

        mockRemoteCall(toPage(addressResolutionStatement));

        BigInteger height = BigInteger.valueOf(10L);
        List<AddressResolutionStatement> addressResolutionStatements = repository
            .searchAddressResolutionStatements(new ResolutionStatementSearchCriteria().height(height)).toFuture().get()
            .getData();

        Assertions.assertEquals(1, addressResolutionStatements.size());
        Assertions.assertEquals(BigInteger.valueOf(6L), addressResolutionStatements.get(0).getHeight());
        Assertions.assertEquals(address, addressResolutionStatements.get(0).getUnresolved());
    }

    @Test
    public void searchMosaicResolutionStatements() throws Exception {

        ResolutionStatementDTO statement2 = new ResolutionStatementDTO();
        ResolutionStatementInfoDTO mosaicResolutionStatement = new ResolutionStatementInfoDTO();
        mosaicResolutionStatement.setStatement(statement2);
        statement2.setUnresolved("9");
        statement2.setHeight(BigInteger.valueOf(7L));

        mockRemoteCall(toPage(mosaicResolutionStatement));

        BigInteger height = BigInteger.valueOf(10L);
        List<MosaicResolutionStatement> mosaicResolutionStatements = repository
            .searchMosaicResolutionStatements(new ResolutionStatementSearchCriteria().height(height)).toFuture().get()
            .getData();

        Assertions.assertEquals(1, mosaicResolutionStatements.size());
        Assertions.assertEquals(BigInteger.valueOf(7L), mosaicResolutionStatements.get(0).getHeight());
        Assertions.assertEquals(BigInteger.valueOf(9L), mosaicResolutionStatements.get(0).getUnresolved().getId());

    }

    private ResolutionStatementPage toPage(ResolutionStatementInfoDTO dto) {
        return new ResolutionStatementPage().data(Collections.singletonList(dto))
            .pagination(new Pagination().pageNumber(1).pageSize(2));
    }


}
