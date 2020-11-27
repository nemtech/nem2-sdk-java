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
package io.nem.symbol.sdk.model.namespace;

import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.HeightDto;
import io.nem.symbol.catapult.builders.NamespaceAliasBuilder;
import io.nem.symbol.catapult.builders.NamespaceIdDto;
import io.nem.symbol.catapult.builders.NamespaceLifetimeBuilder;
import io.nem.symbol.catapult.builders.NamespacePathBuilder;
import io.nem.symbol.catapult.builders.RootNamespaceHistoryBuilder;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.account.Address;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;

/**
 * NamespaceInfo contains the state information of a namespace.
 *
 * @since 1.0
 */
public class NamespaceInfo implements Stored {

  private final String recordId;
  private final int version;
  private final boolean active;
  private final Integer index;
  private final NamespaceRegistrationType registrationType;
  private final Integer depth;
  private final List<NamespaceId> levels;
  private final NamespaceId parentId;
  private final Address ownerAddress;
  private final BigInteger startHeight;
  private final BigInteger endHeight;
  private final Alias<?> alias;

  @SuppressWarnings("squid:S00107")
  public NamespaceInfo(
      String recordId,
      int version,
      boolean active,
      Integer index,
      NamespaceRegistrationType registrationType,
      Integer depth,
      List<NamespaceId> levels,
      NamespaceId parentId,
      Address ownerAddress,
      BigInteger startHeight,
      BigInteger endHeight,
      Alias<?> alias) {

    Validate.notNull(index, "index is required");
    Validate.notNull(registrationType, "registrationType is required");
    Validate.notNull(depth, "depth is required");
    Validate.notNull(ownerAddress, "ownerAddress is required");
    Validate.notNull(startHeight, "startHeight is required");
    Validate.notNull(endHeight, "endHeight is required");
    Validate.notNull(alias, "alias is required");

    this.recordId = recordId;
    this.version = version;
    this.active = active;
    this.index = index;
    this.registrationType = registrationType;
    this.depth = depth;
    this.levels = levels;
    this.parentId = parentId;
    this.ownerAddress = ownerAddress;
    this.startHeight = startHeight;
    this.endHeight = endHeight;
    this.alias = alias;
  }

  /**
   * The record database id.
   *
   * @return the parent id.
   */
  public Optional<String> getRecordId() {
    return Optional.ofNullable(recordId);
  }

  /**
   * Returns true if namespace is active
   *
   * @return true if the namespace is active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Returns the namespace expiration status
   *
   * @return true if namespace is expired
   */
  public boolean isExpired() {
    return !active;
  }

  /**
   * The namespace meta index
   *
   * @return the index.
   */
  public Integer getIndex() {
    return index;
  }

  /**
   * Returns the namespace type
   *
   * @return the namespace type
   */
  public NamespaceRegistrationType getRegistrationType() {
    return registrationType;
  }

  /**
   * Returns the namespace level depth
   *
   * @return the namespace level depth
   */
  public Integer getDepth() {
    return depth;
  }

  /**
   * Returns the different NamespaceIds per level
   *
   * @return the different Namespace IDs per level
   */
  public List<NamespaceId> getLevels() {
    return levels;
  }

  /**
   * Returns the namespace owner address
   *
   * @return mosaic namespace owner
   */
  public Address getOwnerAddress() {
    return ownerAddress;
  }

  /**
   * Returns the block height the namespace was registered
   *
   * @return the block height the namespace was registered
   */
  public BigInteger getStartHeight() {
    return startHeight;
  }

  /**
   * Returns the block height the namespace expires if not renewed
   *
   * @return the block height the namespace expires
   */
  public BigInteger getEndHeight() {
    return endHeight;
  }

  /**
   * Returns the NamespaceId
   *
   * @return namespace id
   */
  public NamespaceId getId() {
    return this.levels.get(this.levels.size() - 1);
  }

  /**
   * Returns true if namespace is Root
   *
   * @return true if namespace is Root
   */
  public boolean isRoot() {
    return this.registrationType == NamespaceRegistrationType.ROOT_NAMESPACE;
  }

  /**
   * Returns the Alias
   *
   * @return alias
   */
  public Alias<?> getAlias() {
    return alias;
  }

  /**
   * Returns true if namespace has Alias
   *
   * @return true if namespace has Alias
   */
  public boolean hasAlias() {
    return !this.alias.isEmpty();
  }

  /**
   * Returns true if namespace is Subnamespace
   *
   * @return true if namespace is Subnamespace
   */
  public boolean isSubnamespace() {
    return this.registrationType == NamespaceRegistrationType.SUB_NAMESPACE;
  }

  /** @return The state version */
  public int getVersion() {
    return version;
  }

  /**
   * Returns the Parent Namespace Id
   *
   * @return the Parent Namespace Idpace
   */
  public NamespaceId parentNamespaceId() {
    if (this.isRoot()) {
      throw new IllegalStateException("Is A Root Namespace");
    }
    return this.parentId;
  }

  /** @return serializes the state of this object. */
  public byte[] serialize(List<NamespaceInfo> fullPath) {
    if (!this.isRoot()) {
      throw new IllegalArgumentException("Namespace must be root in order to serialize!");
    }
    List<NamespaceInfo> children = sortList(fullPath, this.getId());
    if (fullPath.size() != children.size()) {
      throw new IllegalArgumentException(
          "Some of the children do not belong to this root namespace");
    }

    NamespaceIdDto id = new NamespaceIdDto(getId().getIdAsLong());
    AddressDto ownerAddress = SerializationUtils.toAddressDto(getOwnerAddress());
    NamespaceLifetimeBuilder lifetime =
        NamespaceLifetimeBuilder.create(
            new HeightDto(getStartHeight().longValue()), new HeightDto(getEndHeight().longValue()));
    NamespaceAliasBuilder rootAlias = getAlias().createAliasBuilder();
    List<NamespacePathBuilder> paths =
        children.stream().map(this::toNamespaceAliasTypeDto).collect(Collectors.toList());
    RootNamespaceHistoryBuilder builder =
        RootNamespaceHistoryBuilder.create(
            (short) getVersion(), id, ownerAddress, lifetime, rootAlias, paths);

    return builder.serialize();
  }

  private static List<NamespaceInfo> sortList(
      final List<NamespaceInfo> nodes, NamespaceId parentId) {
    return treeAdd(nodes, parentId, new ArrayList<>());
  }

  private static List<NamespaceInfo> treeAdd(
      List<NamespaceInfo> nodes, NamespaceId parentId, List<NamespaceInfo> treeList) {
    nodes.stream()
        .filter(r -> r.isSubnamespace() && r.parentNamespaceId().equals(parentId))
        .sorted(Comparator.comparing(n -> n.getId().getId()))
        .forEach(
            n -> {
              treeList.add(n);
              treeAdd(nodes, n.getId(), treeList);
            });
    return treeList;
  }

  private NamespacePathBuilder toNamespaceAliasTypeDto(NamespaceInfo namespaceInfo) {
    List<NamespaceIdDto> path =
        namespaceInfo.getLevels().stream()
            .skip(1)
            .map(id -> new NamespaceIdDto(id.getIdAsLong()))
            .collect(Collectors.toList());
    NamespaceAliasBuilder alias = namespaceInfo.getAlias().createAliasBuilder();
    return NamespacePathBuilder.create(path, alias);
  }
}
