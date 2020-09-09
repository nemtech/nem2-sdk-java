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
/** AggregateNetworkProperties */
public class AggregateNetworkProperties {

  /** Maximum number of transactions per aggregate. */
  private final String maxTransactionsPerAggregate;

  /** Maximum number of cosignatures per aggregate. */
  private final String maxCosignaturesPerAggregate;

  /**
   * Set to true if cosignatures must exactly match component signers. Set to false if cosignatures
   * should be validated externally.
   */
  private final Boolean enableStrictCosignatureCheck;

  /**
   * Set to true if bonded aggregates should be allowed. Set to false if bonded aggregates should be
   * rejected.
   */
  private final Boolean enableBondedAggregateSupport;

  /** Maximum lifetime a bonded transaction can have before it expires. */
  private final String maxBondedTransactionLifetime;

  public AggregateNetworkProperties(
      String maxTransactionsPerAggregate,
      String maxCosignaturesPerAggregate,
      Boolean enableStrictCosignatureCheck,
      Boolean enableBondedAggregateSupport,
      String maxBondedTransactionLifetime) {
    this.maxTransactionsPerAggregate = maxTransactionsPerAggregate;
    this.maxCosignaturesPerAggregate = maxCosignaturesPerAggregate;
    this.enableStrictCosignatureCheck = enableStrictCosignatureCheck;
    this.enableBondedAggregateSupport = enableBondedAggregateSupport;
    this.maxBondedTransactionLifetime = maxBondedTransactionLifetime;
  }

  public String getMaxTransactionsPerAggregate() {
    return maxTransactionsPerAggregate;
  }

  public String getMaxCosignaturesPerAggregate() {
    return maxCosignaturesPerAggregate;
  }

  public Boolean getEnableStrictCosignatureCheck() {
    return enableStrictCosignatureCheck;
  }

  public Boolean getEnableBondedAggregateSupport() {
    return enableBondedAggregateSupport;
  }

  public String getMaxBondedTransactionLifetime() {
    return maxBondedTransactionLifetime;
  }
}
