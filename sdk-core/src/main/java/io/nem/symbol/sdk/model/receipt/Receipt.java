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
package io.nem.symbol.sdk.model.receipt;

import java.util.Optional;

/** The receipt abstract class */
public abstract class Receipt {

  private final ReceiptType type;
  private final ReceiptVersion version;
  private final Optional<Integer> size;

  /**
   * Constructor
   *
   * @param type Receipt Type
   * @param version Receipt Version
   * @param size Receipt Size
   */
  public Receipt(ReceiptType type, ReceiptVersion version, Optional<Integer> size) {
    this.type = type;
    this.version = version;
    this.size = size;
  }

  /**
   * Returns the receipt type
   *
   * @return receipt type
   */
  public ReceiptType getType() {
    return this.type;
  }

  /**
   * Returns the receipt version
   *
   * @return receipt version
   */
  public ReceiptVersion getVersion() {
    return this.version;
  }

  /**
   * Returns the receipt size
   *
   * @return receipt size
   */
  public Optional<Integer> getSize() {
    return this.size;
  }

  /**
   * Serialize receipt and returns receipt bytes
   *
   * @return receipt bytes
   */
  abstract byte[] serialize();
}
