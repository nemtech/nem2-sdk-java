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




package io.nem.symbol.sdk.model.network;

/**
 * SecretLockNetworkProperties
 */
public class SecretLockNetworkProperties {

    /**
     * Maximum number of blocks for which a secret lock can exist.
     **/
    private final String maxSecretLockDuration;
    /**
     * Minimum size of a proof in bytes.
     **/
    private final String minProofSize;

    /**
     * Maximum size of a proof in bytes.
     **/
    private final String maxProofSize;

  public SecretLockNetworkProperties(String maxSecretLockDuration, String minProofSize,
      String maxProofSize) {
    this.maxSecretLockDuration = maxSecretLockDuration;
    this.minProofSize = minProofSize;
    this.maxProofSize = maxProofSize;
  }

  public String getMaxSecretLockDuration() {
    return maxSecretLockDuration;
  }

  public String getMinProofSize() {
    return minProofSize;
  }

  public String getMaxProofSize() {
    return maxProofSize;
  }
}

