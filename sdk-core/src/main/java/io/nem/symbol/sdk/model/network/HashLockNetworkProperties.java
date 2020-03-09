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




package io.nem.symbol.sdk.model.network;

/**
 * HashLockNetworkProperties
 */
public class HashLockNetworkProperties {

    /**
     * Amount that has to be locked per aggregate in partial cache.
     */
    private final String lockedFundsPerAggregate;

    /**
     * Maximum number of blocks for which a hash lock can exist.
     */
    private final String maxHashLockDuration;


    public HashLockNetworkProperties(String lockedFundsPerAggregate,
        String maxHashLockDuration) {
        this.lockedFundsPerAggregate = lockedFundsPerAggregate;
        this.maxHashLockDuration = maxHashLockDuration;
    }

    public String getLockedFundsPerAggregate() {
        return lockedFundsPerAggregate;
    }

    public String getMaxHashLockDuration() {
        return maxHashLockDuration;
    }
}

