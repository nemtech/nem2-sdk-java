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
 * NetworkHarvestMosaic mosaic
 *
 * This represents the per-network harvest mosaic. This mosaicId is aliased
 * with namespace name `cat.harvest`.
 *
 * @since 1.0
 */
public class NetworkHarvestMosaic  extends Mosaic{
    /**
     * Divisibility
     */
    public static final int DIVISIBILITY = 3;

    /**
     * Initial supply
     */
    public static final BigInteger INITIALSUPPLY = new BigInteger("15000000");

    /**
     * Is transferable
     */
    public static final boolean TRANSFERABLE = true;

    /**
     * Is supply mutable
     */
    public static final boolean SUPPLYMUTABLE = true;

    /**
     * Is Levy mutable
     */
    public static final boolean LEVY_MUTABLE = false;

    /**
     * Namespace id
     */
    public static final  NamespaceId NAMESPACEID = new NamespaceId("cat.harvest");
    /**
     * Mosaic id
     */
    public static final MosaicId MOSAICID = new MosaicId(new BigInteger("1"));

    public NetworkHarvestMosaic(BigInteger amount) {
        super(NetworkHarvestMosaic.MOSAICID, amount);
    }

    /**
     * Create networkHarvestMosaic with using networkHarvestMosaic as unit.
     *
     * @param amount amount to send
     * @return a NetworkHarvestMosaic instance
     */
    public static NetworkHarvestMosaic createRelative(BigInteger amount){
        BigInteger relativeAmount = new BigDecimal(Math.pow(10, NetworkHarvestMosaic.DIVISIBILITY)).toBigInteger().multiply(amount);
        return new NetworkHarvestMosaic(relativeAmount);
    }

    /**
     * Create NetworkHarvestMosaic with using micro NetworkHarvestMosaic as unit,
     * 1 NetworkHarvestMosaic = 1000000 micro NetworkHarvestMosaic.
     *
     * @param amount
     * @return NetworkHarvestMosaic
     */
    public static NetworkHarvestMosaic createAbsolute(BigInteger amount) {
        return new NetworkHarvestMosaic(amount);
    }
}
