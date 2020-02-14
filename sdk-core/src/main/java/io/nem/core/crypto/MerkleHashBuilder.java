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

package io.nem.core.crypto;

import java.util.ArrayList;
import java.util.List;

/**
 * Merkle hash builder.
 */
public class MerkleHashBuilder {

    private final List<byte[]> hashes;

    /**
     * Constructor.
     */
    public MerkleHashBuilder() {
        this.hashes = new ArrayList<>();
    }

    private byte[] getRootHash(List<byte[]> hashes) {
        if (hashes.isEmpty()) {
            return new byte[32];
        }

        // build the merkle tree
        int numRemainingHashes = hashes.size();

        Hasher hasher = Hashes::sha3_256;
        while (numRemainingHashes > 1) {

            for (int i = 0; i < numRemainingHashes; i += 2) {
                if (i + 1 < numRemainingHashes) {
                    hashes.add(i / 2, hasher.hash(hashes.get(i), hashes.get(i + 1)));
                    continue;
                }

                // if there is an odd number of hashes, duplicate the last one
                hashes.add(i / 2, hasher.hash(hashes.get(i), hashes.get(i)));
                ++numRemainingHashes;
            }

            numRemainingHashes /= 2;
        }

        return hashes.get(0);
    }

    /**
     * Get the merkle tree root hash.
     *
     * @return Root hash.
     */
    public byte[] getRootHash() {
        // build the merkle root
        return getRootHash(hashes);
    }

    /**
     * Add a new hash to the tree.
     *
     * @param hash Hash to add.
     */
    public void update(final byte[] hash) {
        hashes.add(hash);
    }
}
