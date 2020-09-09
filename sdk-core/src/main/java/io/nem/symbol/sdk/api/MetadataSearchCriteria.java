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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.util.Objects;

/** Criteria used to search metadata entries */
public class MetadataSearchCriteria extends SearchCriteria<MetadataSearchCriteria> {

  /** Filter by the address of the account that created the metadata. */
  private Address sourceAddress;
  /** Filter by the metadata key. */
  private BigInteger scopedMetadataKey;
  /** Filter by metadata type. */
  private MetadataType metadataType;

  /** Filter by target address. */
  private Address targetAddress;

  /** Filter by either target namespace id or mosaic id. */
  private String targetId;

  public Address getSourceAddress() {
    return sourceAddress;
  }

  public void setSourceAddress(Address sourceAddress) {
    this.sourceAddress = sourceAddress;
  }

  /**
   * Sets the source address filter builder style.
   *
   * @param sourceAddress filter by source address.
   * @return this builder
   */
  public MetadataSearchCriteria sourceAddress(Address sourceAddress) {
    this.sourceAddress = sourceAddress;
    return this;
  }

  public Address getTargetAddress() {
    return targetAddress;
  }

  public void setTargetAddress(Address targetAddress) {
    this.targetAddress = targetAddress;
  }

  /**
   * Sets the target address filter builder style.
   *
   * @param targetAddress filter by target address.
   * @return this builder
   */
  public MetadataSearchCriteria targetAddress(Address targetAddress) {
    this.targetAddress = targetAddress;
    return this;
  }

  public BigInteger getScopedMetadataKey() {
    return scopedMetadataKey;
  }

  public void setScopedMetadataKey(BigInteger scopedMetadataKey) {
    this.scopedMetadataKey = scopedMetadataKey;
  }

  /**
   * Sets the metadata key filter builder style.
   *
   * @param scopedMetadataKey filter by metadata key.
   * @return this builder
   */
  public MetadataSearchCriteria scopedMetadataKey(BigInteger scopedMetadataKey) {
    this.scopedMetadataKey = scopedMetadataKey;
    return this;
  }

  public MetadataType getMetadataType() {
    return metadataType;
  }

  public void setMetadataType(MetadataType metadataType) {
    this.metadataType = metadataType;
  }

  /**
   * Sets the metadata type filter builder style.
   *
   * @param metadataType filter by metadata type.
   * @return this builder
   */
  public MetadataSearchCriteria metadataType(MetadataType metadataType) {
    this.metadataType = metadataType;
    return this;
  }

  /**
   * Sets the target id filter builder style.
   *
   * @param targetMosaicId filter by mosaic target id.
   * @return this builder
   */
  public MetadataSearchCriteria targetId(MosaicId targetMosaicId) {
    this.targetId = targetMosaicId.getIdAsHex();
    return this;
  }

  /**
   * Sets the target id filter builder style.
   *
   * @param targetNamespaceId filter by namesapce target id.
   * @return this builder
   */
  public MetadataSearchCriteria targetId(NamespaceId targetNamespaceId) {
    this.targetId = targetNamespaceId.getIdAsHex();
    return this;
  }

  public void setTargetId(String targetId) {
    this.targetId = targetId;
  }

  public String getTargetId() {
    return targetId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetadataSearchCriteria that = (MetadataSearchCriteria) o;
    return Objects.equals(sourceAddress, that.sourceAddress)
        && Objects.equals(scopedMetadataKey, that.scopedMetadataKey)
        && metadataType == that.metadataType
        && Objects.equals(targetAddress, that.targetAddress)
        && Objects.equals(targetId, that.targetId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceAddress, scopedMetadataKey, metadataType, targetAddress, targetId);
  }
}
