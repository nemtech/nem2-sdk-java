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
     * Returns message type.
     *
     * @return Message type
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
