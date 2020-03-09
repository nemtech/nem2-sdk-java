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
 * MosaicNetworkProperties
 */
public class MosaicNetworkProperties {

    /**
     * Maximum number of mosaics that an account can own.
     **/
    private final String maxMosaicsPerAccount;

    /**
     * Maximum mosaic duration.
     **/
    private final String maxMosaicDuration;

    /**
     * Maximum mosaic divisibility.
     **/
    private final String maxMosaicDivisibility;

    /**
     * Public key.
     **/
    private final String mosaicRentalFeeSinkPublicKey;

    /**
     * Mosaic rental fee.
     **/
    private final String mosaicRentalFee;

  public MosaicNetworkProperties(String maxMosaicsPerAccount, String maxMosaicDuration,
      String maxMosaicDivisibility, String mosaicRentalFeeSinkPublicKey,
      String mosaicRentalFee) {
    this.maxMosaicsPerAccount = maxMosaicsPerAccount;
    this.maxMosaicDuration = maxMosaicDuration;
    this.maxMosaicDivisibility = maxMosaicDivisibility;
    this.mosaicRentalFeeSinkPublicKey = mosaicRentalFeeSinkPublicKey;
    this.mosaicRentalFee = mosaicRentalFee;
  }

  public String getMaxMosaicsPerAccount() {
    return maxMosaicsPerAccount;
  }

  public String getMaxMosaicDuration() {
    return maxMosaicDuration;
  }

  public String getMaxMosaicDivisibility() {
    return maxMosaicDivisibility;
  }

  public String getMosaicRentalFeeSinkPublicKey() {
    return mosaicRentalFeeSinkPublicKey;
  }

  public String getMosaicRentalFee() {
    return mosaicRentalFee;
  }
}

