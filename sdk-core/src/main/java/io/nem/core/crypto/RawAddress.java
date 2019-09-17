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

package io.nem.core.crypto;

import io.nem.core.crypto.Hashes;
import io.nem.core.crypto.SignSchema;
import io.nem.core.utils.ArrayUtils;
import io.nem.core.utils.Base32Encoder;
import io.nem.sdk.model.blockchain.NetworkType;
import java.util.Arrays;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Utility class that knows how to create an address based on a public key. It supports the
 * different hashes algorithms defined in {@link SignSchema}.
 */
public class RawAddress {

    private static final int NUM_CHECKSUM_BYTES = 4;

    /**
     * Private utility class constructor.
     */
    private RawAddress() {
        //private utility class constructor.
    }

    /**
     * This method generates an address based on the public key, and the Catapult configuration
     * network type and sign schema.
     *
     * @param publicKey the public key
     * @param networkType the network type
     * @param signSchema the signature
     * @return an encoded address that can be used to identify accounts.
     */
    public static String generateAddress(final String publicKey, final NetworkType networkType,
        final SignSchema signSchema) {
        byte version = (byte) networkType.getValue();
        // step 1: sha3 hash of the public key
        byte[] publicKeyBytes;
        try {
            publicKeyBytes = Hex.decodeHex(publicKey);
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Public key is not valid");
        }
        final byte[] publicKeyHash = toHash(publicKeyBytes, signSchema);

        // step 2: ripemd160 hash of (1)
        final byte[] ripemd160StepOneHash = Hashes.ripemd160(publicKeyHash);

        // step 3: add version byte in front of (2)
        final byte[] versionPrefixedRipemd160Hash =
            ArrayUtils.concat(new byte[]{version}, ripemd160StepOneHash);

        // step 4: get the checksum of (3)
        final byte[] stepThreeChecksum = generateChecksum(versionPrefixedRipemd160Hash, signSchema);

        // step 5: concatenate (3) and (4)
        final byte[] concatStepThreeAndStepSix =
            ArrayUtils.concat(versionPrefixedRipemd160Hash, stepThreeChecksum);

        // step 6: base32 encode (5)
        return Base32Encoder.getString(concatStepThreeAndStepSix);
    }

    private static byte[] toHash(byte[] publicKeyBytes, SignSchema signSchema) {
        if (signSchema == SignSchema.SHA3) {
            return Hashes.sha3_256(publicKeyBytes);
        }

        if (signSchema == SignSchema.KECCAK_REVERSED_KEY) {
            return Hashes.keccak256(publicKeyBytes);
        }
        throw new IllegalStateException("Unknown SignSchema " + signSchema);
    }

    private static byte[] generateChecksum(final byte[] input, SignSchema signSchema) {
        // step 1: sha3 hash of (input
        final byte[] sha3StepThreeHash = toHash(input, signSchema);

        // step 2: get the first X bytes of (1)
        return Arrays.copyOfRange(sha3StepThreeHash, 0, NUM_CHECKSUM_BYTES);
    }
}
