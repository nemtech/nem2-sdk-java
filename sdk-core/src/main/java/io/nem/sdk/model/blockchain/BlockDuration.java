/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.model.blockchain;

import java.math.BigInteger;
import java.util.Objects;

public class BlockDuration {

    /**
     * The duration in blocks a mosaic will be available. After the duration finishes mosaic is
     * inactive and can be renewed. Duration is required when defining the mosaic
     */
    private final long duration;

    /**
     * Constructor.
     *
     * @param duration long
     */
    public BlockDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Constructor.
     *
     * @param duration BigInteger
     */
    public BlockDuration(BigInteger duration) {
        this.duration = duration.longValue();
    }

    /**
     * Returns the number of blocks from height it will be active
     *
     * @return long duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Returns the duration as string.
     *
     * @return String duration
     */
    public String toString() {
        return Long.toString(duration);
    }

    /**
     * It compares this {@link BlockDuration} to another object. Returns true if the object is a
     * {@link BlockDuration} with the same duration number.
     *
     * @param o the object to be compared.
     * @return true is o is an {@link BlockDuration} with the same duration. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockDuration that = (BlockDuration) o;
        return duration == that.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration);
    }
}
