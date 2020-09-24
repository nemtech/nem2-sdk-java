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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/** An abstract message class that serves as the base class of all message types. */
public abstract class Message {

  private final MessageType type;
  private final String text;

  public Message(MessageType type, String text) {
    this.type = type;
    this.text = text;
  }

  /**
   * This factory method knows how to create the right Message instance from the provided message
   * payload.
   *
   * @param payloadHex the raw payload as it comes from REST data.
   * @return the Message.
   */
  public static Optional<Message> createFromHexPayload(String payloadHex) {
    if (payloadHex == null || payloadHex.isEmpty()) {
      return Optional.empty();
    }
    return createFromPayload(ConvertUtils.fromHexToBytes(payloadHex));
  }

  /**
   * This factory method knows how to create the right Message instance from the provided message
   * payload.
   *
   * @param payload the raw payload as it comes from binary data.
   * @return the Message.
   */
  public static Optional<Message> createFromPayload(byte[] payload) {
    if (payload == null || payload.length == 0) {
      return Optional.empty();
    }
    MessageType messageType =
        MessageType.rawValueOf(SerializationUtils.byteToUnsignedInt(payload[0]));
    String messageHex = ConvertUtils.toHex(payload).substring(2);
    String text = ConvertUtils.fromHexToString(messageHex);
    switch (messageType) {
      case PLAIN_MESSAGE:
        return Optional.of(new PlainMessage(text));
      case ENCRYPTED_MESSAGE:
        return Optional.of(new EncryptedMessage(text));
      case PERSISTENT_HARVESTING_DELEGATION_MESSAGE:
        return Optional.of(new PersistentHarvestingDelegationMessage(text));
      default:
        throw new IllegalStateException("Unknown Message Type " + messageType);
    }
  }

  /**
   * Returns message type.
   *
   * @return int
   */
  public MessageType getType() {
    return type;
  }

  /**
   * Returns message payload.
   *
   * @return String
   */
  public String getText() {
    return text;
  }

  /** @return the full payload including the message type as byte buffer */
  public ByteBuffer getPayloadByteBuffer() {
    MessageType type = this.getType();
    final byte byteMessageType = (byte) type.getValue();
    final byte[] bytePayload = this.getText().getBytes(StandardCharsets.UTF_8);
    final ByteBuffer messageBuffer =
        ByteBuffer.allocate(bytePayload.length + 1 /* for the message type */);
    messageBuffer.put(byteMessageType);
    messageBuffer.put(bytePayload);
    return messageBuffer;
  }

  /** @return the full payload including the message type as string. */
  public String getPayloadHex() {
    return ConvertUtils.toHex(getPayloadByteBuffer().array());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Message message = (Message) o;
    return type == message.type && Objects.equals(text, message.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, text);
  }
}
