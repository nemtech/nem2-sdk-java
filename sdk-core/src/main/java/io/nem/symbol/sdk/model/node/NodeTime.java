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
package io.nem.symbol.sdk.model.node;

import java.math.BigInteger;

public class NodeTime {

    private final BigInteger sendTimestamp;
    private final BigInteger receiveTimestamp;

    /**
     * Constructor
     *
     * @param sendTimestamp the send timestamp.
     * @param receiveTimestamp the receive timestamp.
     */
    public NodeTime(BigInteger sendTimestamp, BigInteger receiveTimestamp) {
        this.receiveTimestamp = receiveTimestamp;
        this.sendTimestamp = sendTimestamp;
    }

    /**
     * Get send timestamp
     *
     * @return BigInteger
     */
    public BigInteger getSendTimestamp() {
        return this.sendTimestamp;
    }

    /**
     * Get receive timestamp
     *
     * @return BigInteger
     */
    public BigInteger getReceiveTimestamp() {
        return this.receiveTimestamp;
    }
}
