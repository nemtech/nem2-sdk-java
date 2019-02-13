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

package io.nem.sdk.model.account;

import io.nem.core.crypto.Hashes;
import io.nem.core.utils.ArrayUtils;
import io.nem.core.utils.Base32Encoder;
import io.nem.core.utils.HexEncoder;
import io.nem.sdk.model.blockchain.NetworkType;

import java.util.Arrays;
import java.util.Objects;

/**
 * The address structure describes an address with its network.
 *
 * @since 1.0
 */
public class Address {
    private static final int NUM_CHECKSUM_BYTES = 4;
    final private String address;
    final private NetworkType networkType;

    /**
     * Constructor
     *
     * @param address     Address in plain format
     * @param networkType Network type
     */
    public Address(String address, NetworkType networkType) {
        this.address = Objects
                .requireNonNull(address, "address must not be null")
                .replace("-", "")
                .trim()
                .toUpperCase();
        this.networkType = Objects.requireNonNull(networkType, "networkType must not be null");
        char addressNetwork = this.address.charAt(0);
        if (networkType.equals(NetworkType.MAIN_NET) && addressNetwork != 'N') {
            throw new IllegalArgumentException("MAIN_NET Address must start with N");
        } else if (networkType.equals(NetworkType.TEST_NET) && addressNetwork != 'T') {
            throw new IllegalArgumentException("TEST_NET Address must start with T");
        } else if (networkType.equals(NetworkType.MIJIN) && addressNetwork != 'M') {
            throw new IllegalArgumentException("MIJIN Address must start with M");
        } else if (networkType.equals(NetworkType.MIJIN_TEST) && addressNetwork != 'S') {
            throw new IllegalArgumentException("MIJIN_TEST Address must start with S");
        }
    }

    /**
     * Create an Address from a given raw address.
     *
     * @param rawAddress String
     * @return {@link Address}
     */
    public static Address createFromRawAddress(String rawAddress) {
        char addressNetwork = rawAddress.charAt(0);
        if (addressNetwork == 'N') {
            return new Address(rawAddress, NetworkType.MAIN_NET);
        } else if (addressNetwork == 'T') {
            return new Address(rawAddress, NetworkType.TEST_NET);
        } else if (addressNetwork == 'M') {
            return new Address(rawAddress, NetworkType.MIJIN);
        } else if (addressNetwork == 'S') {
            return new Address(rawAddress, NetworkType.MIJIN_TEST);
        }
        throw new IllegalArgumentException("Address is invalid");
    }

    /**
     * Create an Address from a given encoded address.
     *
     * @param encodedAddress String
     * @return {@link Address}
     */
    public static Address createFromEncoded(String encodedAddress) {
        try {
            return Address.createFromRawAddress(Base32Encoder.getString(HexEncoder.getBytes(encodedAddress)));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * Create from private key.
     *
     * @param publicKey   String
     * @param networkType NetworkType
     * @return Address
     */
    public static Address createFromPublicKey(String publicKey, NetworkType networkType) {
        return new Address(generateEncoded((byte) networkType.getValue(), publicKey), networkType);
    }

    private static String generateEncoded(final byte version, final String publicKey) {
        // step 1: sha3 hash of the public key
        byte[] publicKeyBytes;
        try {
            publicKeyBytes = HexEncoder.getBytes(publicKey);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("public key is not valid");
        }
        final byte[] sha3PublicKeyHash = Hashes.sha3_256(publicKeyBytes);

        // step 2: ripemd160 hash of (1)
        final byte[] ripemd160StepOneHash = Hashes.ripemd160(sha3PublicKeyHash);

        // step 3: add version byte in front of (2)
        final byte[] versionPrefixedRipemd160Hash = ArrayUtils.concat(new byte[]{version}, ripemd160StepOneHash);

        // step 4: get the checksum of (3)
        final byte[] stepThreeChecksum = generateChecksum(versionPrefixedRipemd160Hash);

        // step 5: concatenate (3) and (4)
        final byte[] concatStepThreeAndStepSix = ArrayUtils.concat(versionPrefixedRipemd160Hash, stepThreeChecksum);

        // step 6: base32 encode (5)
        return Base32Encoder.getString(concatStepThreeAndStepSix);
    }

    private static byte[] generateChecksum(final byte[] input) {
        // step 1: sha3 hash of (input
        final byte[] sha3StepThreeHash = Hashes.sha3_256(input);

        // step 2: get the first X bytes of (1)
        return Arrays.copyOfRange(sha3StepThreeHash, 0, NUM_CHECKSUM_BYTES);
    }

    /**
     * Get address in plain format ex: SB3KUBHATFCPV7UZQLWAQ2EUR6SIHBSBEOEDDDF3.
     *
     * @return String
     */
    public String plain() {
        return this.address;
    }

    /**
     * Returns network type.
     *
     * @return {@link NetworkType}
     */
    public NetworkType getNetworkType() {
        return networkType;
    }

    /**
     * Get address in pretty format ex: SB3KUB-HATFCP-V7UZQL-WAQ2EU-R6SIHB-SBEOED-DDF3.
     *
     * @return String
     */
    public String pretty() {
        return this.address.substring(0, 6) +
                "-" +
                this.address.substring(6, 6 + 6) +
                "-" +
                this.address.substring(6 * 2, 6 * 2 + 6) +
                "-" +
                this.address.substring(6 * 3, 6 * 3 + 6) +
                "-" +
                this.address.substring(6 * 4, 6 * 4 + 6) +
                "-" +
                this.address.substring(6 * 5, 6 * 5 + 6) +
                "-" +
                this.address.substring(6 * 6, 6 * 6 + 4);
    }

    /**
     * Compares addresses for equality.
     *
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address1 = (Address) o;
        return Objects.equals(address, address1.address) &&
                networkType == address1.networkType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, networkType);
    }
}
