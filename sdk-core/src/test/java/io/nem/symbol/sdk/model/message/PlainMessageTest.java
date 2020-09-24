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
import org.junit.jupiter.api.Test;

class PlainMessageTest {

  @Test
  void shouldCreatePlainMessageViaConstructor() {
    PlainMessage plainMessage = new PlainMessage("test-message");
    assertEquals("test-message", plainMessage.getText());
    assertSame(MessageType.PLAIN_MESSAGE, plainMessage.getType());

    PlainMessage anotherPlain =
        (PlainMessage) Message.createFromHexPayload(plainMessage.getPayloadHex()).get();
    assertEquals(anotherPlain, plainMessage);
  }

  @Test
  void shouldCreatePlainMessageViaStaticConstructor() {
    PlainMessage plainMessage = PlainMessage.create("test-message");
    assertEquals("test-message", plainMessage.getText());
    assertSame(MessageType.PLAIN_MESSAGE, plainMessage.getType());
    byte[] payload = plainMessage.getPayloadByteBuffer().array();
    assertEquals("00746573742D6D657373616765", ConvertUtils.toHex(payload));
    PlainMessage anotherPlain = (PlainMessage) Message.createFromPayload(payload).get();
    assertEquals(anotherPlain, plainMessage);
  }

  @Test
  void shouldCreateWhenEmptyMessage() {
    PlainMessage plainMessage = PlainMessage.create("");
    assertEquals("", plainMessage.getText());
    assertSame(MessageType.PLAIN_MESSAGE, plainMessage.getType());
    byte[] payload = plainMessage.getPayloadByteBuffer().array();
    assertEquals("00", ConvertUtils.toHex(payload));
    PlainMessage anotherPlain = (PlainMessage) Message.createFromPayload(payload).get();
    assertEquals(anotherPlain, plainMessage);
  }
}
