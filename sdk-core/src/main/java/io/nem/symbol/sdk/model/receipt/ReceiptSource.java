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

import io.nem.symbol.catapult.builders.ReceiptSourceBuilder;

/** The Receipt Source class */
public class ReceiptSource {

  private final long primaryId;
  private final long secondaryId;

  /**
   * Constructor
   *
   * @param primaryId Receipt source primary id
   * @param secondaryId Receipt source secondary id
   */
  public ReceiptSource(long primaryId, long secondaryId) {
    this.primaryId = primaryId;
    this.secondaryId = secondaryId;
  }

  /**
   * Returns Receipt source primary id
   *
   * @return Receipt source primary id
   */
  public long getPrimaryId() {
    return this.primaryId;
  }

  /**
   * Returns Receipt source secondary id
   *
   * @return Receipt source secondary id
   */
  public long getSecondaryId() {
    return this.secondaryId;
  }

  /**
   * Serialize receipt and returns receipt bytes
   *
   * @return receipt bytes
   */
  public byte[] serialize() {
    return ReceiptSourceBuilder.create((int) getPrimaryId(), (int) getSecondaryId()).serialize();
  }
}
