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

import static io.nem.sdk.infrastructure.okhttp.TestHelperOkHttp.loadResource;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.openapi.okhttp_gson.model.StatementsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReceiptMappingOkHttpTest {

    private final JsonHelper jsonHelper = new JsonHelperGson();

    private NetworkType networkType = NetworkType.MIJIN_TEST;

    @Test
    public void getMosaicResolutionStatementHash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("99381CE398D3AAE110FC97E984D7D35A710A5C525A4F959EC8916B382DE78A63",
                statement.getMosaicResolutionStatement().get(0).generateHash(networkType));
    }

    @Test
    public void getTransactionStatementshash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("C2D0F6CD303912B98943BA8D0407FE24AB7103403FE11C994C485206D5123F96",
                statement.getTransactionStatements().get(0).generateHash());
    }

    @Test
    public void getAddressResolutionStatementsHash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("6967470641BC527768CDC29998F4A3350813FDF2E40D1C97AB0BBA36B9AF649E",
                statement.getAddressResolutionStatements().get(0).generateHash(networkType));
    }

    private Statement getStatement() {
        StatementsDTO statementsDTO = loadResource("Statements.json", StatementsDTO.class);
        ReceiptMappingOkHttp receiptMappingOkHttp = new ReceiptMappingOkHttp(jsonHelper);
        return receiptMappingOkHttp
            .createStatementFromDto(statementsDTO, NetworkType.MIJIN_TEST);
    }
}
