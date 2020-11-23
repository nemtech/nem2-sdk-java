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
package io.nem.symbol.sdk.model.account;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.crypto.VotingKey;
import org.apache.commons.lang3.Validate;

/** Account link voting key */
public class AccountLinkVotingKey {

  /** Public Key. */
  private final PublicKey publicKey;

  /** Start epoch. */
  private final long startEpoch;

  /** End epoch. */
  private final long endEpoch;

  public AccountLinkVotingKey(PublicKey publicKey, long startEpoch, long endEpoch) {
    Validate.notNull(publicKey, "public key is required");
    this.publicKey = publicKey;
    this.startEpoch = startEpoch;
    this.endEpoch = endEpoch;
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public long getStartEpoch() {
    return startEpoch;
  }

  public long getEndEpoch() {
    return endEpoch;
  }
}
