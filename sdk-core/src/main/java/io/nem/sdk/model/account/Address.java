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

import io.nem.core.crypto.RawAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * The address structure describes an address with its network.
 *
 * @since 1.0
 */
public class Address {


    private final String plainAddress;
    private final NetworkType networkType;

    /**
     * Constructor
     *
     * @param plainAddress Address in plain format
     * @param networkType Network type
     */
    public Address(String plainAddress, NetworkType networkType) {
        this.plainAddress =
            Objects.requireNonNull(plainAddress, "address must not be null")
                .replace("-", "")
                .trim()
                .toUpperCase();
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
        throw new IllegalArgumentException(rawAddress + " is an invalid address.");
    }

    /**
     * Create an Address from a given encoded address.
     *
     * @param encodedAddress String
     * @return {@link Address}
     */
    public static Address createFromEncoded(String encodedAddress) {
        try {
            return Address.createFromRawAddress(
                new String(new Base32().encode(Hex.decodeHex(encodedAddress))));
        } catch (DecoderException e) {
            throw new IllegalArgumentException(
                encodedAddress + " could not be decoded. " + ExceptionUtils.getMessage(e), e);
        }
    }

    /**
     * Returns the encoded address.
     *
     * @return the encoded plain address.
     */
    public String encoded() {
        return Hex.encodeHexString(new Base32().decode(plainAddress));
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
     * Gets address as byte buffer.
     *
     * @return Byte buffer.
     */
    public ByteBuffer getByteBuffer() {
        return ByteBuffer.wrap(new Base32().decode(plain().getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Get address in pretty format ex: SB3KUB-HATFCP-V7UZQL-WAQ2EU-R6SIHB-SBEOED-DDF3.
     *
     * @return String
     */
    public String pretty() {
        return this.plainAddress.substring(0, 6)
            + "-"
            + this.plainAddress.substring(6, 6 + 6)
            + "-"
            + this.plainAddress.substring(6 * 2, 6 * 2 + 6)
            + "-"
            + this.plainAddress.substring(6 * 3, 6 * 3 + 6)
            + "-"
            + this.plainAddress.substring(6 * 4, 6 * 4 + 6)
            + "-"
            + this.plainAddress.substring(6 * 5, 6 * 5 + 6)
            + "-"
            + this.plainAddress.substring(6 * 6, 6 * 6 + 4);
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
