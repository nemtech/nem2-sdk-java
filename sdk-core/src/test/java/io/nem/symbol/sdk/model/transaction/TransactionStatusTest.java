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

package io.nem.symbol.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

public class TransactionStatusTest {

    @Test
    void createATransactionStatusViaConstructor() {
        TransactionStatus transactionStatus =
            new TransactionStatus(
                TransactionState.CONFIRMED,
                "Success",
                "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F",
                new Deadline(2, ChronoUnit.HOURS),
                new BigInteger("121855"));

        assertEquals(TransactionState.CONFIRMED, transactionStatus.getGroup());
        assertEquals("Success", transactionStatus.getCode());
        assertEquals(
            "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F",
            transactionStatus.getHash());
        assertTrue(
            LocalDateTime.now().isBefore(transactionStatus.getDeadline().getLocalDateTime()));
        assertEquals(new BigInteger("121855"), transactionStatus.getHeight());
    }
}
