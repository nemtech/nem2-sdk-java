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

import io.nem.core.utils.ConvertUtils;

/**
 * An abstract message class that serves as the base class of all message types.
 */
public abstract class Message {

    private final MessageType type;
    private final String payload;

    public Message(MessageType type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    /**
     * This factory method knows how to create the right Message instance from the provided message
     * type and payload.
     *
     * @param messageType the message type you want to create.
     * @param payload the raw payload as it comes from REST data.
     * @return the Message.
     */
    public static Message createFromPayload(MessageType messageType, String payload) {
        String decoded = payload == null || payload.isEmpty() ? ""
            : ConvertUtils.fromHexToString(payload);
        switch (messageType) {
            case PLAIN_MESSAGE:
                return new PlainMessage(decoded);
            case ENCRYPTED_MESSAGE:
                return new EncryptedMessage(decoded);
            case PERSISTENT_HARVESTING_DELEGATION_MESSAGE:
                return new PersistentHarvestingDelegationMessage(decoded);
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
    public String getPayload() {
        return payload;
    }
}
