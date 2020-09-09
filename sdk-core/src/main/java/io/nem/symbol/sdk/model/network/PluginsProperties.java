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
/** Plugin related configuration properties. */
public class PluginsProperties {

  /** AccountLinkNetworkProperties */
  private final AccountLinkNetworkProperties accountlink;
  /** AggregateNetworkProperties */
  private final AggregateNetworkProperties aggregate;
  /** HashLockNetworkProperties */
  private final HashLockNetworkProperties lockhash;
  /** SecretLockNetworkProperties */
  private final SecretLockNetworkProperties locksecret;
  /** MetadataNetworkProperties */
  private final MetadataNetworkProperties metadata;
  /** MosaicNetworkProperties */
  private final MosaicNetworkProperties mosaic;
  /** MultisigNetworkProperties */
  private final MultisigNetworkProperties multisig;
  /** NamespaceNetworkProperties */
  private final NamespaceNetworkProperties namespace;
  /** AccountRestrictionNetworkProperties */
  private final AccountRestrictionNetworkProperties restrictionaccount;
  /** MosaicRestrictionNetworkProperties */
  private final MosaicRestrictionNetworkProperties restrictionmosaic;
  /** TransferNetworkProperties */
  private final TransferNetworkProperties transfer;

  public PluginsProperties(
      AccountLinkNetworkProperties accountlink,
      AggregateNetworkProperties aggregate,
      HashLockNetworkProperties lockhash,
      SecretLockNetworkProperties locksecret,
      MetadataNetworkProperties metadata,
      MosaicNetworkProperties mosaic,
      MultisigNetworkProperties multisig,
      NamespaceNetworkProperties namespace,
      AccountRestrictionNetworkProperties restrictionaccount,
      MosaicRestrictionNetworkProperties restrictionmosaic,
      TransferNetworkProperties transfer) {
    this.accountlink = accountlink;
    this.aggregate = aggregate;
    this.lockhash = lockhash;
    this.locksecret = locksecret;
    this.metadata = metadata;
    this.mosaic = mosaic;
    this.multisig = multisig;
    this.namespace = namespace;
    this.restrictionaccount = restrictionaccount;
    this.restrictionmosaic = restrictionmosaic;
    this.transfer = transfer;
  }

  public AccountLinkNetworkProperties getAccountlink() {
    return accountlink;
  }

  public AggregateNetworkProperties getAggregate() {
    return aggregate;
  }

  public HashLockNetworkProperties getLockhash() {
    return lockhash;
  }

  public SecretLockNetworkProperties getLocksecret() {
    return locksecret;
  }

  public MetadataNetworkProperties getMetadata() {
    return metadata;
  }

  public MosaicNetworkProperties getMosaic() {
    return mosaic;
  }

  public MultisigNetworkProperties getMultisig() {
    return multisig;
  }

  public NamespaceNetworkProperties getNamespace() {
    return namespace;
  }

  public AccountRestrictionNetworkProperties getRestrictionaccount() {
    return restrictionaccount;
  }

  public MosaicRestrictionNetworkProperties getRestrictionmosaic() {
    return restrictionmosaic;
  }

  public TransferNetworkProperties getTransfer() {
    return transfer;
  }
}
