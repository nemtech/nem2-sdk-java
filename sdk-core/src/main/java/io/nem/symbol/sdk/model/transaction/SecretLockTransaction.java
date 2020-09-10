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

import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import java.math.BigInteger;

public class SecretLockTransaction extends Transaction implements RecipientTransaction {

  private final Mosaic mosaic;
  private final BigInteger duration;
  private final LockHashAlgorithm hashAlgorithm;
  private final String secret;
  private final UnresolvedAddress recipient;

  /**
   * Contructor of this transaction using the factory.
   *
   * @param factory the factory.
   */
  SecretLockTransaction(SecretLockTransactionFactory factory) {
    super(factory);
    this.mosaic = factory.getMosaic();
    this.duration = factory.getDuration();
    this.hashAlgorithm = factory.getHashAlgorithm();
    this.secret = factory.getSecret();
    this.recipient = factory.getRecipient();
  }

  /**
   * Returns locked mosaic.
   *
   * @return locked mosaic.
   */
  public Mosaic getMosaic() {
    return mosaic;
  }

  /**
   * Returns duration for the funds to be released or returned.
   *
   * @return duration for the funds to be released or returned.
   */
  public BigInteger getDuration() {
    return duration;
  }

  /**
   * Returns the hash algorithm, secret is generated with.
   *
   * @return the hash algorithm, secret is generated with.
   */
  public LockHashAlgorithm getHashAlgorithm() {
    return hashAlgorithm;
  }

  /**
   * Returns the proof hashed.
   *
   * @return the proof hashed.
   */
  public String getSecret() {
    return secret;
  }

  /**
   * Returns the recipient of the funds.
   *
   * @return the recipient of the funds.
   */
  @Override
  public UnresolvedAddress getRecipient() {
    return recipient;
  }
}
