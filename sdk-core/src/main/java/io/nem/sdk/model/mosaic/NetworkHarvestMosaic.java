package io.nem.sdk.model.mosaic;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

/**
 * NetworkHarvestMosaic mosaic
 *
 * <p>This represents the per-network harvest mosaic. This mosaicId is aliased with namespace name
 * `cat.harvest`.
 *
 * @since 0.10.2
 */
public class NetworkHarvestMosaic extends Mosaic {

    /**
     * Generates a namespace id of `currency` namespace depending on the network type.
     */
    public static final Function<NetworkType, NamespaceId> NAMESPACE_ID_RESOLVER = (networkType) -> NamespaceId
        .createFromName("cat.harvest",
            networkType);
    /**
     * Divisibility
     */
    public static final int DIVISIBILITY = 3;
    /**
     * Initial supply
     */
    public static final BigInteger INITIALSUPPLY = BigInteger.valueOf(15000000);
    /**
     * Is transferable
     */
    public static final boolean TRANSFERABLE = true;
    /**
     * Is supply mutable
     */
    public static final boolean SUPPLYMUTABLE = true;

    /**
     * @param amount the mosaic amount.
     * @param networkType the network type
     */
    public NetworkHarvestMosaic(BigInteger amount,
        NetworkType networkType) {
        super(NetworkHarvestMosaic.NAMESPACE_ID_RESOLVER.apply(networkType), amount);
    }

    /**
     * Create xem with using xem as unit.
     *
     * @param amount amount to send
     * @param networkType the network type.
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkHarvestMosaic createRelative(BigInteger amount,
        NetworkType networkType) {
        BigInteger relativeAmount =
            BigDecimal.valueOf(Math.pow(10, NetworkHarvestMosaic.DIVISIBILITY))
                .toBigInteger()
                .multiply(amount);
        return new NetworkHarvestMosaic(relativeAmount, networkType);
    }

    /**
     * Create xem with using micro xem as unit, 1 NetworkCurrencyMosaic = 1000000 micro
     * NetworkCurrencyMosaic.
     *
     * @param amount amount to send
     * @param networkType network type
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkHarvestMosaic createAbsolute(BigInteger amount,
        NetworkType networkType) {
        return new NetworkHarvestMosaic(amount, networkType);
    }
}
