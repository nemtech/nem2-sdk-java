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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CurrencyTest {

  @Test
  @SuppressWarnings("squid:S3415")
  void shouldAssertEquals() {
    assertNotEquals(Currency.CAT_HARVEST, Currency.CAT_CURRENCY);
    assertNotEquals(Currency.SYMBOL_XYM, Currency.CAT_CURRENCY);
    assertEquals(Currency.CAT_CURRENCY, Currency.CAT_CURRENCY);
  }

  @Test
  @SuppressWarnings("squid:S3415")
  void souldAssertHashCodeEquals() {
    assertNotEquals(Currency.CAT_HARVEST.hashCode(), Currency.CAT_CURRENCY.hashCode());
    assertNotEquals(Currency.SYMBOL_XYM.hashCode(), Currency.CAT_CURRENCY.hashCode());
    assertEquals(Currency.CAT_CURRENCY.hashCode(), Currency.CAT_CURRENCY.hashCode());
  }

  @Test
  void assertCreatedUsingMosaicId() {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    Currency currency =
        new CurrencyBuilder(mosaicId, 6).withSupplyMutable(false).withTransferable(true).build();

    assertEquals(mosaicId, currency.getMosaicId().get());
    assertEquals(mosaicId, currency.getUnresolvedMosaicId());
    Assertions.assertFalse(currency.getNamespaceId().isPresent());
  }

  @Test
  void assertCreatedUsingNamespaceId() {
    NamespaceId namespaceId = NamespaceId.createFromName("mycurrency");
    Currency currency =
        new CurrencyBuilder(namespaceId, 6).withSupplyMutable(false).withTransferable(true).build();

    Assertions.assertEquals(namespaceId, currency.getNamespaceId().get());
    Assertions.assertEquals(namespaceId, currency.getUnresolvedMosaicId());
    Assertions.assertFalse(currency.getMosaicId().isPresent());
  }

  @Test
  void assertCreatedUsingNamespaceIdAndSettingMosaicId() {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    NamespaceId namespaceId = NamespaceId.createFromName("mycurrency");
    Currency currency =
        new CurrencyBuilder(namespaceId, 6)
            .withSupplyMutable(false)
            .withTransferable(true)
            .withMosaicId(mosaicId)
            .build();

    Assertions.assertEquals(namespaceId, currency.getNamespaceId().get());
    Assertions.assertEquals(namespaceId, currency.getUnresolvedMosaicId());
    assertEquals(mosaicId, currency.getMosaicId().get());
  }

  @Test
  void createMosaicWithIntegers() {
    NamespaceId namespaceId = NamespaceId.createFromName("mycurrency");
    Currency currency =
        new CurrencyBuilder(namespaceId, 3).withSupplyMutable(false).withTransferable(true).build();
    Mosaic mosaic1 = currency.createRelative(15);
    Mosaic mosaic2 = currency.createRelative(BigInteger.valueOf(15));
    Mosaic mosaic3 = currency.createAbsolute(15000);
    Assertions.assertEquals(mosaic1, mosaic2);
    Assertions.assertEquals(mosaic1, mosaic3);
  }

  @Test
  void createMosaicWithDecimals() {
    NamespaceId namespaceId = NamespaceId.createFromName("mycurrency");
    Currency currency =
        new CurrencyBuilder(namespaceId, 3).withSupplyMutable(false).withTransferable(true).build();
    Mosaic mosaic1 = currency.createRelative(15.2);
    Mosaic mosaic2 = currency.createRelative(BigDecimal.valueOf(15.2));
    Mosaic mosaic3 = currency.createAbsolute(15200);
    Mosaic mosaic4 = currency.createRelative(15.2004);
    Mosaic mosaic5 = currency.createRelative(BigDecimal.valueOf(15.2004));
    Assertions.assertEquals(mosaic1, mosaic2);
    Assertions.assertEquals(mosaic1, mosaic3);
    Assertions.assertEquals(mosaic1, mosaic4);
    Assertions.assertEquals(mosaic1, mosaic5);
  }
}
