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

import io.nem.symbol.core.crypto.BlockCipher;
import io.nem.symbol.core.crypto.BlockCipherTest;
import io.nem.symbol.core.crypto.CryptoEngine;
import io.nem.symbol.core.crypto.CryptoEngines;
import io.nem.symbol.core.crypto.CryptoException;
import io.nem.symbol.core.crypto.KeyPair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Ed25519BlockCipherTest extends BlockCipherTest {


    @Test
    public void decryptFailsInputIsTooSmallInLength() {
        // Arrange:
        final CryptoEngine engine = this.getCryptoEngine();
        final KeyPair kp = KeyPair.random(engine);
        final BlockCipher blockCipher = this.getBlockCipher(kp, kp);

        // Act:
        CryptoException exception = Assertions
            .assertThrows(CryptoException.class, () -> blockCipher.decrypt(new byte[27]));

        // Assert:
        Assertions
            .assertEquals("Cannot decrypt input. Size is 27 when at least 28 is expected.", exception.getMessage());
    }

    @Test
    public void decryptFailsIfInputIsNull() {
        // Arrange:
        final CryptoEngine engine = this.getCryptoEngine();
        final KeyPair kp = KeyPair.random(engine);
        final BlockCipher blockCipher = this.getBlockCipher(kp, kp);

        // Act:
        CryptoException exception = Assertions.assertThrows(CryptoException.class, () -> blockCipher.decrypt(null));

        // Assert:
        Assertions.assertEquals("Cannot decrypt. Input is required.", exception.getMessage());
    }

    @Override
    protected BlockCipher getBlockCipher(final KeyPair senderKeyPair, final KeyPair recipientKeyPair) {
        return new Ed25519BlockCipher(senderKeyPair, recipientKeyPair);
    }

    @Override
    protected CryptoEngine getCryptoEngine() {
        return CryptoEngines.ed25519Engine();
    }
}
