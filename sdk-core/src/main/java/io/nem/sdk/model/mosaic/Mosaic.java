/*
 * Copyright 2018 NEM
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

import io.nem.sdk.model.transaction.UInt64Id;
import java.math.BigInteger;

/**
 * A mosaic describes an instance of a mosaic definition. Mosaics can be transferred by means of a
 * transfer transaction.
 *
 * @since 1.0
 */
public class Mosaic {

    private final UInt64Id id;
    private final BigInteger amount;

    public Mosaic(UInt64Id id, BigInteger amount) {
        this.id = id;
        this.amount = amount;
    }

    /**
     * Returns the mosaic identifier
     *
     * @return mosaic identifier
     */
    public UInt64Id getId() {
        return id;
    }

    /**
     * Returns mosaic id as a hexadecimal string
     *
     * @return id hex string
     */
    public String getIdAsHex() {
        return id.getIdAsHex();
    }

    /**
     * Return mosaic amount. The quantity is always given in smallest units for the mosaic i.e. if
     * it has a divisibility of 3 the quantity is given in millis.
     *
     * @return amount of mosaic
     */
    public BigInteger getAmount() {
        return amount;
    }

}
