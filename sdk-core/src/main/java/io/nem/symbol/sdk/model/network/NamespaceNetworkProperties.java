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
 * NamespaceNetworkProperties
 */
public class NamespaceNetworkProperties {

    /**
     * Maximum namespace name size.
     **/
    private final String maxNameSize;
    /**
     * Maximum number of children for a root namespace.
     **/
    private final String maxChildNamespaces;

    /**
     * Maximum namespace depth.
     **/
    private final String maxNamespaceDepth;
    /**
     * Minimum namespace duration.
     **/
    private final String minNamespaceDuration;
    /**
     * Maximum namespace duration.
     **/
    private final String maxNamespaceDuration;
    /**
     * Grace period during which time only the previous owner can renew an expired namespace.
     **/
    private final String namespaceGracePeriodDuration;
    /**
     * Reserved root namespaces that cannot be claimed.
     **/
    private final String reservedRootNamespaceNames;
    /**
     * Public key.
     **/
    private final String namespaceRentalFeeSinkPublicKey;

    /**
     * Root namespace rental fee per block.
     **/
    private final String rootNamespaceRentalFeePerBlock;

    /**
     * Child namespace rental fee.
     **/
    private final String childNamespaceRentalFee;


  public NamespaceNetworkProperties(String maxNameSize, String maxChildNamespaces,
      String maxNamespaceDepth, String minNamespaceDuration, String maxNamespaceDuration,
      String namespaceGracePeriodDuration, String reservedRootNamespaceNames,
      String namespaceRentalFeeSinkPublicKey, String rootNamespaceRentalFeePerBlock,
      String childNamespaceRentalFee) {
    this.maxNameSize = maxNameSize;
    this.maxChildNamespaces = maxChildNamespaces;
    this.maxNamespaceDepth = maxNamespaceDepth;
    this.minNamespaceDuration = minNamespaceDuration;
    this.maxNamespaceDuration = maxNamespaceDuration;
    this.namespaceGracePeriodDuration = namespaceGracePeriodDuration;
    this.reservedRootNamespaceNames = reservedRootNamespaceNames;
    this.namespaceRentalFeeSinkPublicKey = namespaceRentalFeeSinkPublicKey;
    this.rootNamespaceRentalFeePerBlock = rootNamespaceRentalFeePerBlock;
    this.childNamespaceRentalFee = childNamespaceRentalFee;
  }

  public String getMaxNameSize() {
    return maxNameSize;
  }

  public String getMaxChildNamespaces() {
    return maxChildNamespaces;
  }

  public String getMaxNamespaceDepth() {
    return maxNamespaceDepth;
  }

  public String getMinNamespaceDuration() {
    return minNamespaceDuration;
  }

  public String getMaxNamespaceDuration() {
    return maxNamespaceDuration;
  }

  public String getNamespaceGracePeriodDuration() {
    return namespaceGracePeriodDuration;
  }

  public String getReservedRootNamespaceNames() {
    return reservedRootNamespaceNames;
  }

  public String getNamespaceRentalFeeSinkPublicKey() {
    return namespaceRentalFeeSinkPublicKey;
  }

  public String getRootNamespaceRentalFeePerBlock() {
    return rootNamespaceRentalFeePerBlock;
  }

  public String getChildNamespaceRentalFee() {
    return childNamespaceRentalFee;
  }
}

