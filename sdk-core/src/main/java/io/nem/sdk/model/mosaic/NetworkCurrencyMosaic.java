package io.nem.sdk.model.mosaic;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

/**
 * NetworkCurrencyMosaic mosaic
 *
 * <p>This represents the per-network currency mosaic. This mosaicId is aliased with namespace name
 * `cat.currency`.
 *
 * @since 0.10.2
 */
public class NetworkCurrencyMosaic extends Mosaic {

    /**
     * Generates a Namespace id of `currency` namespace depending on the NetworkType.
     */
    public static final Function<NetworkType, NamespaceId> NAMESPACE_ID_RESOLVER = networkType -> NamespaceId
        .createFromName("cat.currency", networkType);
    /**
     * Divisibility
     */
    public static final int DIVISIBILITY = 6;
    /**
     * Initial supply
     */
    public static final BigInteger INITIALSUPPLY = BigInteger.valueOf(8999999999L);
    /**
     * Is transferable
     */
    public static final boolean TRANSFERABLE = true;
    /**
     * Is supply mutable
     */
    public static final boolean SUPPLYMUTABLE = false;

    /**
     * @param amount the mosaic amount.
     * @param networkType the network type.
     */
    public NetworkCurrencyMosaic(BigInteger amount, NetworkType networkType) {
        super(NetworkCurrencyMosaic.NAMESPACE_ID_RESOLVER.apply(networkType), amount);
    }

    /**
     * Create xem with using xem as unit.
     *
     * @param amount amount to send
     * @param networkType the network type.
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkCurrencyMosaic createRelative(BigInteger amount, NetworkType networkType) {

        BigInteger relativeAmount =
            BigDecimal.valueOf(Math.pow(10, NetworkCurrencyMosaic.DIVISIBILITY))
                .toBigInteger()
                .multiply(amount);
        return new NetworkCurrencyMosaic(relativeAmount, networkType);
    }

    /**
     * Create xem with using xem as unit.
     *
     * @param amount amount to send
     * @param networkType the network type
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkCurrencyMosaic createRelative(BigDecimal amount,
        NetworkType networkType) {

        BigInteger relativeAmount =
            BigDecimal.valueOf(Math.pow(10, NetworkCurrencyMosaic.DIVISIBILITY))
                .multiply(amount)
                .toBigInteger();
        return new NetworkCurrencyMosaic(relativeAmount, networkType);
    }

    /**
     * Create xem with using micro xem as unit, 1 NetworkCurrencyMosaic = 1000000 micro
     * NetworkCurrencyMosaic.
     *
     * @param amount amount to send
     * @param networkType the network type.
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkCurrencyMosaic createAbsolute(BigInteger amount,
        NetworkType networkType) {

        return new NetworkCurrencyMosaic(amount, networkType);
    }
}
