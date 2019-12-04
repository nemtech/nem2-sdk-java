package io.nem.sdk.model.mosaic;

import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigDecimal;
import java.math.BigInteger;

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
     * Namespace id of `currency` namespace.
     */
    public static final NamespaceId NAMESPACEID = NamespaceId.createFromName("cat.currency");
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
     */
    public NetworkCurrencyMosaic(BigInteger amount) {
        super(NetworkCurrencyMosaic.NAMESPACEID, amount);
    }

    /**
     * Create xem with using xem as unit.
     *
     * @param amount amount to send
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkCurrencyMosaic createRelative(BigInteger amount) {

        BigInteger relativeAmount =
            BigDecimal.valueOf(Math.pow(10, NetworkCurrencyMosaic.DIVISIBILITY))
                .toBigInteger()
                .multiply(amount);
        return new NetworkCurrencyMosaic(relativeAmount);
    }

    /**
     * Create xem with using xem as unit.
     *
     * @param amount amount to send
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkCurrencyMosaic createRelative(BigDecimal amount) {

        BigInteger relativeAmount =
            BigDecimal.valueOf(Math.pow(10, NetworkCurrencyMosaic.DIVISIBILITY))
                .multiply(amount)
                .toBigInteger();
        return new NetworkCurrencyMosaic(relativeAmount);
    }

    /**
     * Create xem with using micro xem as unit, 1 NetworkCurrencyMosaic = 1000000 micro
     * NetworkCurrencyMosaic.
     *
     * @param amount amount to send
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkCurrencyMosaic createAbsolute(BigInteger amount) {
        return new NetworkCurrencyMosaic(amount);
    }
}
