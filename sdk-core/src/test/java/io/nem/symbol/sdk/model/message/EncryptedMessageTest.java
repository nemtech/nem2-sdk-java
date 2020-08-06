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

import io.nem.symbol.core.crypto.CryptoException;
import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.core.crypto.PrivateKey;
import io.nem.symbol.core.utils.ConvertUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for the EncryptMessage.
 */
public class EncryptedMessageTest {

    @Test
    public void testCreateEncryptedMessage() {

        String message = "This is a plain message 漢字";
        KeyPair sender = KeyPair.random();
        KeyPair recipient = KeyPair.random();

        EncryptedMessage encryptedMessage = EncryptedMessage
            .create(message, sender.getPrivateKey(), recipient.getPublicKey());

        Assertions.assertEquals(MessageType.ENCRYPTED_MESSAGE, encryptedMessage.getType());

        String plainMessage = encryptedMessage.decryptPayload(sender.getPublicKey(), recipient.getPrivateKey());

        Assertions.assertEquals(message, plainMessage);
    }

    @Test
    public void testDecryptWrong() {

        String wrongEncrypted = "ABCD";
        KeyPair sender = KeyPair.random();
        KeyPair recipient = KeyPair.random();
        EncryptedMessage encryptedMessage = new EncryptedMessage(wrongEncrypted);
        Assertions.assertEquals(MessageType.ENCRYPTED_MESSAGE, encryptedMessage.getType());
        CryptoException e = Assertions.assertThrows(CryptoException.class,
            () -> encryptedMessage.decryptPayload(sender.getPublicKey(), recipient.getPrivateKey()));
        Assertions.assertEquals("Cannot decrypt input. Size is 2 when at least 28 is expected.", e.getMessage());

    }


    @Test
    public void testTypeScriptCompatibility() {

        // This unit test recreates a message encrypted in one of the typescript unit tests.
        // Encryption should be the same between the 2 sdk. If one sdk is encrypting a message,
        // the other sdk should be able to decrypted if it knows the right keys.
        // Although using the same encryption algorithm, outcome may be different if the encoding
        // process is different. Both TS and Java are using utf-8 and hex encodings,

        KeyPair sender = KeyPair
            .fromPrivate(PrivateKey.fromHexString("2602F4236B199B3DF762B2AAB46FC3B77D8DDB214F0B62538D3827576C46C108"));
        KeyPair recipient = KeyPair
            .fromPrivate(PrivateKey.fromHexString("B72F2950498111BADF276D6D9D5E345F04E0D5C9B8342DA983C3395B4CF18F08"));

        String typescriptEncryptedKey = "079A490A7F68CC42F7156D12F082AF1ADC193FD8E3DA93CF67FA1D3F880D5DCEF2A9734EFE39646501023D1A9B63A44E57AEDE";

        EncryptedMessage encryptedMessage = new EncryptedMessage(typescriptEncryptedKey);
        String plainMessage = encryptedMessage.decryptPayload(sender.getPublicKey(), recipient.getPrivateKey());

        Assertions.assertEquals("test transaction 漢字", plainMessage);


    }

    @Test
    public void testTypeScriptCompatibilityFromPayload() {

        // This unit test recreates a message encrypted in one of the typescript unit tests.
        // Encryption should be the same between the 2 sdk. If one sdk is encrypting a message,
        // the other sdk should be able to decrypted if it knows the right keys.
        // Although using the same encryption algorithm, outcome may be different if the encoding
        // process is different. Both TS and Java are using utf-8 and hex encodings,

        KeyPair sender = KeyPair
            .fromPrivate(PrivateKey.fromHexString("2602F4236B199B3DF762B2AAB46FC3B77D8DDB214F0B62538D3827576C46C108"));
        KeyPair recipient = KeyPair
            .fromPrivate(PrivateKey.fromHexString("B72F2950498111BADF276D6D9D5E345F04E0D5C9B8342DA983C3395B4CF18F08"));

        String typescriptEncryptedKey = "079A490A7F68CC42F7156D12F082AF1ADC193FD8E3DA93CF67FA1D3F880D5DCEF2A9734EFE39646501023D1A9B63A44E57AEDE";

        EncryptedMessage encryptedMessage = (EncryptedMessage) Message
            .createFromPayload(MessageType.ENCRYPTED_MESSAGE, ConvertUtils.fromStringToHex(typescriptEncryptedKey));
        String plainMessage = encryptedMessage.decryptPayload(sender.getPublicKey(), recipient.getPrivateKey());

        Assertions.assertEquals("test transaction 漢字", plainMessage);


    }

}
