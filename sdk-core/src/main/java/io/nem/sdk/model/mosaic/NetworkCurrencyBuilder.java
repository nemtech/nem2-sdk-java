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

package io.nem.sdk.model.mosaic;

import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

public final class NetworkCurrencyBuilder {

    private final UnresolvedMosaicId unresolvedMosaicId;
    private final int divisibility;
    private Optional<MosaicId> mosaicId = Optional.empty();
    private Optional<NamespaceId> namespaceId = Optional.empty();
    private BigInteger initialSupply = BigInteger.ZERO;
    private boolean transferable = true;
    private boolean supplyMutable = false;

    public NetworkCurrencyBuilder(UnresolvedMosaicId unresolvedMosaicId, int divisibility) {
        Validate.notNull(unresolvedMosaicId, "unresolvedMosaicId must not be null");
        Validate.isTrue(divisibility > 0, "divisibility must be greater than 0");
        this.unresolvedMosaicId = unresolvedMosaicId;
        this.divisibility = divisibility;
        if (unresolvedMosaicId.isAlias()) {
            withNamespaceId((NamespaceId) unresolvedMosaicId);
        } else {
            withMosaicId((MosaicId) unresolvedMosaicId);
        }
    }

    public NetworkCurrencyBuilder withMosaicId(MosaicId mosaicId) {
        Validate.notNull(mosaicId, "mosaicId must not be null");
        this.mosaicId = Optional.of(mosaicId);
        return this;
    }

    public NetworkCurrencyBuilder withNamespaceId(NamespaceId namespaceId) {
        Validate.notNull(namespaceId, "namespaceId must not be null");
        this.namespaceId = Optional.of(namespaceId);
        return this;
    }

    public NetworkCurrencyBuilder withInitialSupply(BigInteger initialSupply) {
        Validate.notNull(initialSupply, "initialSupply must not be null");
        this.initialSupply = initialSupply;
        return this;
    }

    public NetworkCurrencyBuilder withTransferable(boolean transferable) {
        this.transferable = transferable;
        return this;
    }

    public NetworkCurrencyBuilder withSupplyMutable(boolean supplyMutable) {
        this.supplyMutable = supplyMutable;
        return this;
    }

    public NetworkCurrency build() {
        return new NetworkCurrency(this);
    }

    public UnresolvedMosaicId getUnresolvedMosaicId() {
        return unresolvedMosaicId;
    }

    public int getDivisibility() {
        return divisibility;
    }

    public Optional<MosaicId> getMosaicId() {
        return mosaicId;
    }

    public Optional<NamespaceId> getNamespaceId() {
        return namespaceId;
    }

    public BigInteger getInitialSupply() {
        return initialSupply;
    }

    public boolean isTransferable() {
        return transferable;
    }

    public boolean isSupplyMutable() {
        return supplyMutable;
    }
}
