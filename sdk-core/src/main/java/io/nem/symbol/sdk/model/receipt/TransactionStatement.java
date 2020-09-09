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

import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.utils.ArrayUtils;
import io.nem.symbol.core.utils.ByteUtils;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.Stored;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class TransactionStatement implements Stored {

  private final Optional<String> recordId;
  private final BigInteger height;
  private final ReceiptSource receiptSource;
  private final List<Receipt> receipts;

  /**
   * Constructor
   *
   * @param recordId the database record id if known.
   * @param height Block height
   * @param receiptSource The receipt source.
   * @param receipts Array of receipts.
   */
  public TransactionStatement(
      String recordId, BigInteger height, ReceiptSource receiptSource, List<Receipt> receipts) {
    this.recordId = Optional.ofNullable(recordId);
    this.height = height;
    this.receiptSource = receiptSource;
    this.receipts = receipts;
  }

  /**
   * Returns receipt source
   *
   * @return receipt source
   */
  public ReceiptSource getReceiptSource() {
    return this.receiptSource;
  }

  /**
   * Returns block height
   *
   * @return block height
   */
  public BigInteger getHeight() {
    return this.height;
  }

  /**
   * Returns Array of receipts.
   *
   * @return Array of receipts.
   */
  public List<Receipt> getReceipts() {
    return this.receipts;
  }

  /**
   * Serialize transaction statement and generate hash
   *
   * @return transaction statement hash
   */
  public String generateHash() {

    final byte[] versionByte =
        ByteUtils.shortToBytes(
            Short.reverseBytes((short) ReceiptVersion.TRANSACTION_STATEMENT.getValue()));
    final byte[] typeByte =
        ByteUtils.shortToBytes(
            Short.reverseBytes((short) ReceiptType.TRANSACTION_GROUP.getValue()));
    final byte[] sourceByte = getReceiptSource().serialize();

    byte[] results = ArrayUtils.concat(versionByte, typeByte, sourceByte);

    for (final Receipt receipt : receipts) {
      results = ArrayUtils.concat(results, receipt.serialize());
    }

    byte[] hash = Hashes.sha3_256(results);
    return ConvertUtils.toHex(hash);
  }

  @Override
  public Optional<String> getRecordId() {
    return recordId;
  }
}
