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
package io.nem.symbol.core.crypto.ed25519;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

  public static byte[] getRandomNonce(int numBytes) {
    byte[] nonce = new byte[numBytes];
    new SecureRandom().nextBytes(nonce);
    return nonce;
  }

  // AES secret key
  public static SecretKey getAESKey(int keysize) throws NoSuchAlgorithmException {
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(keysize, SecureRandom.getInstanceStrong());
    return keyGen.generateKey();
  }

  // Password derived AES 256 bits secret key
  public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt)
      throws NoSuchAlgorithmException, InvalidKeySpecException {

    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    // iterationCount = 65536
    // keyLength = 256
    KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    return secret;
  }

  // hex representation
  public static String hex(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (byte b : bytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }

  // print hex with block size split
  public static String hexWithBlockSize(byte[] bytes, int blockSize) {

    String hex = hex(bytes);

    // one hex = 2 chars
    blockSize = blockSize * 2;

    // better idea how to print this?
    List<String> result = new ArrayList<>();
    int index = 0;
    while (index < hex.length()) {
      result.add(hex.substring(index, Math.min(index + blockSize, hex.length())));
      index += blockSize;
    }

    return result.toString();
  }
}
