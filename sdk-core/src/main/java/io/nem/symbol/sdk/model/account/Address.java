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

package io.nem.symbol.sdk.model.account;

import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.crypto.RawAddress;
import io.nem.symbol.core.utils.Base32Encoder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

/**
 * The address structure describes an address with its network.
 *
 * @since 1.0
 */
public class Address implements UnresolvedAddress {

    /**
     * The plain address size.
     */
    private static final int PLAIN_ADDRESS_SIZE = 40;

    /**
     * The raw address size.
     */
    private static final int RAW_ADDRESS_SIZE = 25;

    /**
     * The checksum size.
     */
    private static final int CHECKSUM_SIZE = 4;

    private final String plainAddress;

    private final NetworkType networkType;

    /**
     * Constructor
     *
     * @param plainAddress Address in plain format
     * @param networkType Network type
     */
    public Address(String plainAddress, NetworkType networkType) {
        Validate.notNull(plainAddress, "address must not be null");
        this.plainAddress = toPlainAddress(plainAddress);
        Validate.isTrue(this.plainAddress.length() == PLAIN_ADDRESS_SIZE,
            "Address " + plainAddress + " has to be " + PLAIN_ADDRESS_SIZE + " characters long.");
        this.networkType = Objects.requireNonNull(networkType, "networkType must not be null");
        char addressNetwork = this.plainAddress.charAt(0);
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
     * It normalizes a plain or pretty address into an upercase plain address.
     *
     * @param address the plain or pretty address.
     * @return the plain address.
     */
    private static String toPlainAddress(String address) {
        return address.trim().toUpperCase().replace("-", "");
    }

    /**
     * Create an Address from a given raw address.
     *
     * @param rawAddress String
     * @return {@link Address}
     */
    public static Address createFromRawAddress(String rawAddress) {
        String addressTrimAndUpperCase = toPlainAddress(rawAddress);
        Validate.isTrue(addressTrimAndUpperCase.length() == PLAIN_ADDRESS_SIZE,
            "Address " + addressTrimAndUpperCase + " has to be " + PLAIN_ADDRESS_SIZE
                + " characters long.");
        return new Address(addressTrimAndUpperCase, resolveNetworkType(addressTrimAndUpperCase));
    }

    /**
     * It resolve the network type from a given address using the first character.
     *
     * @param plainAddress the plain address
     * @return the network type.
     */
    private static NetworkType resolveNetworkType(String plainAddress) {
        char addressNetwork = plainAddress.charAt(0);
        if (addressNetwork == 'N') {
            return NetworkType.MAIN_NET;
        } else if (addressNetwork == 'T') {
            return NetworkType.TEST_NET;
        } else if (addressNetwork == 'M') {
            return NetworkType.MIJIN;
        } else if (addressNetwork == 'S') {
            return NetworkType.MIJIN_TEST;
        }
        throw new IllegalArgumentException(plainAddress + " is an invalid address.");

    }

    /**
     * Create an Address from a given encoded address.
     *
     * @param encodedAddress String
     * @return {@link Address}
     */
    public static Address createFromEncoded(String encodedAddress) {
        return Address.createFromRawAddress(
            Base32Encoder.getString(ConvertUtils.fromHexToBytes(encodedAddress)));
    }

    /**
     * Creates an address based on the public key and the network type. The sign schema will be
     * resolved based on the @{@link NetworkType}. See RawAddress.
     *
     * @param publicKey String
     * @param networkType the {@link NetworkType}
     * @return Address
     * @see RawAddress
     */
    public static Address createFromPublicKey(String publicKey, NetworkType networkType) {
        return new Address(RawAddress.generateAddress(publicKey, networkType), networkType);
    }

    /**
     * Determines the validity of a rawAddress address.
     *
     * @param rawAddress Decoded address.
     * @return true if the rawAddress address is valid, false otherwise.
     */
    public static boolean isValidPlainAddress(String rawAddress) {
        try {
            byte[] decodedArray = Base32Encoder.getBytes(rawAddress);
            Validate.isTrue(decodedArray.length == 25);
            int checksumBegin = RAW_ADDRESS_SIZE - CHECKSUM_SIZE;
            byte[] expectedChecksum = Arrays
                .copyOf(Hashes.sha3_256(Arrays.copyOf(decodedArray, checksumBegin)), CHECKSUM_SIZE);
            Validate.isTrue(expectedChecksum.length == 4);
            byte[] providedChecksum = Arrays
                .copyOfRange(decodedArray, checksumBegin, decodedArray.length);
            return Arrays
                .equals(expectedChecksum, providedChecksum);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Determines the validity of an encoded address string.
     *
     * @param encodedAddress encoded Encoded address string.
     * @return true if the encoded address string is valid, false otherwise.
     */
    public static boolean isValidEncodedAddress(String encodedAddress) {
        try {
            return isValidPlainAddress(
                Base32Encoder.getString(ConvertUtils.fromHexToBytes(encodedAddress)));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get address in plain format ex: SB3KUBHATFCPV7UZQLWAQ2EUR6SIHBSBEOEDDDF3.
     *
     * @return String
     */
    public String plain() {
        return this.plainAddress;
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
     * Returns the encoded address.
     *
     * @param networkType the network type.
     * @return the encoded plain address.
     */
    @Override
    public String encoded(NetworkType networkType) {
        return encoded();
    }

    /**
     * Returns the encoded address.
     *
     * @return the encoded plain address.
     */
    public String encoded() {
        return ConvertUtils.toHex(Base32Encoder.getBytes(plain()));
    }


    /**
     * Get address in pretty format ex: SB3KUB-HATFCP-V7UZQL-WAQ2EU-R6SIHB-SBEOED-DDF3.
     *
     * @return String
     */
    public String pretty() {
        return this.plainAddress.replaceAll("(.{6})", "$1-");
    }

    /**
     * Compares addresses for equality.
     *
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }
        Address address1 = (Address) o;
        return Objects.equals(plainAddress, address1.plainAddress)
            && networkType == address1.networkType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plainAddress, networkType);
    }

}
