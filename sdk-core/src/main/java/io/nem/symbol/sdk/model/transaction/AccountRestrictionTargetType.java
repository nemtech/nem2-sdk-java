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

import io.nem.symbol.core.utils.MapperUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * The account restriction target type used to know the type of values an account Restriction Type
 * has.
 */
public enum AccountRestrictionTargetType {

  /** The value should be an unresolved address. */
  ADDRESS,

  /** The value should be a unresolved mosaic id. */
  MOSAIC_ID,

  /** The value should be an int of a known Transaction Type. */
  TRANSACTION_TYPE;

  /**
   * This method knows how to convert the basic value returned in rest api to the more useful model
   * objects.
   *
   * @param value the string value.
   * @return the model representation.
   */
  public Object fromString(String value) {
    try {
      if (this == ADDRESS) {
        return MapperUtils.toUnresolvedAddress(value);
      }
      if (this == MOSAIC_ID) {
        return MapperUtils.toUnresolvedMosaicId(value);
      }
      if (this == TRANSACTION_TYPE) {
        // https://stackoverflow.com/questions/15507997/how-to-prevent-gson-from-expressing-integers-as-floats/15508288
        return TransactionType.rawValueOf((int) Double.parseDouble(value));
      }
      throw new IllegalStateException("Unknown enum value " + this);
    } catch (RuntimeException e) {
      throw new IllegalArgumentException(
          "Value '"
              + value
              + "' cannot be converted to "
              + this
              + ". Error: "
              + ExceptionUtils.getMessage(e),
          e);
    }
  }
}
