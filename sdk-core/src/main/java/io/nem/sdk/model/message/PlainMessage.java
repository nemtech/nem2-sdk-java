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
 * The plain message model defines a plain string. When sending it to the network we transform the
 * payload to hex-string.
 *
 * @since 1.0
 */
public class PlainMessage extends Message {

    /**
     * Plain message containing an empty string.
     */
    public static final PlainMessage Empty = new PlainMessage("");

    /**
     * Constructor
     *
     * @param payload plain message payload
     */
    public PlainMessage(String payload) {
        super(MessageType.PLAIN_MESSAGE, payload);
    }

    /**
     * Create a plain message object.
     *
     * @param payload plain message payload
     * @return instance of PlainMessage
     */
    public static PlainMessage create(String payload) {
        return new PlainMessage(payload);
    }

    /**
     * Create a plain message object from binary payload.
     *
     * @param payload plain message payload
     * @return instance of PlainMessage
     */
    public static PlainMessage createFromPayload(String payload) {
        return new PlainMessage(payload);
    }
}
