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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.Validate;

/** An SDK defined message where the type is prepended to the payload. */
public abstract class TypedMessage extends Message {

  private final String text;

  /**
   * Creates a type message from a type and a text
   *
   * @param type the message type
   * @param text the text.
   */
  public TypedMessage(MessageType type, String text) {
    super(getPayloadByteBuffer(type, text).array(), type);
    Validate.notNull(text, "text is required");
    this.text = text;
  }

  /**
   * It creates the final payload
   *
   * @param type the message type
   * @param text the plain/encrypted text.
   * @return the full payload including the message type as byte buffer
   */
  public static ByteBuffer getPayloadByteBuffer(MessageType type, String text) {
    final byte byteMessageType = (byte) type.getValue();
    final byte[] bytePayload = text.getBytes(StandardCharsets.UTF_8);
    final ByteBuffer messageBuffer =
        ByteBuffer.allocate(bytePayload.length + 1 /* for the message type */);
    messageBuffer.put(byteMessageType);
    messageBuffer.put(bytePayload);
    return messageBuffer;
  }

  /**
   * Returns message payload.
   *
   * @return String
   */
  @Override
  public String getText() {
    return text;
  }
}
