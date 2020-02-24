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

/**
 * Mosaic alias transaction.
 */
public class MosaicAliasTransaction extends Transaction {

    private final AliasAction aliasAction;
    private final NamespaceId namespaceId;
    private final MosaicId mosaicId;

    /**
     * Crates a {@link MosaicAliasTransaction} based on the factory.
     *
     * @param factory the factory.
     */
    MosaicAliasTransaction(MosaicAliasTransactionFactory factory) {
        super(factory);
        this.aliasAction = factory.getAliasAction();
        this.namespaceId = factory.getNamespaceId();
        this.mosaicId = factory.getMosaicId();
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

}
