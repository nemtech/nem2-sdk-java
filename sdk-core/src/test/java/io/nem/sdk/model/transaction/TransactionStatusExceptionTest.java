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

package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.account.Address;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class TransactionStatusExceptionTest {

    @Test
    void getStatusError() {
        Address address = Address.createFromRawAddress("SCJFR55L7KWHERD2VW6C3NR2MBZLVDQWDHCHH6ZP");
        TransactionStatusError transactionStatusError =
            new TransactionStatusError(address, "hash", "error",
                new Deadline(BigInteger.valueOf(1)));

        TransactionStatusException exception = new TransactionStatusException(
            new IllegalStateException("Caller"), transactionStatusError);

        assertEquals(transactionStatusError, exception.getStatusError());
    }

}
