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
import org.apache.commons.lang3.Validate;

public class PersistentHarvestingDelegationMessage extends Message {

  /** When decrypting, the message is converted back to the 2 original private keys. */
  public static class HarvestingKeys {

    private final PrivateKey signingPrivateKey;
    private final PrivateKey vrfPrivateKey;

    private HarvestingKeys(PrivateKey signingPrivateKey, PrivateKey vrfPrivateKey) {
      Validate.notNull(signingPrivateKey, "signingPrivateKey is required");
      Validate.notNull(vrfPrivateKey, "vrfPrivateKey is required");
      this.signingPrivateKey = signingPrivateKey;
      this.vrfPrivateKey = vrfPrivateKey;
    }

    public PrivateKey getSigningPrivateKey() {
      return signingPrivateKey;
    }

    public PrivateKey getVrfPrivateKey() {
      return vrfPrivateKey;
    }
  }

  public PersistentHarvestingDelegationMessage(String payload) {
    super(MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE, payload);
  }

  /**
   * Helper constructor that allow users to create an encrypted Persistent Harvesting Delegation
   * Message
   *
   * <p>Note, the strategy to encrypt and decrypt should be shared between the different SDKs. A
   * client may send a transaction using a sdk and the recipient may be using a different one.
   *
   * <p>The strategy is:
   *
   * <p>"plain text" string - utf8 byte array - encrypted byte array - hex string (the encrypted
   * message string)
   *
   * @param signingPrivateKey Remote harvester signing private key linked to the main account
   * @param vrfPrivateKey VRF private key linked to the main account
   * @param nodePublicKey Recipient public key
   * @return {@link PersistentHarvestingDelegationMessage}
   */
  public static PersistentHarvestingDelegationMessage create(
      PrivateKey signingPrivateKey, PrivateKey vrfPrivateKey, PublicKey nodePublicKey) {

    KeyPair ephemeralKeyPair = KeyPair.random();

    CryptoEngine engine = CryptoEngines.defaultEngine();

    KeyPair recipient = KeyPair.onlyPublic(nodePublicKey, engine);
    BlockCipher blockCipher = engine.createBlockCipher(ephemeralKeyPair, recipient);

    String payload =
        MessageMarker.PERSISTENT_DELEGATION_UNLOCK
            + ephemeralKeyPair.getPublicKey().toHex()
            + ConvertUtils.toHex(
                blockCipher.encrypt(
                    StringEncoder.getBytes(signingPrivateKey.toHex() + vrfPrivateKey.toHex())));

    return new PersistentHarvestingDelegationMessage(payload.toUpperCase());
  }

  /**
   * Utility method that allow users to decrypt a message if it was created using the Java SDK or
   * the Typescript SDK.
   *
   * @param recipientPrivateKey Recipient private key
   * @return the 2 private keys
   */
  public HarvestingKeys decryptPayload(PrivateKey recipientPrivateKey) {

    int markerLength = MessageMarker.PERSISTENT_DELEGATION_UNLOCK.length();
    int publicKeyHexSize = PublicKey.SIZE * 2;
    PublicKey senderPublicKey =
        PublicKey.fromHexString(getText().substring(markerLength, markerLength + publicKeyHexSize));

    String encryptedPayload = getText().substring(markerLength + publicKeyHexSize);

    CryptoEngine engine = CryptoEngines.defaultEngine();
    KeyPair sender = KeyPair.onlyPublic(senderPublicKey, engine);
    KeyPair recipient = KeyPair.fromPrivate(recipientPrivateKey);
    BlockCipher blockCipher = engine.createBlockCipher(sender, recipient);

    String doubleKey =
        StringEncoder.getString(blockCipher.decrypt(ConvertUtils.fromHexToBytes(encryptedPayload)))
            .toUpperCase();
    PrivateKey signingPrivateKey =
        PrivateKey.fromHexString(doubleKey.substring(0, publicKeyHexSize));
    PrivateKey vrfPrivateKey = PrivateKey.fromHexString(doubleKey.substring(publicKeyHexSize));
    return new HarvestingKeys(signingPrivateKey, vrfPrivateKey);
  }
}
