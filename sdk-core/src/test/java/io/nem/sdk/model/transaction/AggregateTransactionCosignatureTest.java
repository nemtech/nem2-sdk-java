/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import org.junit.jupiter.api.Test;

public class AggregateTransactionCosignatureTest {

    private static SignSchema signSchema = SignSchema.DEFAULT;
    @Test
    void createAnAggregateCosignatureViaConstructor() {
        AggregateTransactionCosignature aggregateTransactionCosignature =
            new AggregateTransactionCosignature(
                "signature",
                new PublicAccount(
                    "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
                    NetworkType.MIJIN_TEST, signSchema));

        assertEquals("signature", aggregateTransactionCosignature.getSignature());
        assertEquals(
            new PublicAccount(
                "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
                NetworkType.MIJIN_TEST, signSchema),
            aggregateTransactionCosignature.getSigner());
    }
}
