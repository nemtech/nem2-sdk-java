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

import io.nem.catapult.builders.AddressDto;
import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.Hash256Dto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicIdDto;
import io.nem.catapult.builders.NamespaceIdDto;
import io.nem.catapult.builders.UnresolvedAddressDto;
import io.nem.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.MapperUtils;
import io.nem.core.utils.StringEncoder;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.Validate;

/**
 * Utility class used to serialize/deserialize catbuffer values.
 */
public class SerializationUtils {

    /**
     * Private constructor.
     */
    private SerializationUtils() {

    }

    /**
     * It creates a PublicAccount from a {@link KeyDto}.
     *
     * @param keyDto catbuffer {@link KeyDto}.
     * @param networkType the network type
     * @return the model {@link PublicAccount}
     */
    public static PublicAccount toPublicAccount(KeyDto keyDto, NetworkType networkType) {
        return PublicAccount
            .createFromPublicKey(ConvertUtils.toHex(keyDto.getKey().array()), networkType);
    }

    /**
     * It creates a Mosaic from an {@link UnresolvedMosaicBuilder}.
     *
     * @param builder the catbuffer {@link UnresolvedMosaicBuilder}.
     * @return the model {@link Mosaic}
     */
    public static Mosaic toMosaic(UnresolvedMosaicBuilder builder) {
        return new Mosaic(
            new MosaicId(toUnsignedBigInteger(builder.getMosaicId().getUnresolvedMosaicId())),
            toUnsignedBigInteger(builder.getAmount().getAmount()));
    }

    /**
     * It creates a {@link MosaicId} from an {@link UnresolvedMosaicIdDto}.
     *
     * @param dto the catbuffer {@link UnresolvedMosaicIdDto}.
     * @return the model {@link MosaicId}
     */
    public static UnresolvedMosaicId toMosaicId(UnresolvedMosaicIdDto dto) {
        return new MosaicId(toUnsignedBigInteger(dto.getUnresolvedMosaicId()));
    }

    /**
     * It creates a {@link MosaicId} from an {@link MosaicIdDto}.
     *
     * @param dto the catbuffer {@link MosaicIdDto}.
     * @return the model {@link MosaicId}
     */
    public static MosaicId toMosaicId(MosaicIdDto dto) {
        return new MosaicId(toUnsignedBigInteger(dto.getMosaicId()));
    }

    /**
     * It creates a {@link NamespaceId} from an {@link NamespaceIdDto}.
     *
     * @param dto the catbuffer {@link NamespaceIdDto}.
     * @return the model {@link NamespaceId}
     */
    public static NamespaceId toNamespaceId(NamespaceIdDto dto) {
        return NamespaceId.createFromId(toUnsignedBigInteger(dto.getNamespaceId()));
    }

    /**
     * It creates a {@link Address} from an {@link UnresolvedAddressDto}.
     *
     * @param dto the catbuffer {@link UnresolvedAddressDto}.
     * @return the model {@link Address}
     */
    public static UnresolvedAddress toAddress(UnresolvedAddressDto dto) {
        return MapperUtils.toUnresolvedAddress(ConvertUtils.toHex(dto.getUnresolvedAddress().array()));
    }

    /**
     * It serializes a UnresolvedAddress to a xx
     */
    public static ByteBuffer fromUnresolvedAddressToByteBuffer(
        UnresolvedAddress unresolvedAddress) {
        Validate.notNull(unresolvedAddress, "unresolvedAddress must not be null");

        if (unresolvedAddress instanceof NamespaceId) {
            final ByteBuffer namespaceIdAlias = ByteBuffer.allocate(25);
            final byte firstByte = (byte) 0x91;
            namespaceIdAlias.order(ByteOrder.LITTLE_ENDIAN);
            namespaceIdAlias.put(firstByte);
            namespaceIdAlias.putLong(((NamespaceId) unresolvedAddress).getIdAsLong());
            return ByteBuffer.wrap(namespaceIdAlias.array());
        }

        if (unresolvedAddress instanceof Address) {
            return ByteBuffer.wrap(new Base32().decode(((Address) unresolvedAddress).plain()));
        }
        throw new IllegalArgumentException(
            "Unexpected UnresolvedAddress type " + unresolvedAddress.getClass());
    }


    /**
     * It creates a {@link Address} from an {@link AddressDto}.
     *
     * @param dto the catbuffer {@link AddressDto}.
     * @return the model {@link Address}
     */
    public static Address toAddress(AddressDto dto) {
        return Address.createFromEncoded(ConvertUtils.toHex(dto.getAddress().array()));
    }


    /**
     * It converts a signed byte to a positive integer.
     *
     * @param value the byte, it can be a overflowed negative byte.
     * @return the positive integer.
     */
    public static int byteToUnsignedInt(byte value) {
        return value & 0xFF;
    }

    /**
     * It converts a signed short to a positive integer.
     *
     * @param value the short, it can be a overflowed negative short.
     * @return the positive integer.
     */
    public static int shortToUnsignedInt(short value) {
        return value & 0xFFFF;
    }

    /**
     * It creates a {@link DataInput} from a binary payload.
     *
     * @param payload the payload
     * @return the {@link DataInput} catbuffer uses.
     */
    public static DataInput toDataInput(byte[] payload) {
        return new DataInputStream(new ByteArrayInputStream(payload));
    }

    /**
     * It converts an AmountDto into a positive {@link BigInteger}.
     *
     * @param amountDto the catbuffer {@link AmountDto}
     * @return the positive {@link BigInteger}.
     */
    public static BigInteger toUnsignedBigInteger(AmountDto amountDto) {
        return toUnsignedBigInteger(amountDto.getAmount());
    }

    /**
     * It converts a signed long to a positive integer.
     *
     * @param value the short, it can be a overflowed negative long.
     * @return the positive integer.
     */
    public static BigInteger toUnsignedBigInteger(long value) {
        return ConvertUtils.toUnsignedBigInteger(value);
    }

    /**
     * It extracts the hex string from the {@link Hash256Dto}
     *
     * @param dto the {@link Hash256Dto}
     * @return the hex string.
     */
    public static String toHexString(Hash256Dto dto) {
        return ConvertUtils.toHex(dto.getHash256().array());
    }

    /**
     * It extracts an UTF-8 string from the {@link ByteBuffer}
     *
     * @param buffer the {@link ByteBuffer}
     * @return the UTF-8 string.
     */
    public static String toString(ByteBuffer buffer) {
        return StringEncoder.getString(buffer.array());
    }


}
