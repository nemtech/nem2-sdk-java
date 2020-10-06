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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/** Abstract factory of {@link MetadataTransaction} */
public abstract class MetadataTransactionFactory<T extends MetadataTransaction>
    extends TransactionFactory<T> {

  /** Metadata target public key. */
  private final UnresolvedAddress targetAddress;

  /** Metadata key scoped to source, target and type. */
  private final BigInteger scopedMetadataKey;
  /**
   * When there is an existing value, the new value is calculated as xor(previous-value, value). It
   * can be a plain text.
   */
  private final String value;
  /** Change in value size in bytes. Defaulted to the size of the encoded value. */
  private int valueSizeDelta;
  /** The value size. Defaulted to the size of the encoded value. */
  private long valueSize;

  MetadataTransactionFactory(
      TransactionType transactionType,
      NetworkType networkType,
      Deadline deadline,
      UnresolvedAddress targetAddress,
      BigInteger scopedMetadataKey,
      String value) {
    super(transactionType, networkType, deadline);

    Validate.notNull(targetAddress, "TargetAddress must not be null");
    Validate.notNull(scopedMetadataKey, "ScopedMetadataKey must not be null");
    Validate.notNull(value, "Value must not be null");

    ConvertUtils.validateNotNegative(scopedMetadataKey);

    this.targetAddress = targetAddress;
    this.scopedMetadataKey = scopedMetadataKey;
    this.value = value;
    int defaultSize = MetadataTransaction.toByteArray(value).length;
    this.valueSizeDelta = defaultSize;
    this.valueSize = defaultSize;
  }

  public UnresolvedAddress getTargetAddress() {
    return targetAddress;
  }

  public BigInteger getScopedMetadataKey() {
    return scopedMetadataKey;
  }

  public int getValueSizeDelta() {
    return valueSizeDelta;
  }

  /**
   * Use this method when you want to update/modify a metadata. The value size delta needs to be
   * provided in order to update the existing metadata correctly.
   *
   * @param valueSizeDelta the new value size delta
   * @return this factory.
   */
  public MetadataTransactionFactory<T> valueSizeDelta(int valueSizeDelta) {
    this.valueSizeDelta = valueSizeDelta;
    return this;
  }

  public long getValueSize() {
    return valueSize;
  }

  public MetadataTransactionFactory<T> valueSize(long valueSize) {
    this.valueSize = valueSize;
    return this;
  }

  public String getValue() {
    return value;
  }
}
