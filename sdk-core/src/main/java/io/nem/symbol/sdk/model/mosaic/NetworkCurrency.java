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
package io.nem.symbol.sdk.model.mosaic;

import io.nem.symbol.sdk.api.NetworkCurrencyService;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.infrastructure.RepositoryFactoryBase;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 * This object holds the configuration of a given currency. The configuration can be provided by
 * user (offline work) or loaded via rest (online work, currently using block/1 transaction).
 *
 * <p>Some commonly used and known instances are also provided statically if the user wants to work
 * offline.
 *
 * <p>Objects of this class are created using the {@link NetworkCurrencyBuilder} and they are thread
 * safe and immutable.
 *
 * @see NetworkCurrencyService
 * @see RepositoryFactory
 * @see RepositoryFactoryBase
 * @see NetworkCurrencyBuilder
 */
public class NetworkCurrency {

  /** The original public bootstrap network currency. */
  public static final NetworkCurrency CAT_CURRENCY =
      new NetworkCurrencyBuilder(NamespaceId.createFromName("cat.currency"), 6)
          .withSupplyMutable(false)
          .withTransferable(true)
          .build();
  /** The original public bootstrap havest currency. */
  public static final NetworkCurrency CAT_HARVEST =
      new NetworkCurrencyBuilder(NamespaceId.createFromName("cat.harvest"), 3)
          .withSupplyMutable(true)
          .withTransferable(true)
          .build();

  /** The new public network currency. */
  public static final NetworkCurrency SYMBOL_XYM =
      new NetworkCurrencyBuilder(NamespaceId.createFromName("symbol.xym"), 6)
          .withSupplyMutable(false)
          .withTransferable(true)
          .build();

  /**
   * The selected unresolved mosaic id used when creating {@link Mosaic}. This could either be the
   * Namespace or the Mosaic id.
   */
  private final UnresolvedMosaicId unresolvedMosaicId;

  /**
   * Mosaic id of this currency. This value is optional if the user only wants to provide the mosaic
   * id. This value will be set if it's loaded by rest.
   */
  private final Optional<MosaicId> mosaicId;
  /**
   * The Namespace id of this currency. This value is option if the user only wants to provide the
   * namespace id. This value will be set if it's loaded by rest.
   */
  private final Optional<NamespaceId> namespaceId;
  /** Divisibility of this currency, required to create Mosaic from relative amounts. */
  private final int divisibility;

  /** Is this currency transferable. */
  private final boolean transferable;
  /** Is this currency supply mutable. */
  private final boolean supplyMutable;

  /**
   * User would create these objects using the builder.
   *
   * @param builder the builder.
   * @see NetworkType
   */
  NetworkCurrency(NetworkCurrencyBuilder builder) {
    Validate.notNull(builder, "builder must not be null");
    this.unresolvedMosaicId = builder.getUnresolvedMosaicId();
    this.mosaicId = builder.getMosaicId();
    this.namespaceId = builder.getNamespaceId();
    this.divisibility = builder.getDivisibility();
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
  public Mosaic createRelative(double amount) {
    return createRelative(BigDecimal.valueOf(amount));
  }

  /**
   * Create xem with using xem as unit.
   *
   * @param amount amount to send
   * @return a NetworkCurrencyMosaic instance
   */
  public Mosaic createRelative(long amount) {
    return createRelative(BigInteger.valueOf(amount));
  }

  /**
   * Create xem with using xem as unit.
   *
   * @param amount amount to send
   * @return a NetworkCurrencyMosaic instance
   */
  public Mosaic createRelative(BigDecimal amount) {
    BigInteger relativeAmount =
        BigDecimal.valueOf(Math.pow(10, getDivisibility())).multiply(amount).toBigInteger();
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
        BigDecimal.valueOf(Math.pow(10, getDivisibility())).toBigInteger().multiply(amount);
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

  /**
   * Create xem with using micro xem as unit, 1 NetworkCurrencyMosaic = 1000000 micro
   * NetworkCurrencyMosaic.
   *
   * @param amount amount to send
   * @return a NetworkCurrencyMosaic instance
   */
  public Mosaic createAbsolute(long amount) {
    return createAbsolute(BigInteger.valueOf(amount));
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
    return divisibility == that.divisibility
        && transferable == that.transferable
        && supplyMutable == that.supplyMutable
        && Objects.equals(unresolvedMosaicId, that.unresolvedMosaicId)
        && Objects.equals(mosaicId, that.mosaicId)
        && Objects.equals(namespaceId, that.namespaceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        unresolvedMosaicId, mosaicId, namespaceId, divisibility, transferable, supplyMutable);
  }

  @Override
  public String toString() {
    return "NetworkCurrency{"
        + "unresolvedMosaicId="
        + unresolvedMosaicId
        + ", mosaicId="
        + mosaicId
        + ", namespaceId="
        + namespaceId
        + ", divisibility="
        + divisibility
        + ", transferable="
        + transferable
        + ", supplyMutable="
        + supplyMutable
        + '}';
  }
}
