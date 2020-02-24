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

package io.nem.symbol.sdk.model.blockchain;

import java.util.Arrays;

/**
 * Static class containing network type constants.
 *
 * @since 1.0
 */
public enum NetworkType {
    /**
     * Main net network
     */
    MAIN_NET(104, "public"),
    /**
     * Test net network
     */
    TEST_NET(152, "publicTest"),
    /**
     * Mijin net network
     */
    MIJIN(96, "mijin"),
    /**
     * Mijin test net network
     */
    MIJIN_TEST(144, "mijinTest");

    private final int value;

    private final String networkName;

    NetworkType(int value, String networkName) {
        this.value = value;
        this.networkName = networkName;
    }

    /**
     * Static constructor converting network raw value to enum instance.
     *
     * @param value the low level int value.
     * @return {@link NetworkType}
     */
    public static NetworkType rawValueOf(int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

    /**
     * Static constructor converting network name to enum instance.
     *
     * @param networkName the networkName.
     * @return {@link NetworkType}
     */
    public static NetworkType rawValueOf(String networkName) {
        return Arrays.stream(values()).filter(e -> e.getNetworkName().equals(networkName))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(networkName + " is not a valid network name"));
    }

    /**
     * Returns enum value.
     *
     * @return int
     */
    public int getValue() {
        return this.value;
    }

    /**
     * @return the network name.
     */
    public String getNetworkName() {
        return networkName;
    }
}
