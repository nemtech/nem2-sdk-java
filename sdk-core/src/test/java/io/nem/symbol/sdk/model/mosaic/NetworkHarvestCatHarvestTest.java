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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class NetworkHarvestCatHarvestTest {

  private Currency currency = Currency.CAT_HARVEST;

  @Test
  void shouldCreateRelativeNetworkHarvestMosaic() {
    Mosaic currency = this.currency.createRelative(BigInteger.valueOf(1000));
    assertEquals(BigInteger.valueOf(1000 * 1000), currency.getAmount());
    assertEquals(this.currency.getNamespaceId().get(), currency.getId());
    assertEquals("941299B2B7E1291C", currency.getIdAsHex());
  }

  @Test
  void shouldCreateAbsoluteNetworkHarvestMosaic() {
    Mosaic currency = this.currency.createAbsolute(BigInteger.valueOf(1));
    assertEquals(BigInteger.valueOf(1), currency.getAmount());
    assertEquals(this.currency.getNamespaceId().get(), currency.getId());
    assertEquals("941299B2B7E1291C", currency.getIdAsHex());
  }

  @Test
  void shouldCompareNamespaceIdsForEquality() {
    NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.valueOf(-7776984613647210212L));
    assertEquals(-7776984613647210212L, namespaceId.getIdAsLong());
    assertEquals(currency.getNamespaceId().get().getIdAsLong(), namespaceId.getIdAsLong());
    assertEquals(currency.getNamespaceId().get().getIdAsHex(), namespaceId.getIdAsHex());
  }

  @Test
  @SuppressWarnings("squid:S3415")
  void shouldHaveValidStatics() {
    assertEquals(currency.getUnresolvedMosaicId(), currency.getNamespaceId().get());
    assertEquals("cat.harvest", currency.getNamespaceId().get().getFullName().get());
    assertEquals(3, currency.getDivisibility());
    assertTrue(currency.isTransferable());
    assertTrue(currency.isSupplyMutable());
  }
}
