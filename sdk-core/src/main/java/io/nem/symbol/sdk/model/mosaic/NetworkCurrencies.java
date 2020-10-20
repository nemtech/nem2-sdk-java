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

import java.util.Objects;

/** Object holding the symbol network currencies, main and harvest. */
public class NetworkCurrencies {
  /** The pre-configured public currencies for easier offline access. */
  public static final NetworkCurrencies PUBLIC =
      new NetworkCurrencies(Currency.SYMBOL_XYM, Currency.SYMBOL_XYM);

  /** The main network currency. */
  private final Currency currency;
  /** The harvest network currency. */
  private final Currency harvest;

  public NetworkCurrencies(Currency currency, Currency harvest) {
    this.currency = currency;
    this.harvest = harvest;
  }

  public Currency getCurrency() {
    return currency;
  }

  public Currency getHarvest() {
    return harvest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NetworkCurrencies that = (NetworkCurrencies) o;
    return Objects.equals(currency, that.currency) && Objects.equals(harvest, that.harvest);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currency, harvest);
  }
}
