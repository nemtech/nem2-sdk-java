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

package io.nem.sdk.model.node;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NodeTimeTest {

    static SignSchema signSchema = SignSchema.DEFAULT;

    static Account account;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST, signSchema);
    }

    @Test
    void shouldCreateNodeTime() {
        NodeTime nodeTime = new NodeTime(BigInteger.valueOf(1234567890),
            BigInteger.valueOf(987654321));
        assertEquals(987654321, nodeTime.getReceiveTimeStamp().intValue());
        assertEquals(1234567890, nodeTime.getSendTimeStamp().intValue());
    }
}
