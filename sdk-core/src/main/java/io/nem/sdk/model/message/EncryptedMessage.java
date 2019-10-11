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

import io.nem.core.crypto.BlockCipher;
import io.nem.core.crypto.CryptoEngine;
import io.nem.core.crypto.CryptoEngines;
import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PrivateKey;
import io.nem.core.crypto.PublicKey;
import io.nem.core.crypto.SignSchema;
import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.StringEncoder;
import io.nem.sdk.model.blockchain.NetworkType;


/**
 * A message that has been encrypted using the NEM's SDK libraries.
 */
public class EncryptedMessage extends Message {

    /**
     * It creates a Encrypted Message from the encryptedPayload.
     */
    public EncryptedMessage(String encryptedPayload) {
        super(MessageType.ENCRYPTED_MESSAGE, encryptedPayload);
    }

    /**
     * Helper constructor that allow users to easily encrypt a message using the SDK provided {@link
     * CryptoEngine} and {@link BlockCipher}.
     *
     * Note, the strategy to encrypt and decrypt should be shared between the different SDKs. A
     * client may send a transaction using a sdk and the recipient may be using a different one.
     *
     * The strategy is:
     *
     * "plain text" string - utf8 byte array - encrypted byte array - hex string (the encrypted
     * message string)
     *
     * @param plainTextMessage Plain message to be encrypted
     * @param senderPrivateKey Sender private key
     * @param recipientPublicKey Recipient public key
     * @param networkType Catapult network type
     * @return EncryptedMessage
     */
    public static EncryptedMessage create(String plainTextMessage,
        PrivateKey senderPrivateKey,
        PublicKey recipientPublicKey,
        NetworkType networkType) {
        SignSchema signSchema = networkType.resolveSignSchema();
        CryptoEngine engine = CryptoEngines.defaultEngine();
        KeyPair sender = KeyPair.fromPrivate(senderPrivateKey, signSchema);
        KeyPair recipient = KeyPair.onlyPublic(recipientPublicKey, engine);
        BlockCipher blockCipher = engine
            .createBlockCipher(sender,
                recipient, signSchema);
        return new EncryptedMessage(
            ConvertUtils.toHex(blockCipher.encrypt(StringEncoder.getBytes(plainTextMessage)))
        );
    }


    /**
     * Utility method that allow users to decrypt a message if it was created using the Java SDK or
     * the Typescript SDK.
     *
     * @param senderPublicKey Sender public key.
     * @param recipientPrivateKey Recipient private key
     * @param networkType Catapult network type
     * @return plain string message.
     */
    public String decryptPayload(PublicKey senderPublicKey, PrivateKey recipientPrivateKey,
        NetworkType networkType) {
        SignSchema signSchema = networkType.resolveSignSchema();
        CryptoEngine engine = CryptoEngines.defaultEngine();
        KeyPair sender = KeyPair.onlyPublic(senderPublicKey, engine);
        KeyPair recipient = KeyPair.fromPrivate(recipientPrivateKey, signSchema);
        BlockCipher blockCipher = engine
            .createBlockCipher(sender, recipient, signSchema);
        return StringEncoder.getString(blockCipher.decrypt(ConvertUtils.fromHexToBytes(getPayload())));
    }

}
