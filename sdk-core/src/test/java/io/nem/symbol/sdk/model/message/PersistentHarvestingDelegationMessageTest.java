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

package io.nem.symbol.sdk.model.message;

import io.nem.symbol.core.crypto.KeyPair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for the EncryptMessage.
 */
public class PersistentHarvestingDelegationMessageTest {

    @Test
    public void testCreateEncryptedMessage() {
        KeyPair proxy = KeyPair.random();
        KeyPair harvester = KeyPair.random();

        PersistentHarvestingDelegationMessage encryptedMessage = PersistentHarvestingDelegationMessage
            .create(proxy.getPrivateKey(), harvester.getPublicKey());

        Assertions.assertEquals(MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE,
            encryptedMessage.getType());

        String plainMessage = encryptedMessage
            .decryptPayload(harvester.getPrivateKey());

        Assertions.assertEquals(proxy.getPrivateKey().toHex().toUpperCase(), plainMessage);
    }


}
