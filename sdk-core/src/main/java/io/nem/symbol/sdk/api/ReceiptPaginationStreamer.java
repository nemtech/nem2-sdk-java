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
package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.receipt.AddressResolutionStatement;
import io.nem.symbol.sdk.model.receipt.MosaicResolutionStatement;
import io.nem.symbol.sdk.model.receipt.TransactionStatement;

/** Factory for recipient streamers. */
public class ReceiptPaginationStreamer {

  /**
   * It creates a transaction statement streamer of TransactionStatement objects.
   *
   * @param repository the {@link ReceiptRepository} repository
   * @return a new Pagination Streamer.
   */
  public static PaginationStreamer<TransactionStatement, TransactionStatementSearchCriteria>
      transactions(ReceiptRepository repository) {
    return new PaginationStreamer<>(repository::searchReceipts);
  }

  /**
   * It creates a transaction statement streamer of AddressResolutionStatement objects.
   *
   * @param repository the {@link ReceiptRepository} repository
   * @return a new Pagination Streamer.
   */
  public static PaginationStreamer<AddressResolutionStatement, ResolutionStatementSearchCriteria>
      addresses(ReceiptRepository repository) {
    return new PaginationStreamer<>(repository::searchAddressResolutionStatements);
  }

  /**
   * It creates a mosaic resolution statement streamer of MosaicResolutionStatement objects.
   *
   * @param repository the {@link ReceiptRepository} repository
   * @return a new Pagination Streamer.
   */
  public static PaginationStreamer<MosaicResolutionStatement, ResolutionStatementSearchCriteria>
      mosaics(ReceiptRepository repository) {
    return new PaginationStreamer<>(repository::searchMosaicResolutionStatements);
  }
}
