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

package io.nem.sdk.infrastructure.vertx;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.openapi.vertx.model.StatementsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReceiptMappingVertxTest {

    private final JsonHelper jsonHelper = new JsonHelperJackson2();

    private NetworkType networkType = NetworkType.MIJIN_TEST;

    @Test
    public void getMosaicResolutionStatementHash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("99381CE398D3AAE110FC97E984D7D35A710A5C525A4F959EC8916B382DE78A63",
                statement.getMosaicResolutionStatement().get(0).generateHash(networkType));
    }

    @Test
    public void getTransactionStatementsHash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("78E5F66EC55D1331646528F9BF7EC247C68F58E651223E7F05CBD4FBF0BF88FA",
                statement.getTransactionStatements().get(0).generateHash().toUpperCase());
    }

    @Test
    public void getAddressResolutionStatementsHash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("6967470641BC527768CDC29998F4A3350813FDF2E40D1C97AB0BBA36B9AF649E",
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
