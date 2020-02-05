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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 *
 */
public class NetworkCurrency {

    public static final NetworkCurrency CAT_CURRENCY = new NetworkCurrencyBuilder(
        NamespaceId.createFromName("cat.currency"), 6)
        .withInitialSupply(BigInteger.valueOf(8999999999L)).withSupplyMutable(false)
        .withTransferable(true).build();

    public static final NetworkCurrency CAT_HARVEST = new NetworkCurrencyBuilder(
        NamespaceId.createFromName("cat.harvest"), 3)
        .withInitialSupply(BigInteger.valueOf(15000000)).withSupplyMutable(true)
        .withTransferable(true).build();


    public static final NetworkCurrency SYMBOL_XYM = new NetworkCurrencyBuilder(
        NamespaceId.createFromName("symbol.xym"), 6)
        .withInitialSupply(BigInteger.valueOf(7831975436000000L)).withSupplyMutable(false)
        .withTransferable(true).build();

    /**
     * The selected unresolved mosaic id used when creating {@link Mosaic}. This could either be the
     * Namespace or the Mosaic id of the
     */
    private final UnresolvedMosaicId unresolvedMosaicId;

    /**
     * Namespace id of `currency` namespace.
     */
    private final Optional<MosaicId> mosaicId;
    /**
     * Namespace id of `currency` namespace.
     */
    private final Optional<NamespaceId> namespaceId;
    /**
     * Divisibility
     */
    private final int divisibility;
    /**
     * Initial supply
     */
    private final BigInteger initialSupply;
    /**
     * Is transferable
     */
    private final boolean transferable;
    /**
     * Is supply mutable
     */
    private final boolean supplyMutable;

    NetworkCurrency(NetworkCurrencyBuilder builder) {
        Validate.notNull(builder, "builder must not be null");
        this.unresolvedMosaicId = builder.getUnresolvedMosaicId();
        this.mosaicId = builder.getMosaicId();
        this.namespaceId = builder.getNamespaceId();
        this.divisibility = builder.getDivisibility();
        this.initialSupply = builder.getInitialSupply();
        this.transferable = builder.isTransferable();
        this.supplyMutable = builder.isSupplyMutable();
    }

    public UnresolvedMosaicId getUnresolvedMosaicId() {
        return unresolvedMosaicId;
    }

    public Optional<MosaicId> getMosaicId() {
        return mosaicId;
    }

    public Optional<NamespaceId> getNamespaceId() {
        return namespaceId;
    }

    public int getDivisibility() {
        return divisibility;
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

    /**
     * Create xem with using xem as unit.
     *
     * @param amount amount to send
     * @return a NetworkCurrencyMosaic instance
     */
    public Mosaic createRelative(BigDecimal amount) {
        BigInteger relativeAmount =
            BigDecimal.valueOf(Math.pow(10, getDivisibility()))
                .multiply(amount)
                .toBigInteger();
        return new Mosaic(getUnresolvedMosaicId(), relativeAmount);
    }

    /**
     * Create xem with using xem as unit.
     *
     * @param amount amount to send
     * @return a NetworkCurrencyMosaic instance
     */
    public Mosaic createRelative(BigInteger amount) {
        BigInteger relativeAmount =
            BigDecimal.valueOf(Math.pow(10, getDivisibility()))
                .toBigInteger()
                .multiply(amount);
        return new Mosaic(getUnresolvedMosaicId(), relativeAmount);
    }

    /**
     * Create xem with using micro xem as unit, 1 NetworkCurrencyMosaic = 1000000 micro
     * NetworkCurrencyMosaic.
     *
     * @param amount amount to send
     * @return a NetworkCurrencyMosaic instance
     */
    public Mosaic createAbsolute(BigInteger amount) {
        return new Mosaic(getUnresolvedMosaicId(), amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetworkCurrency that = (NetworkCurrency) o;
        return divisibility == that.divisibility &&
            transferable == that.transferable &&
            supplyMutable == that.supplyMutable &&
            Objects.equals(unresolvedMosaicId, that.unresolvedMosaicId) &&
            Objects.equals(mosaicId, that.mosaicId) &&
            Objects.equals(namespaceId, that.namespaceId) &&
            Objects.equals(initialSupply, that.initialSupply);
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(unresolvedMosaicId, mosaicId, namespaceId, divisibility, initialSupply,
                transferable,
                supplyMutable);
    }
}
