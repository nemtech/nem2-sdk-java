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
package io.nem.symbol.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class CurrencyCatCurrencyTest {

  private Currency currency = Currency.CAT_CURRENCY;

  @Test
  void shouldCreateRelativeMosaic() {
    Mosaic currency = this.currency.createRelative(BigInteger.valueOf(1000));
    assertEquals(BigInteger.valueOf(1000 * 1000000), currency.getAmount());
    assertEquals(this.currency.getNamespaceId().get(), currency.getId());
    assertEquals("85BBEA6CC462B244", currency.getIdAsHex());
  }

  @Test
  void shouldCreateRelativeMosaicUsingBigDecimal() {
    Mosaic currency = this.currency.createRelative(BigDecimal.valueOf(0.000001));
    assertEquals(BigInteger.valueOf((long) (0.000001 * 1000000)), currency.getAmount());
    assertEquals(this.currency.getNamespaceId().get(), currency.getId());
    assertEquals("85BBEA6CC462B244", currency.getIdAsHex());
  }

  @Test
  void shouldCreateAbsoluteMosaic() {
    Mosaic currency = this.currency.createAbsolute(BigInteger.valueOf(1));
    assertEquals(BigInteger.valueOf(1), currency.getAmount());
    assertEquals(this.currency.getNamespaceId().get(), currency.getId());
    assertEquals("85BBEA6CC462B244", currency.getIdAsHex());
  }

  @Test
  void shouldCompareNamespaceIdsForEquality() {
    NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.valueOf(-8810190493148073404L));
    assertEquals(-8810190493148073404L, namespaceId.getIdAsLong());
    assertEquals(currency.getNamespaceId().get().getIdAsLong(), namespaceId.getIdAsLong());
    assertEquals(currency.getNamespaceId().get().getIdAsHex(), namespaceId.getIdAsHex());

    // Note:
    // BigInteger decimal generated from namespace path vs generated using Lower and
    // Higher
    // integers
    // Using namespace path: 9636553580561478212 (Decimal number)
    // Using Lower and Higher: -8810190493148073404 (Decimal from signed 2's
    // complement)
    // In Hexadecimal: 85bbea6cc462b244 (same for both)
  }

  @Test
  @SuppressWarnings("squid:S3415")
  void shouldHaveValidStatics() {
    assertEquals(currency.getUnresolvedMosaicId(), currency.getNamespaceId().get());
    assertEquals("cat.currency", currency.getNamespaceId().get().getFullName().get());
    assertEquals(6, currency.getDivisibility());
    assertTrue(currency.isTransferable());
    assertFalse(currency.isSupplyMutable());
  }
}
