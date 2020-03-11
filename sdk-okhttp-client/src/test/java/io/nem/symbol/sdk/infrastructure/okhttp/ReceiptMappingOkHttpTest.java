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

import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.receipt.Statement;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.StatementsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReceiptMappingOkHttpTest {

    private final JsonHelper jsonHelper = new JsonHelperGson();

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
    public void getAddressResolutionStatementsHash() {
        Statement statement = getStatement();
        Assertions
            .assertEquals("812AA120525990BE821035BC9CBCCD569F807B5338BA9D13DD63D99F3697ACCA",
                statement.getAddressResolutionStatements().get(0).generateHash(networkType));
    }

    private Statement getStatement() {
        StatementsDTO statementsDTO = loadResource("Statements.json", StatementsDTO.class);
        ReceiptMappingOkHttp receiptMappingOkHttp = new ReceiptMappingOkHttp(jsonHelper);
        return receiptMappingOkHttp
            .createStatementFromDto(statementsDTO, NetworkType.MIJIN_TEST);
    }
}
