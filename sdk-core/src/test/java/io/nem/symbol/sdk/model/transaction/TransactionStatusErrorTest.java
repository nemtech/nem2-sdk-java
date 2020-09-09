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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class TransactionStatusErrorTest {

  @Test
  void getHash() {
    Address address = Address.generateRandom(NetworkType.MIJIN_TEST);
    final TransactionStatusError transactionStatusError =
        new TransactionStatusError(address, "hash", "error", new Deadline(BigInteger.valueOf(1)));
    assertEquals("hash", transactionStatusError.getHash());
  }

  @Test
  void getStatus() {
    Address address = Address.generateRandom(NetworkType.MIJIN_TEST);
    final TransactionStatusError transactionStatusError =
        new TransactionStatusError(address, "hash", "error", new Deadline(BigInteger.valueOf(1)));
    assertEquals("error", transactionStatusError.getStatus());
  }

  @Test
  void getDeadline() {
    Address address = Address.generateRandom(NetworkType.MIJIN_TEST);
    Deadline deadline = new Deadline(BigInteger.valueOf(1));
    final TransactionStatusError transactionStatusError =
        new TransactionStatusError(address, "hash", "error", deadline);
    assertEquals(deadline, transactionStatusError.getDeadline());
  }

  @Test
  void getAddress() {
    Address address = Address.generateRandom(NetworkType.MIJIN_TEST);
    Deadline deadline = new Deadline(BigInteger.valueOf(1));
    final TransactionStatusError transactionStatusError =
        new TransactionStatusError(address, "hash", "error", deadline);
    assertEquals(address, transactionStatusError.getAddress());
  }
}
