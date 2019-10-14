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

package io.nem.sdk.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An adapter that converts a {@link ByteBuffer} into a {@link InputStream}.
 */
public class ByteBufferBackedInputStream extends InputStream {

    /**
     * Wrapped {@link ByteBuffer}.
     */
    private final ByteBuffer buf;

    /**
     * Contructor
     *
     * @param buf the {@link ByteBuffer} to be addapted.
     */
    public ByteBufferBackedInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    /**
     * @return the next byte of data from the ByteBuffer. -1 if ByteBuffer has reached the end.
     */
    public int read() {
        if (!buf.hasRemaining()) {
            return -1;
        }
        return buf.get() & 0xFF;
    }

    /**
     * It copies the bytes from {@link ByteBuffer} to the byte array according to the offset and
     * length.
     *
     * @param bytes the buffer into which the data is read.
     * @param off the start offset in array <code>b</code> at which the data is written.
     * @param len the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or <code>-1</code> if there is no
     * more data because the end of the stream has been reached.
     */
    public int read(byte[] bytes, int off, int len) {
        if (!buf.hasRemaining()) {
            return -1;
        }
        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
    }
}
