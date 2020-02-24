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

package io.nem.symbol.core.crypto;

import io.nem.symbol.core.crypto.ed25519.Ed25519CryptoEngine;

/**
 * Static class that exposes crypto engines.
 */
public class CryptoEngines {

    private static final CryptoEngine ED25519_ENGINE;
    private static final CryptoEngine DEFAULT_ENGINE;

    static {
        ED25519_ENGINE = new Ed25519CryptoEngine();
        DEFAULT_ENGINE = ED25519_ENGINE;
    }

    /**
     * Private constructor for this utility class.
     */
    private CryptoEngines() {
    }

    /**
     * Gets the default crypto engine.
     *
     * @return The default crypto engine.
     */
    public static CryptoEngine defaultEngine() {
        return DEFAULT_ENGINE;
    }

    /**
     * Gets the ED25519 crypto engine.
     *
     * @return The ED25519 crypto engine.
     */
    public static CryptoEngine ed25519Engine() {
        return ED25519_ENGINE;
    }
}
