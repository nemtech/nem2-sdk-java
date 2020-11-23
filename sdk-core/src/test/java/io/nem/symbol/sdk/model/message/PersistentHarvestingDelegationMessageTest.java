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
import io.nem.symbol.sdk.model.message.PersistentHarvestingDelegationMessage.HarvestingKeys;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test class for the EncryptMessage. */
public class PersistentHarvestingDelegationMessageTest {

  @Test
  public void testCreateEncryptedMessage() {
    KeyPair signing = KeyPair.random();
    KeyPair vrf = KeyPair.random();
    KeyPair remote = KeyPair.random();

    PersistentHarvestingDelegationMessage message =
        PersistentHarvestingDelegationMessage.create(
            signing.getPrivateKey(), vrf.getPrivateKey(), remote.getPublicKey());

    Assertions.assertTrue(
        message.getPayloadHex().startsWith(MessageMarker.PERSISTENT_DELEGATION_UNLOCK));

    Assertions.assertEquals(message.getText(), message.getPayloadHex());

    Assertions.assertEquals(
        PersistentHarvestingDelegationMessage.HEX_PAYLOAD_SIZE, message.getPayloadHex().length());

    System.out.println(message.getPayloadHex().length());

    Assertions.assertEquals(
        MessageType.PERSISTENT_HARVESTING_DELEGATION_MESSAGE, message.getType());

    HarvestingKeys plainMessage = message.decryptPayload(remote.getPrivateKey());

    Assertions.assertEquals(signing.getPrivateKey(), plainMessage.getSigningPrivateKey());
    Assertions.assertEquals(vrf.getPrivateKey(), plainMessage.getVrfPrivateKey());

    Optional<Message> message2 = Message.createFromHexPayload(message.getPayloadHex());
    Assertions.assertEquals(message, message2.get());
    Assertions.assertTrue(message2.get() instanceof PersistentHarvestingDelegationMessage);

    Optional<Message> encryptedMessage3 =
        Message.createFromPayload(message.getPayloadByteBuffer().array());
    Assertions.assertEquals(message, encryptedMessage3.get());

    Assertions.assertTrue(encryptedMessage3.get() instanceof PersistentHarvestingDelegationMessage);

    System.out.println(message.getPayloadHex());
    Assertions.assertTrue(
        message.getPayloadHex().startsWith(MessageMarker.PERSISTENT_DELEGATION_UNLOCK));
  }

  @Test
  public void createFromPayload() {
    String payload =
        "FE2A8061577301E231539A87767B731A725E8F87926FDA9968701C082D2AC6CD16C6572F4F3047184D6C4A0443CC5D2565838040CC31B7EA0BA4588728110668BE960A28CAFCDC1703C234903937CCD0CDD6F11DBE7AE4C288FE2E2245BD4BE08C1F864E7FB42C4648E19CA53622AA0C2EAEDB47B8A06B157BD47FD6C230193FCC50F1F9";

    PersistentHarvestingDelegationMessage message =
        (PersistentHarvestingDelegationMessage)
            PersistentHarvestingDelegationMessage.createFromHexPayload(payload).get();
    Assertions.assertEquals(payload, message.getPayloadHex());

    Assertions.assertTrue(
        message.getPayloadHex().startsWith(MessageMarker.PERSISTENT_DELEGATION_UNLOCK));

    Assertions.assertEquals(message, Message.createFromHexPayload(payload).get());

    PersistentHarvestingDelegationMessage message2 =
        (PersistentHarvestingDelegationMessage) Message.createFromHexPayload(payload).get();
    Assertions.assertEquals(payload, message2.getPayloadHex());
  }
}
