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

class NetworkCurrencyTest {

  @Test
  @SuppressWarnings("squid:S3415")
  void shouldAssertEquals() {
    assertNotEquals(NetworkCurrency.CAT_HARVEST, NetworkCurrency.CAT_CURRENCY);
    assertNotEquals(NetworkCurrency.SYMBOL_XYM, NetworkCurrency.CAT_CURRENCY);
    assertEquals(NetworkCurrency.CAT_CURRENCY, NetworkCurrency.CAT_CURRENCY);
  }

  @Test
  @SuppressWarnings("squid:S3415")
  void souldAssertHashCodeEquals() {
    assertNotEquals(
        NetworkCurrency.CAT_HARVEST.hashCode(), NetworkCurrency.CAT_CURRENCY.hashCode());
    assertNotEquals(NetworkCurrency.SYMBOL_XYM.hashCode(), NetworkCurrency.CAT_CURRENCY.hashCode());
    assertEquals(NetworkCurrency.CAT_CURRENCY.hashCode(), NetworkCurrency.CAT_CURRENCY.hashCode());
  }

  @Test
  void assertCreatedUsingMosaicId() {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    NetworkCurrency networkCurrency =
        new NetworkCurrencyBuilder(mosaicId, 6)
            .withSupplyMutable(false)
            .withTransferable(true)
            .build();

    assertEquals(mosaicId, networkCurrency.getMosaicId().get());
    assertEquals(mosaicId, networkCurrency.getUnresolvedMosaicId());
    Assertions.assertFalse(networkCurrency.getNamespaceId().isPresent());
  }

  @Test
  void assertCreatedUsingNamespaceId() {
    NamespaceId namespaceId = NamespaceId.createFromName("mycurrency");
    NetworkCurrency networkCurrency =
        new NetworkCurrencyBuilder(namespaceId, 6)
            .withSupplyMutable(false)
            .withTransferable(true)
            .build();

    Assertions.assertEquals(namespaceId, networkCurrency.getNamespaceId().get());
    Assertions.assertEquals(namespaceId, networkCurrency.getUnresolvedMosaicId());
    Assertions.assertFalse(networkCurrency.getMosaicId().isPresent());
  }

  @Test
  void assertCreatedUsingNamespaceIdAndSettingMosaicId() {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    NamespaceId namespaceId = NamespaceId.createFromName("mycurrency");
    NetworkCurrency networkCurrency =
        new NetworkCurrencyBuilder(namespaceId, 6)
            .withSupplyMutable(false)
            .withTransferable(true)
            .withMosaicId(mosaicId)
            .build();

    Assertions.assertEquals(namespaceId, networkCurrency.getNamespaceId().get());
    Assertions.assertEquals(namespaceId, networkCurrency.getUnresolvedMosaicId());
    assertEquals(mosaicId, networkCurrency.getMosaicId().get());
  }

  @Test
  void createMosaicWithIntegers() {
    NamespaceId namespaceId = NamespaceId.createFromName("mycurrency");
    NetworkCurrency networkCurrency =
        new NetworkCurrencyBuilder(namespaceId, 3)
            .withSupplyMutable(false)
            .withTransferable(true)
            .build();
    Mosaic mosaic1 = networkCurrency.createRelative(15);
    Mosaic mosaic2 = networkCurrency.createRelative(BigInteger.valueOf(15));
    Mosaic mosaic3 = networkCurrency.createAbsolute(15000);
    Assertions.assertEquals(mosaic1, mosaic2);
    Assertions.assertEquals(mosaic1, mosaic3);
  }

  @Test
  void createMosaicWithDecimals() {
    NamespaceId namespaceId = NamespaceId.createFromName("mycurrency");
    NetworkCurrency networkCurrency =
        new NetworkCurrencyBuilder(namespaceId, 3)
            .withSupplyMutable(false)
            .withTransferable(true)
            .build();
    Mosaic mosaic1 = networkCurrency.createRelative(15.2);
    Mosaic mosaic2 = networkCurrency.createRelative(BigDecimal.valueOf(15.2));
    Mosaic mosaic3 = networkCurrency.createAbsolute(15200);
    Mosaic mosaic4 = networkCurrency.createRelative(15.2004);
    Mosaic mosaic5 = networkCurrency.createRelative(BigDecimal.valueOf(15.2004));
    Assertions.assertEquals(mosaic1, mosaic2);
    Assertions.assertEquals(mosaic1, mosaic3);
    Assertions.assertEquals(mosaic1, mosaic4);
    Assertions.assertEquals(mosaic1, mosaic5);
  }
}
