/*
 * Copyright 2019 NEM
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
/**
 * NetworkCurrencyMosaic mosaic
 *
 * This represents the per-network currency mosaic. This mosaicId is aliased
 * with namespace name `cat.currency`.
 *
 * @since 1.0
 */

public class NetworkCurrencyMosaic extends Mosaic{
    /**
     * Divisibility
     */
    public static final int DIVISIBILITY = 6;

    /**
     * Initial supply
     */
    public static final BigInteger INITIALSUPPLY = new BigInteger("8999999998");

    /**
     * Is transferable
     */
    public static final boolean TRANSFERABLE = true;

    /**
     * Is supply mutable
     */
    public static final boolean SUPPLYMUTABLE = false;

    /**
     * Is Levy mutable
     */
    public static final boolean LEVY_MUTABLE = false;

    /**
     * Namespace id
     */
    public static final  NamespaceId NAMESPACEID = new NamespaceId("cat.currency");

    /**
     * Mosaic id
     */
    public static final MosaicId MOSAICID = new MosaicId(new BigInteger("0"));


    public NetworkCurrencyMosaic(BigInteger amount) {
        super(NetworkCurrencyMosaic.MOSAICID, amount);
    }

    /**
     * Create networkCurrencyMosaic with using networkCurrencyMosaic as unit.
     *
     * @param amount amount to send
     * @return a NetworkHarvestMosaic instance
     */
    public static NetworkCurrencyMosaic createRelative(BigInteger amount){
        BigInteger relativeAmount = new BigDecimal(Math.pow(10, NetworkCurrencyMosaic.DIVISIBILITY)).toBigInteger().multiply(amount);
        return new NetworkCurrencyMosaic(relativeAmount);
    }

    /**
     * Create NetworkCurrencyMosaic with using micro NetworkCurrencyMosaic as unit,
     * 1 NetworkCurrencyMosaic = 1000000 micro NetworkCurrencyMosaic.
     *
     * @param amount
     * @return NetworkCurrencyMosaic
     */
    public static NetworkCurrencyMosaic createAbsolute(BigInteger amount) {
        return new NetworkCurrencyMosaic(amount);
    }

}
