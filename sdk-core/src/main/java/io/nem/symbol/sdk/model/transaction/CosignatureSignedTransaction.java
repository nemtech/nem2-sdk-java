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

import io.nem.symbol.sdk.model.account.PublicAccount;
import java.math.BigInteger;

/**
 * The co-signature signed transaction.
 *
 * @since 1.0
 */
public class CosignatureSignedTransaction extends AggregateTransactionCosignature {

  private final String parentHash;

  public CosignatureSignedTransaction(
      BigInteger version, String parentHash, String signature, PublicAccount signer) {
    super(version, signature, signer);
    this.parentHash = parentHash;
  }

  /**
   * Returns hash of parent aggregate transaction that has been signed by a cosignatory of the
   * transaction.
   *
   * @return String
   */
  public String getParentHash() {
    return parentHash;
  }
}
