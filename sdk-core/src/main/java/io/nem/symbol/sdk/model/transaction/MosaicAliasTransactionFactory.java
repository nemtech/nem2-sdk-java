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

import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.apache.commons.lang3.Validate;

/** Factory of {@link MosaicAliasTransaction} */
public class MosaicAliasTransactionFactory extends TransactionFactory<MosaicAliasTransaction> {

  private final AliasAction aliasAction;
  private final NamespaceId namespaceId;
  private final MosaicId mosaicId;

  private MosaicAliasTransactionFactory(
      final NetworkType networkType,
      final AliasAction aliasAction,
      final NamespaceId namespaceId,
      final MosaicId mosaicId) {
    super(TransactionType.MOSAIC_ALIAS, networkType);
    Validate.notNull(namespaceId, "namespaceId must not be null");
    Validate.notNull(mosaicId, "mosaicId must not be null");

    this.aliasAction = aliasAction;
    this.namespaceId = namespaceId;
    this.mosaicId = mosaicId;
  }

  /**
   * Static create method for factory.
   *
   * @param networkType Network type.
   * @param aliasAction Alias action.
   * @param namespaceId Namespace id.
   * @param mosaicId Mosaic id.
   * @return Mosaic alias transaction.
   */
  public static MosaicAliasTransactionFactory create(
      NetworkType networkType,
      AliasAction aliasAction,
      NamespaceId namespaceId,
      MosaicId mosaicId) {
    return new MosaicAliasTransactionFactory(networkType, aliasAction, namespaceId, mosaicId);
  }

  /**
   * Gets the alias action.
   *
   * @return Alias Action.
   */
  public AliasAction getAliasAction() {
    return this.aliasAction;
  }

  /**
   * Gets the namespace id.
   *
   * @return Namespace id.
   */
  public NamespaceId getNamespaceId() {
    return this.namespaceId;
  }

  /**
   * Gets the mosiac id.
   *
   * @return Mosaic id.
   */
  public MosaicId getMosaicId() {
    return this.mosaicId;
  }

  @Override
  public MosaicAliasTransaction build() {
    return new MosaicAliasTransaction(this);
  }
}
