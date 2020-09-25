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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.nem.symbol.core.utils.ConvertUtils;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RawMessageTest {

  @Test
  void createRawMessage() {
    RawMessage rawMessage = new RawMessage("test-message".getBytes(StandardCharsets.UTF_8));
    assertEquals("test-message", rawMessage.getText());
    assertSame(MessageType.RAW_MESSAGE, rawMessage.getType());
    byte[] payload = rawMessage.getPayloadByteBuffer().array();
    assertEquals("746573742D6D657373616765", ConvertUtils.toHex(payload));
    RawMessage anotherPlain =
        (RawMessage) Message.createFromHexPayload(rawMessage.getPayloadHex()).get();
    assertEquals(anotherPlain, rawMessage);
  }

  @Test
  void shouldCreateWhenEmptyMessage() {
    RawMessage rawMessage = new RawMessage("".getBytes(StandardCharsets.UTF_8));
    assertEquals("", rawMessage.getText());
    assertSame(MessageType.RAW_MESSAGE, rawMessage.getType());
    byte[] payload = rawMessage.getPayloadByteBuffer().array();
    assertEquals("", ConvertUtils.toHex(payload));
    Assertions.assertFalse(Message.createFromPayload(payload).isPresent());
  }
}
