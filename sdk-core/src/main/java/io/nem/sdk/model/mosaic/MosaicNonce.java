package io.nem.sdk.model.mosaic;

import io.nem.core.utils.ByteUtils;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.infrastructure.RandomUtils;
import java.math.BigInteger;

/**
 * Mosaic nonce class
 */
public class MosaicNonce {

    /**
     * The number of bytes of the nonce.
     */
    private final static int NO_OF_RANDOM_BYTES = 4;
    /**
     * Mosaic nonce
     */
    private final byte[] nonce;

    /**
     * Create MosaicNonce from byte array
     *
     * @param nonce the nonce as byte array.
     */
    public MosaicNonce(byte[] nonce) {
        this.nonce = nonce;
    }

    /**
     * Create a random MosaicNonce
     *
     * @return MosaicNonce nonce
     */
    public static MosaicNonce createRandom() {
        return new MosaicNonce(RandomUtils.generateRandomBytes(NO_OF_RANDOM_BYTES));
    }

    /**
     * Create a MosaicNonce from hexadecimal notation.
     *
     * @param hex the hex value.
     * @return MosaicNonce
     */
    public static MosaicNonce createFromHex(String hex) {
        final byte[] bytes = ConvertUtils.fromHexToBytes(hex);
        if (bytes.length != 4) {
            throw new IllegalIdentifierException(
                "Expected 4 bytes for Nonce but got " + bytes.length + " instead.");
        }
        return new MosaicNonce(bytes);
    }

    /**
     * Create a MosaicNonce from a BigInteger.
     *
     * @param number the nonce as number.
     * @return MosaicNonce
     */
    public static MosaicNonce createFromBigInteger(BigInteger number) {
        return new MosaicNonce(ByteUtils.bigIntToBytesOfSize(number, 4));
    }

    /**
     * Create a MosaicNonce from a BigInteger.
     *
     * @param number the nonce as number.
     * @return MosaicNonce
     */
    public static MosaicNonce createFromBigInteger(Long number) {
        return new MosaicNonce(ByteUtils.bigIntToBytesOfSize(BigInteger.valueOf(number), 4));
    }

    /**
     * @return nonce
     */
    public byte[] getNonce() {
        return nonce;
    }

    /**
     * @return nonce int
     */
    public int getNonceAsInt() {
        return ByteUtils.bytesToInt(this.nonce);
    }
}
