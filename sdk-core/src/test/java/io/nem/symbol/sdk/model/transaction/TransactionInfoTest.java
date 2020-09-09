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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class TransactionInfoTest {

  @Test
  void createATransactionInfoWithStaticConstructorCreateForTransactionsGetUsingListener() {
    TransactionInfo transactionInfo =
        TransactionInfo.create(
            new BigInteger("121855"),
            "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F",
            "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F");

    assertEquals(new BigInteger("121855"), transactionInfo.getHeight());
    assertFalse(transactionInfo.getIndex().isPresent());
    assertFalse(transactionInfo.getId().isPresent());
    assertFalse(transactionInfo.getAggregateHash().isPresent());
    assertFalse(transactionInfo.getAggregateId().isPresent());
    assertTrue(transactionInfo.getHash().isPresent());
    assertTrue(transactionInfo.getMerkleComponentHash().isPresent());

    assertEquals(
        "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F",
        transactionInfo.getHash().get());
    assertEquals(
        "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F",
        transactionInfo.getMerkleComponentHash().get());
  }

  @Test
  void createATransactionInfoWithStaticConstructorCreateForStandaloneTransactions() {
    TransactionInfo transactionInfo =
        TransactionInfo.create(
            new BigInteger("121855"),
            1,
            "ABC",
            "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F",
            "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F");

    assertEquals(new BigInteger("121855"), transactionInfo.getHeight());
    assertTrue(transactionInfo.getIndex().isPresent());
    assertEquals("ABC", transactionInfo.getId().get());
    assertFalse(transactionInfo.getAggregateHash().isPresent());
    assertFalse(transactionInfo.getAggregateId().isPresent());
    assertTrue(transactionInfo.getHash().isPresent());
    assertTrue(transactionInfo.getMerkleComponentHash().isPresent());
    assertEquals(1, (int) transactionInfo.getIndex().get());
    assertEquals(
        "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F",
        transactionInfo.getHash().get());
    assertEquals(
        "B6C7648A3DDF71415650805E9E7801424FE03BBEE7D21F9C57B60220D3E95B2F",
        transactionInfo.getMerkleComponentHash().get());
  }

  @Test
  void createATransactionInfoWithStaticConstructorCreateForAggregateInnerTransactions() {
    TransactionInfo transactionInfo =
        TransactionInfo.createAggregate(
            new BigInteger("121855"),
            1,
            "5A3D23889CD1E800015929A9",
            "3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006",
            "5A0069D83F17CF0001777E55");

    assertEquals(new BigInteger("121855"), transactionInfo.getHeight());
    assertTrue(transactionInfo.getIndex().isPresent());
    assertTrue(transactionInfo.getId().isPresent());
    assertFalse(transactionInfo.getHash().isPresent());
    assertFalse(transactionInfo.getMerkleComponentHash().isPresent());
    assertTrue(transactionInfo.getAggregateHash().isPresent());
    assertTrue(transactionInfo.getAggregateId().isPresent());
    assertEquals(1, (int) transactionInfo.getIndex().get());
    assertEquals("5A3D23889CD1E800015929A9", transactionInfo.getId().get());
    assertEquals(
        "3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006",
        transactionInfo.getAggregateHash().get());
    assertEquals("5A0069D83F17CF0001777E55", transactionInfo.getAggregateId().get());
  }
}
