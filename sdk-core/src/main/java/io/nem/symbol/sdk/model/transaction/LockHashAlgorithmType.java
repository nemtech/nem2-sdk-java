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
package io.nem.symbol.sdk.model.transaction;

import java.util.Arrays;

/**
 * Enum containing hash type.
 *
 * @since 1.0
 */
public enum LockHashAlgorithmType {

    /**
     * hashed using SHA3-256 (Catapult Native)
     */
    SHA3_256(0),
    /**
     * hashed using Keccak-256 (ETH Compat)
     */
    KECCAK_256(1),
    /**
     * hashed twice: first with SHA-256 and then with RIPEMD-160 (BTC Compat)
     */
    HASH_160(2),
    /**
     * Hashed twice with SHA-256 (BTC Compat)
     */
    HASH_256(3);

    /**
     * The regex used to validate a hashed value.
     */
    public static final String VALIDATOR_REGEX = "-?[0-9a-fA-F]+";

    private final int value;

    LockHashAlgorithmType(int value) {
        this.value = value;
    }

    public static LockHashAlgorithmType rawValueOf(int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

    /**
     * Validate hash algorithm and hash have desired format
     *
     * @param hashType Hash type
     * @param input Input hashed
     * @return boolean when format is correct
     */
    public static boolean validator(LockHashAlgorithmType hashType, String input) {
        if (!input.matches(VALIDATOR_REGEX)) {
            return false;
        }
        switch (hashType) {
            case SHA3_256:
            case KECCAK_256:
            case HASH_256:
                return input.length() == 64;
            case HASH_160:
                return input.length() == 64 || input.length() == 40;
        }
        return false;
    }

    public int getValue() {
        return value;
    }
}
