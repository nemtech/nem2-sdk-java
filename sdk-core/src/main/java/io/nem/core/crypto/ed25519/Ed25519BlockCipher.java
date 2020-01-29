/*
 * Copyright 2018 NEM
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

package io.nem.core.crypto.ed25519;

import io.nem.core.crypto.BlockCipher;
import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PrivateKey;
import io.nem.core.crypto.PublicKey;
import io.nem.core.crypto.SignSchema;
import io.nem.core.crypto.ed25519.arithmetic.Ed25519EncodedGroupElement;
import io.nem.core.crypto.ed25519.arithmetic.Ed25519GroupElement;
import io.nem.sdk.infrastructure.RandomUtils;
import java.util.Arrays;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Implementation of the block cipher for Ed25519.
 */
public class Ed25519BlockCipher implements BlockCipher {

    private static final int IV_LENGTH = 16;

    private final KeyPair senderKeyPair;
    private final KeyPair recipientKeyPair;
    private final SignSchema signSchema;

    public Ed25519BlockCipher(final KeyPair senderKeyPair, final KeyPair recipientKeyPair,
        final SignSchema signSchema) {
        this.senderKeyPair = senderKeyPair;
        this.recipientKeyPair = recipientKeyPair;
        this.signSchema = signSchema;
    }

    @Override
    @SuppressWarnings("squid:S1168")
    public byte[] encrypt(final byte[] input) {
        // Setup salt.

        // Derive shared key.
        final byte[] sharedKey = getSharedKey(this.senderKeyPair.getPrivateKey(),
            this.recipientKeyPair.getPublicKey(), signSchema);

        // Setup IV.
        final byte[] ivData = RandomUtils.generateRandomBytes(IV_LENGTH);

        // Setup block cipher.
        final BufferedBlockCipher cipher = setupBlockCipher(sharedKey, ivData, true);

        // Encode.
        final byte[] buf = transform(cipher, input);
        if (null == buf) {
            return null;
        }

        final byte[] result = new byte[ivData.length + buf.length];
        System.arraycopy(ivData, 0, result, 0, ivData.length);
        System.arraycopy(buf, 0, result, ivData.length, buf.length);
        return result;
    }

    @Override
    @SuppressWarnings("squid:S1168")
    public byte[] decrypt(final byte[] input) {
        if (input.length < 32) {
            return null;
        }

        final byte[] ivData = Arrays.copyOfRange(input, 0, IV_LENGTH);
        final byte[] encData = Arrays.copyOfRange(input, IV_LENGTH, input.length);

        // Derive shared key.
        final byte[] sharedKey = getSharedKey(this.recipientKeyPair.getPrivateKey(),
            this.senderKeyPair.getPublicKey(), signSchema);

        // Setup block cipher.
        final BufferedBlockCipher cipher = setupBlockCipher(sharedKey, ivData, false);

        // Decode.
        return transform(cipher, encData);
    }

    @SuppressWarnings("squid:S1168")
    public static byte[] transform(final BufferedBlockCipher cipher, final byte[] data) {
        final byte[] buf = new byte[cipher.getOutputSize(data.length)];
        int length = cipher.processBytes(data, 0, data.length, buf, 0);
        try {
            length += cipher.doFinal(buf, length);
        } catch (final InvalidCipherTextException e) {
            return null;
        }

        return Arrays.copyOf(buf, length);
    }

    public static BufferedBlockCipher setupBlockCipher(
        final byte[] sharedKey, final byte[] ivData, final boolean forEncryption) {
        // Setup cipher parameters with key and IV.
        final KeyParameter keyParam = new KeyParameter(sharedKey);
        final CipherParameters params = new ParametersWithIV(keyParam, ivData);

        // Setup AES cipher in CBC mode with PKCS7 padding.
        final BlockCipherPadding padding = new PKCS7Padding();
        final BufferedBlockCipher cipher =
            new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), padding);
        cipher.reset();
        cipher.init(forEncryption, params);
        return cipher;
    }

    public static byte[] getSharedKey(final PrivateKey privateKey, final PublicKey publicKey,
        final SignSchema signSchema) {
        final byte[] sharedSecret = getSharedSecret(privateKey, publicKey, signSchema);
        Digest hash = new SHA256Digest();
        byte[] info = "catapult".getBytes();
        int length = 32;
        byte[] sharedKey = new byte[length];
        HKDFParameters params = new HKDFParameters(sharedSecret, null, info);
        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(hash);
        hkdf.init(params);
        hkdf.generateBytes(sharedKey, 0, length);
        return sharedKey;
    }

    public static byte[] getSharedSecret(final PrivateKey privateKey, final PublicKey publicKey,
        final SignSchema signSchema) {
        final Ed25519GroupElement senderA =
            new Ed25519EncodedGroupElement(publicKey.getBytes()).decode();
        senderA.precomputeForScalarMultiplication();
        return senderA
            .scalarMultiply(Ed25519Utils.prepareForScalarMultiply(privateKey, signSchema))
            .encode()
            .getRaw();
    }
}
