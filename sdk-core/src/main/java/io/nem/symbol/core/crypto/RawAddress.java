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
package io.nem.symbol.core.crypto;

import io.nem.symbol.core.utils.ArrayUtils;
import io.nem.symbol.core.utils.Base32Encoder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.Arrays;

/** Utility class that knows how to create an address based on a public key. */
public class RawAddress {

  public static final int NUM_CHECKSUM_BYTES = 3;

  /** Private utility class constructor. */
  private RawAddress() {
    // private utility class constructor.
  }

  /**
   * This method generates an address based on the public key and the Symbol configuration network
   * type.
   *
   * @param publicKey the public key
   * @param networkType the network type
   * @return an encoded address that can be used to identify accounts.
   */
  public static String generateAddress(final String publicKey, final NetworkType networkType) {

    byte networkTypeValue = (byte) networkType.getValue();
    // step 1: sha3 hash of the public key
    byte[] publicKeyBytes;
    try {
      publicKeyBytes = ConvertUtils.fromHexToBytes(publicKey);
    } catch (Exception e) {
      throw new IllegalArgumentException("Public key is not valid");
    }
    final byte[] publicKeyHash = Hashes.sha3_256(publicKeyBytes);

    // step 2: ripemd160 hash of (1)
    final byte[] ripemd160StepOneHash = Hashes.ripemd160(publicKeyHash);

    // step 3: add network type byte in front of (2)
    final byte[] versionPrefixedRipemd160Hash =
        ArrayUtils.concat(new byte[] {networkTypeValue}, ripemd160StepOneHash);

    // step 4: get the checksum of (3)
    final byte[] stepThreeChecksum = generateChecksum(versionPrefixedRipemd160Hash);

    // step 5: concatenate (3) and (4)
    final byte[] concatStepThreeAndStepSix =
        ArrayUtils.concat(versionPrefixedRipemd160Hash, stepThreeChecksum);

    // step 6: base32 encode (5)
    String base32 = Base32Encoder.getString(concatStepThreeAndStepSix);
    return base32.substring(0, base32.length() - 1);
  }

  private static byte[] generateChecksum(final byte[] input) {
    // step 1: sha3 hash of (input
    final byte[] stepThreeHash = Hashes.sha3_256(input);

    // step 2: get the first X bytes of (1)
    return Arrays.copyOfRange(stepThreeHash, 0, NUM_CHECKSUM_BYTES);
  }
}
