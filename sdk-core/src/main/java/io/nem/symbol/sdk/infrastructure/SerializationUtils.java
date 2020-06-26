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

package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.AmountDto;
import io.nem.symbol.catapult.builders.FinalizationPointDto;
import io.nem.symbol.catapult.builders.GeneratorUtils;
import io.nem.symbol.catapult.builders.Hash256Dto;
import io.nem.symbol.catapult.builders.KeyDto;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.NamespaceIdDto;
import io.nem.symbol.catapult.builders.SignatureDto;
import io.nem.symbol.catapult.builders.UnresolvedAddressDto;
import io.nem.symbol.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.symbol.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.symbol.catapult.builders.VotingKeyDto;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.crypto.VotingKey;
import io.nem.symbol.core.utils.Base32Encoder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
        return PublicAccount.createFromPublicKey(ConvertUtils.toHex(keyDto.getKey().array()), networkType);
    }

    /**
     * It creates a Mosaic from an {@link UnresolvedMosaicBuilder}.
     *
     * @param builder the catbuffer {@link UnresolvedMosaicBuilder}.
     * @return the model {@link Mosaic}
     */
    public static Mosaic toMosaic(UnresolvedMosaicBuilder builder) {
        return new Mosaic(new MosaicId(toUnsignedBigInteger(builder.getMosaicId().getUnresolvedMosaicId())),
            toUnsignedBigInteger(builder.getAmount().getAmount()));
    }

    /**
     * It creates a {@link UnresolvedMosaicId} from an {@link UnresolvedMosaicIdDto}.
     *
     * @param dto the catbuffer {@link UnresolvedMosaicIdDto}.
     * @return the model {@link UnresolvedMosaicId}
     */
    public static UnresolvedMosaicId toUnresolvedMosaicId(UnresolvedMosaicIdDto dto) {
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
     * It serializes a {@link UnresolvedAddress} to an hex catbuffer understand.
     *
     * @param unresolvedAddress the {@link Address} or {@link NamespaceId} to be serialized.
     * @param networkType the network type to customize the {@link NamespaceId} serialization
     * @return the serialized {@link UnresolvedAddress} as {@link ByteBuffer}.
     */
    public static ByteBuffer fromUnresolvedAddressToByteBuffer(UnresolvedAddress unresolvedAddress,
        NetworkType networkType) {
        Validate.notNull(unresolvedAddress, "unresolvedAddress must not be null");

        if (unresolvedAddress instanceof NamespaceId) {
            final ByteBuffer namespaceIdAlias = ByteBuffer.allocate(24);
            NamespaceId namespaceId = (NamespaceId) unresolvedAddress;
            final byte firstByte = (byte) (networkType.getValue() | 0x01);
            namespaceIdAlias.order(ByteOrder.LITTLE_ENDIAN);
            namespaceIdAlias.put(firstByte);
            namespaceIdAlias.putLong(namespaceId.getIdAsLong());
            return ByteBuffer.wrap(namespaceIdAlias.array());
        }

        if (unresolvedAddress instanceof Address) {
            return fromAddressToByteBuffer((Address) unresolvedAddress);
        }
        throw new IllegalArgumentException("Unexpected UnresolvedAddress type " + unresolvedAddress.getClass());
    }

    /**
     * It serializes an resolved {@link Address} into a {@link ByteBuffer}
     *
     * @param resolvedAddress the resolved address
     * @return the serialized {@link ByteBuffer}
     */
    public static ByteBuffer fromAddressToByteBuffer(Address resolvedAddress) {
        return ByteBuffer.wrap(Base32Encoder.getBytes(resolvedAddress.plain()));
    }


    /**
     * It serializes an resolved {@link Address} into a {@link ByteBuffer}
     *
     * @param resolvedAddress the resolved address
     * @return the serialized {@link ByteBuffer}
     */
    public static AddressDto toAddressDto(Address resolvedAddress) {
        return new AddressDto(fromAddressToByteBuffer(resolvedAddress));
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
     * It creates a {@link UnresolvedAddressDto} from an {@link UnresolvedAddress}.
     *
     * @param unresolvedAddress the catbuffer {@link UnresolvedAddressDto}.
     * @param networkType the network type serialized in the payload.
     * @return the dto {@link UnresolvedAddressDto}
     */
    public static UnresolvedAddressDto toUnresolvedAddress(UnresolvedAddress unresolvedAddress,
        NetworkType networkType) {
        return new UnresolvedAddressDto(
            SerializationUtils.fromUnresolvedAddressToByteBuffer(unresolvedAddress, networkType));
    }


    /**
     * It creates a {@link UnresolvedAddress} from an {@link UnresolvedAddressDto}.
     *
     * @param dto the catbuffer {@link UnresolvedAddressDto}.
     * @return the model {@link UnresolvedAddress}
     */
    public static UnresolvedAddress toUnresolvedAddress(UnresolvedAddressDto dto) {
        return MapperUtils.toUnresolvedAddress(ConvertUtils.toHex(dto.getUnresolvedAddress().array()));
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
     * It creates a {@link DataInputStream} from a binary payload.
     *
     * @param payload the payload
     * @return the {@link DataInputStream} catbuffer uses.
     */
    public static DataInputStream toDataInput(byte[] payload) {
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
        return ConvertUtils.toHex(dto.getHash256().array()).toUpperCase();
    }

    /**
     * It extracts the hex string from the {@link ByteBuffer}
     *
     * @param buffer the {@link ByteBuffer}
     * @return the hex string.
     */
    public static String toHexString(ByteBuffer buffer) {
        return ConvertUtils.toHex(buffer.array()).toUpperCase();
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

    /**
     * It concats the 2 byte arrays patching the int size at the beginning of the first byte array setting up the sum of
     * both lengths.
     *
     * @param commonBytes the common transaction byte array
     * @param transactionBytes the specific transaction byte array.
     * @return the concated byte array.
     */
    public static byte[] concat(byte[] commonBytes, byte[] transactionBytes) {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeInt(Integer.reverseBytes(commonBytes.length + transactionBytes.length));
            dataOutputStream.write(commonBytes, 4, commonBytes.length - 4);
            dataOutputStream.write(transactionBytes);
        });
    }

    /**
     * It serializes the public key of a public account.
     *
     * @param publicAccount to be serialized.
     * @return the public account
     */
    public static ByteBuffer toByteBuffer(PublicAccount publicAccount) {
        final byte[] bytes = publicAccount.getPublicKey().getBytes();
        return ByteBuffer.wrap(bytes);
    }

    /**
     * It serializes the string signature into a SignatureDto catbuffer understands
     *
     * @param signature the signature string
     * @return SignatureDto.
     */
    public static SignatureDto toSignatureDto(String signature) {
        return new SignatureDto(ByteBuffer.wrap(ConvertUtils.getBytes(signature)));
    }

    /**
     * It creates a catbuffer KeyDto from a {@link PublicKey}.
     *
     * @param publicKey the public key.
     * @return the keyDto
     */
    public static KeyDto toKeyDto(PublicKey publicKey) {
        return new KeyDto(ByteBuffer.wrap(publicKey.getBytes()));
    }

    /**
     * It creates a catbuffer {@link PublicKey} from a {@link KeyDto}.
     *
     * @param dto the public key.
     * @return the {@link PublicKey}
     */
    public static PublicKey toPublicKey(KeyDto dto) {
        return new PublicKey(dto.getKey().array());
    }

    /**
     * It creates a {@link VotingKey} from the DTO
     *
     * @param votingKeyDto the dto
     * @return the {@link VotingKey}
     */
    public static VotingKey toVotingKey(VotingKeyDto votingKeyDto) {
        return new VotingKey(votingKeyDto.getVotingKey().array());
    }

    /**
     * It creates a catbuffer VotingKeyDto from a {@link VotingKey}.
     *
     * @param key the voting key.
     * @return the VotingKeyDto
     */
    public static VotingKeyDto toVotingKeyDto(VotingKey key) {
        return new VotingKeyDto(ByteBuffer.wrap(key.getBytes()));
    }

    /**
     * It creates a catbuffer Hash256Dto from a String hash.
     *
     * @param hash the hash
     * @return the {@link Hash256Dto}
     */
    public static Hash256Dto toHash256Dto(String hash) {
        return new Hash256Dto(ByteBuffer.wrap(ConvertUtils.fromHexToBytes(hash)));
    }

    /**
     * Converts an a model {@link UnresolvedMosaicId} into an {@link UnresolvedMosaicIdDto} from catbuffer.
     *
     * @param mosaicId the model
     * @return the dto
     */
    public static UnresolvedMosaicIdDto toUnresolvedMosaicIdDto(UnresolvedMosaicId mosaicId) {
        return new UnresolvedMosaicIdDto(mosaicId.getId().longValue());
    }

    /**
     * Converts an a model {@link MosaicId} into an {@link MosaicIdDto} from catbuffer.
     *
     * @param mosaicId the model
     * @return the dto
     */
    public static MosaicIdDto toMosaicIdDto(MosaicId mosaicId) {
        return new MosaicIdDto(mosaicId.getId().longValue());
    }

    /**
     * @param finalizationPoint the big int finalization point
     * @return a new FinalizationPointDto from the big integer.
     */
    public static FinalizationPointDto toFinalizationPointDto(BigInteger finalizationPoint) {
        return new FinalizationPointDto(finalizationPoint.longValue());
    }
}
