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

package io.nem.sdk.model.transaction;

import io.nem.core.crypto.Hashes;
import io.nem.core.utils.ByteUtils;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.model.mosaic.IllegalIdentifierException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Id generator
 */
public class IdGenerator {

    /**
     * Private constructor for this utility class.
     */
    private IdGenerator() {
    }

    private static final long ID_GENERATOR_FLAG = 0x8000000000000000L;

    /**
     * Generate mosaic id.
     *
     * @param nonce Nonce bytes.
     * @param publicKey Public key.
     * @return Mosaic id.
     */
    public static BigInteger generateMosaicId(final byte[] nonce, final byte[] publicKey) {
        final byte[] reverseNonce = ByteUtils.reverseCopy(nonce);
        final byte[] hash = IdGenerator.getHashInLittleEndian(reverseNonce, publicKey);
        // Unset the high bit for mosaic id
        return BigInteger.valueOf(ByteBuffer.wrap(hash).getLong() & ~ID_GENERATOR_FLAG);
    }

    /**
     * Generate namespace id.
     *
     * @param namespaceName Namespace name.
     * @param parentId Parent id.
     * @return Namespace id.
     */
    public static BigInteger generateNamespaceId(final String namespaceName,
        final BigInteger parentId) {
        if (!namespaceName.matches("^[a-z0-9][a-z0-9-_]*$")) {
            throw new IllegalIdentifierException("invalid namespace name");
        }

        final ByteBuffer parentIdBuffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
            .putLong(parentId.longValue());
        final byte[] hash = IdGenerator
            .getHashInLittleEndian(parentIdBuffer.array(), namespaceName.getBytes());
        // Set the high bit for namespace id
        return ConvertUtils
            .toUnsignedBigInteger(ByteBuffer.wrap(hash).getLong() | ID_GENERATOR_FLAG);
    }

    /**
     * Generate namespace id.
     *
     * @param namespaceName Namespace name.
     * @param parentNamespaceName Parent name.
     * @return Namespace id.
     */
    public static BigInteger generateNamespaceId(String namespaceName, String parentNamespaceName) {
        return IdGenerator.generateNamespaceId(parentNamespaceName + "." + namespaceName);
    }

    /**
     * Generate namespace id.
     *
     * @param namespacePath Namespace path.
     * @return Namespace id.
     */
    public static BigInteger generateNamespaceId(String namespacePath) {
        List<BigInteger> namespaceList = generateNamespacePath(namespacePath);
        return namespaceList.get(namespaceList.size() - 1);
    }

    /**
     * Generate namespace id.
     *
     * @param namespacePath Namespace path.
     * @return List of namespace id.
     */
    public static List<BigInteger> generateNamespacePath(String namespacePath) {
        String[] parts = namespacePath.split(Pattern.quote("."));
        List<BigInteger> path = new ArrayList<>();

        if (parts.length == 0) {
            throw new IllegalIdentifierException("invalid namespace path");
        } else if (parts.length > 3) {
            throw new IllegalIdentifierException("too many parts");
        }

        BigInteger namespaceId = BigInteger.valueOf(0);

        for (int i = 0; i < parts.length; i++) {
            namespaceId = generateNamespaceId(parts[i], namespaceId);
            path.add(namespaceId);
        }
        return path;
    }

    /**
     * Gets hash in little endian.
     *
     * @param inputs Inputs to hash.
     * @return Hash value.
     */
    private static byte[] getHashInLittleEndian(final byte[]... inputs) {
        byte[] result = Hashes.sha3_256(inputs);
        result = Arrays.copyOfRange(result, 0, 8);
        ArrayUtils.reverse(result);
        return result;
    }
}
