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
package io.nem.symbol.core.utils;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;

/** Utility class for mappers. */
public class MapperUtils {

  /** Private Contructors. */
  private MapperUtils() {}

  /**
   * Creates a {@link NamespaceId} from the provided hex string.
   *
   * @param hex the hex string with the id
   * @return a namespace id from the hex number or null if the hex is null
   */
  public static NamespaceId toNamespaceId(String hex) {
    return hex == null ? null : NamespaceId.createFromId(fromHexToBigInteger(hex));
  }

  /**
   * Creates a {@link MosaicId} from the provided hex string.
   *
   * @param hex the hex string with the id
   * @return a {@link MosaicId} from the hex number or null if the hex is null
   */
  public static MosaicId toMosaicId(String hex) {
    return hex == null ? null : new MosaicId(fromHexToBigInteger(hex));
  }

  /**
   * Creates a {@link MosaicId} or {@link NamespaceId }from the provided hex string.
   *
   * @param hex the hex string with the id
   * @return a {@link UnresolvedMosaicId} from the hex number or null if the hex is null
   */
  public static UnresolvedMosaicId toUnresolvedMosaicId(String hex) {
    if (hex == null) {
      return null;
    }
    if ((ConvertUtils.getBytes(hex)[0] & 128) == 128) {
      return toNamespaceId(hex);
    } else {
      return toMosaicId(hex);
    }
  }

  /**
   * Creates a {@link MosaicId} or {@link Address }from the provided hex string.
   *
   * @param hex the hex string with the id
   * @return a {@link UnresolvedAddress} from the hex number or null if the hex is null
   */
  public static UnresolvedAddress toUnresolvedAddress(String hex) {
    if (hex == null) {
      return null;
    }
    // If bit 0 of byte 0 is not set (like in 0x90), then it is a regular address.
    // Else (e.g. 0x91) it represents a namespace id which starts at byte 1.
    byte bit0 = ConvertUtils.getBytes(hex.substring(1, 3))[0];
    if ((bit0 & 16) == 16) {
      // namespaceId encoded hexadecimal notation provided
      // only 8 bytes are relevant to resolve the NamespaceId
      return MapperUtils.toNamespaceId(ConvertUtils.reverseHexString(hex.substring(2, 18)));
    } else {
      return Address.createFromEncoded(hex);
    }
  }

  /**
   * Creates a {@link Address} from the provided raw address.
   *
   * @param rawAddress the rawAddress
   * @return a {@link Address} from the raw address or null if the parameter is null
   */
  public static Address toAddressFromRawAddress(String rawAddress) {
    return rawAddress != null ? Address.createFromRawAddress(rawAddress) : null;
  }

  /**
   * Creates a {@link BigInteger} from the provided hex number.
   *
   * @param hex the hex string with the id
   * @return a {@link BigInteger} from the hex number or null if the hex is null
   */
  public static BigInteger fromHexToBigInteger(String hex) {
    return new BigInteger(hex, 16);
  }

  /**
   * Creates a hex string form {@link BigInteger}. The value must not be negative
   *
   * @param value the not negative {@link BigInteger} to be converted to hex
   * @return the hex of the value.
   */
  public static String fromBigIntegerToHex(BigInteger value) {
    return ConvertUtils.toSize16Hex(value);
  }

  /**
   * Creates a {@link Address} from an encoded address.
   *
   * @param encodedAddress the encodedAddress
   * @return a {@link Address} from the unresolved address or null if the parameter is null
   */
  public static Address toAddress(String encodedAddress) {
    return encodedAddress != null ? Address.createFromEncoded(encodedAddress) : null;
  }

  /**
   * Converts a namespace or a mosaic id to hex
   *
   * @param id the id. It may be null.
   * @return the hex or null if the parameter is null
   */
  public static String getIdAsHex(UnresolvedMosaicId id) {
    return id == null ? null : id.getIdAsHex();
  }

  /**
   * Returns an unresolved address from a plain string like C0FB8AA409916260 (alias hex) or
   * TAHNZXQBC57AA7KJTMGS3PJPZBXN7DV5JHJU42A (address)
   *
   * @param plainUnresolvedAddress plain address or alias hex
   * @return an {@link UnresolvedAddress}
   */
  public static UnresolvedAddress toUnresolvedAddressFromPlain(String plainUnresolvedAddress) {
    try {
      if (Address.isValidPlainAddress(plainUnresolvedAddress)) {
        return Address.createFromRawAddress(plainUnresolvedAddress);
      } else {
        return new NamespaceId(plainUnresolvedAddress);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "'" + plainUnresolvedAddress + "' is not a valid plain address or namespace hex");
    }
  }
}
