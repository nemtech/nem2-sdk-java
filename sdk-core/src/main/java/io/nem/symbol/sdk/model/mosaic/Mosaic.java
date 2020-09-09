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

import io.nem.symbol.core.utils.ConvertUtils;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/**
 * A mosaic describes an instance of a mosaic definition. Mosaics can be transferred by means of a
 * transfer transaction.
 *
 * @since 1.0
 */
public class Mosaic {

  private final UnresolvedMosaicId id;

  private final BigInteger amount;

  public Mosaic(UnresolvedMosaicId id, BigInteger amount) {
    Validate.notNull(id, "Id must not be null");
    Validate.notNull(amount, "Amount must not be null");
    ConvertUtils.validateNotNegative(amount);
    this.id = id;
    this.amount = amount;
  }

  /**
   * Returns the mosaic identifier
   *
   * @return mosaic identifier
   */
  public UnresolvedMosaicId getId() {
    return id;
  }

  /**
   * Returns mosaic id as a hexadecimal string
   *
   * @return id hex string
   */
  public String getIdAsHex() {
    return id.getIdAsHex();
  }

  /**
   * Return mosaic amount. The quantity is always given in smallest units for the mosaic i.e. if it
   * has a divisibility of 3 the quantity is given in millis.
   *
   * @return amount of mosaic
   */
  public BigInteger getAmount() {
    return amount;
  }
}
