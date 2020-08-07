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

package io.nem.symbol.sdk.infrastructure;

/**
 * The listener payload with the parsed data.
 */
public class ListenerMessage {

    private final ListenerChannel channel;
    private final String channelParams;
    private final Object message;

    public ListenerMessage(ListenerChannel channel, String channelParams, Object message) {
        this.channel = channel;
        this.channelParams = channelParams;
        this.message = message;
    }

    public ListenerChannel getChannel() {
        return channel;
    }

    public String getChannelParams() {
        return channelParams;
    }

    public Object getMessage() {
        return message;
    }
}
