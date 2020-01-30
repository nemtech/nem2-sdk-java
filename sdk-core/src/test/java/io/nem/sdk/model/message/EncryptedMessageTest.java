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

package io.nem.sdk.model.message;

import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PrivateKey;
import io.nem.sdk.model.blockchain.NetworkType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for the EncryptMessage.
 */
public class EncryptedMessageTest {

    @Test
    public void testCreateEncryptedMessage() {
        NetworkType networkType = NetworkType.MIJIN_TEST;
        String message = "This is a plain message 漢字";
        KeyPair sender = KeyPair.random(networkType.resolveSignSchema());
        KeyPair recipient = KeyPair.random(networkType.resolveSignSchema());

        EncryptedMessage encryptedMessage = EncryptedMessage
            .create(message, sender.getPrivateKey(), recipient.getPublicKey(), networkType);

        Assertions.assertEquals(MessageType.ENCRYPTED_MESSAGE, encryptedMessage.getType());

        String plainMessage = encryptedMessage
            .decryptPayload(sender.getPublicKey(), recipient.getPrivateKey(), networkType);

        Assertions.assertEquals(message, plainMessage);
    }


    @Test
    public void testTypeScriptCompatibility() {

        // This unit test recreates a message encrypted in one of the typescript unit tests.
        // Encryption should be the same between the 2 sdk. If one sdk is encrypting a message,
        // the other sdk should be able to decrypted if it knows the right keys.
        // Although using the same encryption algorithm, outcome may be different if the encoding
        // process is different. Both TS and Java are using utf-8 and hex encodings,

        NetworkType networkType = NetworkType.MIJIN_TEST;

        KeyPair sender = KeyPair.fromPrivate(PrivateKey
                .fromHexString("2602F4236B199B3DF762B2AAB46FC3B77D8DDB214F0B62538D3827576C46C108"),
            networkType.resolveSignSchema());
        KeyPair recipient = KeyPair.fromPrivate(PrivateKey
                .fromHexString("B72F2950498111BADF276D6D9D5E345F04E0D5C9B8342DA983C3395B4CF18F08"),
            networkType.resolveSignSchema());

        String typescriptEncryptedKey = "3A1B688733D1974BBA96BEEF4FB03E7A5379C343B057701B77E9E063C887A6936BF63900524E4E05F006FFF0D078D1DE";

        EncryptedMessage encryptedMessage = new EncryptedMessage(typescriptEncryptedKey);
        String plainMessage = encryptedMessage
            .decryptPayload(sender.getPublicKey(), recipient.getPrivateKey(), networkType);

        Assertions.assertEquals("test transaction 漢字", plainMessage);


    }

}
