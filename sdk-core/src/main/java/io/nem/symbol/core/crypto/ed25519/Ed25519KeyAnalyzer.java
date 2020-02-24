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

package io.nem.symbol.core.crypto.ed25519;

import io.nem.symbol.core.crypto.KeyAnalyzer;
import io.nem.symbol.core.crypto.PublicKey;

/**
 * Implementation of the key analyzer for Ed25519.
 */
public class Ed25519KeyAnalyzer implements KeyAnalyzer {

    private static final int COMPRESSED_KEY_SIZE = 32;

    @Override
    public boolean isKeyCompressed(final PublicKey publicKey) {
        return COMPRESSED_KEY_SIZE == publicKey.getBytes().length;
    }
}
