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
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.crypto.RawAddress;
import io.nem.symbol.core.utils.Base32Encoder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * The address structure describes an address with its network.
 *
 * @since 1.0
 */
public class Address implements UnresolvedAddress {

    /**
     * The plain address size.
     */
    private static final int PLAIN_ADDRESS_SIZE = 39;

    /**
     * The raw address size.
     */
    private static final int RAW_ADDRESS_SIZE = 24;

    /**
     * The checksum size.
     */
    private static final int CHECKSUM_SIZE = RawAddress.NUM_CHECKSUM_BYTES;

    private final String plainAddress;

    private final NetworkType networkType;

    /**
     * Constructor
     *
     * @param prettyOrRaw The address in pretty or raw.
     * @param networkType Network type
     */
    public Address(String prettyOrRaw, NetworkType networkType) {
        this.plainAddress = toPlainAddress(Validate.notNull(prettyOrRaw, "address must not be null"));
        this.networkType = Objects.requireNonNull(networkType, "networkType must not be null");
        Optional<String> validationError = validatePlainAddress(this.plainAddress);
        Validate.isTrue(!validationError.isPresent(), validationError.orElse(""));
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
        Optional<String> validationError = validatePlainAddress(toPlainAddress(rawAddress));
        Validate.isTrue(!validationError.isPresent(), validationError.orElse(""));
        return new Address(rawAddress, resolveNetworkType(rawAddress));
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
        return Address.createFromRawAddress(fromEncodedToPlain(encodedAddress));
    }

    /**
     * Creates an address based on the public key and the network type. The sign schema will be resolved based on the
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
     * Determines the validity of a plainAddress address.
     *
     * @param plainAddress Decoded address.
     * @return true if the plainAddress address is valid, false otherwise.
     */
    public static boolean isValidPlainAddress(String plainAddress) {
        return !validatePlainAddress(plainAddress).isPresent();
    }


    /**
     * Determines the validity of an encoded address string.
     *
     * @param encodedAddress encoded Encoded address string.
     * @return true if the encoded address string is valid, false otherwise.
     */
    public static boolean isValidEncodedAddress(String encodedAddress) {
        return !validateEncodedAddress(encodedAddress).isPresent();
    }

    /**
     * Checks if an encoded address is valid returning the problem message if it's not valid.
     *
     * @param encodedAddress the encoded hex address.
     * @return the error message or emtpy if the address is valid.
     */
    public static Optional<String> validateEncodedAddress(String encodedAddress) {
        try {
            if (encodedAddress == null) {
                return Optional.of("Encoded Address it nos provided");
            }
            String plainAddress = fromEncodedToPlain(encodedAddress);
            return validatePlainAddress(plainAddress)
                .map(message -> "Encoded address: " + encodedAddress + " is invalid. " + message);
        } catch (Exception e) {
            return Optional
                .of("Encoded address " + encodedAddress + " is invalid. Error: " + ExceptionUtils.getMessage(e));
        }
    }

    /**
     * Checks if a plain address is valid returning the problem message if it's not valid.
     *
     * @param plainAddress the address to be checked.
     * @return the error message or emtpy if the address is valid.
     */
    public static Optional<String> validatePlainAddress(String plainAddress) {
        try {
            if (plainAddress == null) {
                return Optional.of("Plain Address it nos provided");
            }
            if (plainAddress.length() != PLAIN_ADDRESS_SIZE) {
                return Optional.of("Plain address '" + plainAddress + "' size is " + plainAddress.length() + " when "
                    + PLAIN_ADDRESS_SIZE + " is required");
            }
            byte[] decodedArray = Base32Encoder.getBytes(plainAddress);

            if (decodedArray.length != RAW_ADDRESS_SIZE) {
                return Optional
                    .of("Plain address '" + plainAddress + "' decoded address size is " + decodedArray.length + " when "
                        + RAW_ADDRESS_SIZE + " is required");
            }

            int checksumBegin = RAW_ADDRESS_SIZE - CHECKSUM_SIZE;
            byte[] expectedChecksum = Arrays
                .copyOf(Hashes.sha3_256(Arrays.copyOf(decodedArray, checksumBegin)), CHECKSUM_SIZE);

            if (expectedChecksum.length != CHECKSUM_SIZE) {
                return Optional
                    .of("Plain address '" + plainAddress + "' checksum size is " + expectedChecksum.length + " when "
                        + CHECKSUM_SIZE + " is required");
            }

            byte[] providedChecksum = Arrays
                .copyOfRange(decodedArray, checksumBegin, decodedArray.length);
            if (!Arrays.equals(expectedChecksum, providedChecksum)) {
                return Optional.of("Plain address '" + plainAddress + "' checksum is incorrect. Address checksum is '"
                    + ConvertUtils.toHex(providedChecksum) + "' when '" + ConvertUtils.toHex(expectedChecksum)
                    + "' is expected");
            }
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional
                .of("Plain address '" + plainAddress + "' is invalid. Error: " + ExceptionUtils.getMessage(e));
        }
    }

    /**
     * Generates a random address for the given network type.
     * @param networkType the network type
     * @return an random address.
     */
    public static Address generateRandom(NetworkType networkType) {
        return Address.createFromPublicKey(PublicKey.generateRandom().toHex(), networkType);
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
        return fromPlainToEncoded(plain());
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
     * Concert a encoded address to a plain one
     *
     * @param plain the plain address.
     * @return the encoded address.
     */
    private static String fromPlainToEncoded(String plain) {
        byte[] bytes = Base32Encoder.getBytes(plain);
        return ConvertUtils.toHex(bytes);
    }

    /**
     * Concerts an encoded to a plain one
     *
     * @param encoded the encoded address.
     * @return the encoded address.
     */
    private static String fromEncodedToPlain(String encoded) {
        byte[] bytes = ConvertUtils.fromHexToBytes(encoded);
        String rawAddress = Base32Encoder.getString(bytes);
        return rawAddress.substring(0, rawAddress.length() -1);
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
