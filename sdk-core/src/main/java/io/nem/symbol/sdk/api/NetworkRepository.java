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

import io.nem.symbol.sdk.model.network.NetworkConfiguration;
import io.nem.symbol.sdk.model.network.NetworkInfo;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.network.RentalFees;
import io.nem.symbol.sdk.model.network.TransactionFees;
import io.reactivex.Observable;

/**
 * Network interface repository.
 *
 * @since 1.0
 */
public interface NetworkRepository {

  /**
   * Get current network type.
   *
   * @return network type enum.
   */
  Observable<NetworkType> getNetworkType();

  /**
   * Returns information about the average, median, highest and lower fee multiplier over the last
   * "numBlocksTransactionFeeStats".
   *
   * @return the TransactionFees
   */
  Observable<TransactionFees> getTransactionFees();

  /** @return the network information with like the network's name and description. */
  Observable<NetworkInfo> getNetworkInfo();

  /**
   * @return the estimated effective rental fees for namespaces and mosaics. This endpoint is only
   *     available if the REST instance has access to catapult-server
   *     ``resources/config-network.properties`` file. To activate this feature, add the setting
   *     "network.propertiesFilePath" in the configuration file (rest/resources/rest.json).
   */
  Observable<RentalFees> getRentalFees();

  /**
   * @return the content from a catapult-server network configuration file
   *     (resources/config-network.properties). To enable this feature, the REST setting
   *     "network.propertiesFilePath" must define where the file is located. This is adjustable via
   *     the configuration file (rest/resources/rest.json) per REST instance.
   */
  Observable<NetworkConfiguration> getNetworkProperties();
}
