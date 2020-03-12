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

import io.nem.symbol.sdk.model.receipt.ReceiptType;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.receipt.Statement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.vertx.model.StatementsDTO;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReceiptMappingVertxTest {

    private final JsonHelper jsonHelper = new JsonHelperJackson2();

    private NetworkType networkType = NetworkType.MIJIN_TEST;

    @Test
    public void getMosaicResolutionStatementHash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("DE29FB6356530E5D1FBEE0A84202520C155D882C46EA74456752D6C75F0707B3",
                statement.getMosaicResolutionStatement().get(0).generateHash(networkType));
    }

    @Test
    public void getTransactionStatementshash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("78E5F66EC55D1331646528F9BF7EC247C68F58E651223E7F05CBD4FBF0BF88FA",
                statement.getTransactionStatements().get(0).generateHash().toUpperCase());
    }

    @Test
    public void getTransactionStatements2Recipients() {
        Statement statement = getStatement();
        TransactionStatement transactionStatement = statement.getTransactionStatements().get(1);
        Assertions.assertEquals("450C393FD6D1915538194943D3417A82C8428F76C222D645E35C7396F63CE641",
            transactionStatement.generateHash().toUpperCase());

        Assertions.assertEquals(5, transactionStatement.getReceipts().size());
        Assertions.assertEquals(
            ReceiptType.NAMESPACE_RENTAL_FEE, transactionStatement.getReceipts().get(0).getType());
        Assertions.assertEquals(ReceiptType.MOSAIC_EXPIRED,
            transactionStatement.getReceipts().get(1).getType());
        Assertions.assertEquals(ReceiptType.NAMESPACE_EXPIRED,
            transactionStatement.getReceipts().get(2).getType());
        Assertions.assertEquals(ReceiptType.NAMESPACE_DELETED,
            transactionStatement.getReceipts().get(3).getType());
        Assertions.assertEquals(ReceiptType.INFLATION,
            transactionStatement.getReceipts().get(4).getType());
    }

    @Test
    public void createReceiptFromDtoInvalid() {
        ReceiptMappingVertx receiptMappingOkHttp = new ReceiptMappingVertx(jsonHelper);
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            receiptMappingOkHttp
                .createReceiptFromDto(Collections.singletonMap("type", 61763), networkType);
        });
        Assertions.assertEquals("Receipt type: ADDRESS_ALIAS_RESOLUTION not valid", e.getMessage());
    }


    @Test
    public void getAddressResolutionStatementsHash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("812AA120525990BE821035BC9CBCCD569F807B5338BA9D13DD63D99F3697ACCA",
                statement.getAddressResolutionStatements().get(0).generateHash(networkType));
    }

    private Statement getStatement() {
        StatementsDTO statementsDTO = TestHelperVertx
            .loadResource("Statements.json", StatementsDTO.class);
        ReceiptMappingVertx receiptMappingOkHttp = new ReceiptMappingVertx(jsonHelper);
        return receiptMappingOkHttp
            .createStatementFromDto(statementsDTO, NetworkType.MIJIN_TEST);
    }
}
