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
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/** An abstract message class that serves as the base class of all message types. */
public abstract class Message {

  /** The low level message. */
  private final byte[] payload;

  /** The message type */
  private final MessageType type;

  public Message(byte[] payload, MessageType type) {
    Validate.notNull(payload, "payload is required");
    Validate.notNull(type, "type is required");
    this.payload = payload;
    this.type = type;
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
    return createFromHexPayload(ConvertUtils.toHex(payload));
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

    String upperCasePayload = payloadHex.toUpperCase();
    if (upperCasePayload.length() == PersistentHarvestingDelegationMessage.HEX_PAYLOAD_SIZE
        && upperCasePayload.startsWith(MessageMarker.PERSISTENT_DELEGATION_UNLOCK)) {
      return Optional.of(new PersistentHarvestingDelegationMessage(upperCasePayload));
    }

    byte[] payload = ConvertUtils.fromHexToBytes(payloadHex);
    MessageType messageType =
        MessageType.rawValueOf(SerializationUtils.byteToUnsignedInt(payload[0]));

    String messageHex = payloadHex.substring(2);
    String text = ConvertUtils.fromHexToString(messageHex);
    switch (messageType) {
      case PLAIN_MESSAGE:
        return Optional.of(new PlainMessage(text));
      case ENCRYPTED_MESSAGE:
        return Optional.of(new EncryptedMessage(text));
      default:
        return Optional.of(new RawMessage(payload));
    }
  }

  /** @return the raw byte payload. */
  public byte[] getPayload() {
    return payload;
  }

  /** @return the type of this message. */
  public MessageType getType() {
    return type;
  }

  /**
   * Returns payload as text. Sub
   *
   * @return String
   */
  public abstract String getText();

  /** @return the full payload including the message type as byte buffer */
  public ByteBuffer getPayloadByteBuffer() {
    return ByteBuffer.wrap(getPayload());
  }

  /** @return the full payload including the message type as string. */
  public String getPayloadHex() {
    return ConvertUtils.toHex(getPayload());
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
    return Arrays.equals(payload, message.payload) && type == message.type;
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(type);
    result = 31 * result + Arrays.hashCode(payload);
    return result;
  }
}
