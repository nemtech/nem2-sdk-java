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

import io.nem.symbol.core.crypto.BlockCipher;
import io.nem.symbol.core.crypto.CryptoEngine;
import io.nem.symbol.core.crypto.CryptoEngines;
import io.nem.symbol.core.crypto.KeyPair;
import io.nem.symbol.core.crypto.PrivateKey;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.StringEncoder;

public class PersistentHarvestingDelegationMessage extends Message {

    public PersistentHarvestingDelegationMessage(String payload) {
        super(MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE, payload);
    }

    /**
     * Helper constructor that allow users to create an encrypted Persistent Harvesting Delegation
     * Message
     *
     * Note, the strategy to encrypt and decrypt should be shared between the different SDKs. A
     * client may send a transaction using a sdk and the recipient may be using a different one.
     *
     * The strategy is:
     *
     * "plain text" string - utf8 byte array - encrypted byte array - hex string (the encrypted
     * message string)
     *
     * @param delegatedPrivateKey the remote’s account proxy private key.
     * @param recipientPublicKey Recipient public key
     * @return {@link PersistentHarvestingDelegationMessage}
     */
    public static PersistentHarvestingDelegationMessage create(PrivateKey delegatedPrivateKey,
        PublicKey recipientPublicKey) {

        KeyPair ephemeralKeypair = KeyPair.random();

        CryptoEngine engine = CryptoEngines.defaultEngine();

        KeyPair recipient = KeyPair.onlyPublic(recipientPublicKey, engine);
        BlockCipher blockCipher = engine.createBlockCipher(ephemeralKeypair, recipient);

        String payload =
            MessageMarker.PERSISTENT_DELEGATION_UNLOCK + ephemeralKeypair.getPublicKey().toHex()
                + ConvertUtils
                .toHex(blockCipher.encrypt(StringEncoder.getBytes(delegatedPrivateKey.toHex())));

        return new PersistentHarvestingDelegationMessage(payload.toUpperCase());
    }


    /**
     * Utility method that allow users to decrypt a message if it was created using the Java SDK or
     * the Typescript SDK.
     *
     * @param recipientPrivateKey Recipient private key
     * @return the recipient public key.
     */
    public String decryptPayload(PrivateKey recipientPrivateKey) {

        int markerLength = MessageMarker.PERSISTENT_DELEGATION_UNLOCK.length();
        PublicKey senderPublicKey = PublicKey
            .fromHexString(getPayload().substring(markerLength, markerLength + 64));

        String encryptedPayload = getPayload()
            .substring(markerLength + senderPublicKey.toHex().length());

        CryptoEngine engine = CryptoEngines.defaultEngine();
        KeyPair sender = KeyPair.onlyPublic(senderPublicKey, engine);
        KeyPair recipient = KeyPair.fromPrivate(recipientPrivateKey);
        BlockCipher blockCipher = engine
            .createBlockCipher(sender, recipient);

        return StringEncoder
            .getString(blockCipher.decrypt(ConvertUtils.fromHexToBytes(encryptedPayload)))
            .toUpperCase();
    }

}
