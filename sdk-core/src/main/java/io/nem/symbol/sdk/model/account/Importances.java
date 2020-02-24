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

package io.nem.symbol.sdk.model.account;

import java.math.BigInteger;

/**
 * Importances of the account
 */
public class Importances {

    private BigInteger value;
    private BigInteger height;

    /**
     * Constructor.
     *
     * @param value Value
     * @param height Height
     */
    public Importances(BigInteger value, BigInteger height) {
        this.value = value;
        this.height = height;
    }

    /**
     * Gets height.
     *
     * @return Height
     */
    public BigInteger getHeight() {
        return height;
    }

    /**
     * Gets value.
     *
     * @return Value.
     */
    public BigInteger getValue() {
        return value;
    }
}
