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
package io.nem.symbol.sdk.model.restriction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.catapult.builders.MosaicRestrictionEntryBuilder;
import io.nem.symbol.core.crypto.Hashes;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MosaicAddressRestrictionTest {

  private final PublicKey accountPublicKey =
      PublicKey.fromHexString("1111111111111111111111111111111111111111111111111111111111111111");
  private final NetworkType networkType = NetworkType.MIJIN_TEST;
  private final PublicAccount account =
      PublicAccount.createFromPublicKey(accountPublicKey.toHex(), networkType);
  private final MosaicId mosaicId = MosaicId.createFromNonce(new MosaicNonce(1), account);

  @Test
  public void serialize() {

    Map<BigInteger, BigInteger> restrictions = new LinkedHashMap<>();
    restrictions.put(BigInteger.valueOf(10), BigInteger.valueOf(1));
    restrictions.put(BigInteger.valueOf(20), BigInteger.valueOf(2));
    MosaicAddressRestriction restriction =
        new MosaicAddressRestriction(
            "a",
            1,
            "BBBB",
            MosaicRestrictionEntryType.GLOBAL,
            mosaicId,
            account.getAddress(),
            restrictions);

    byte[] serializedState = restriction.serialize();
    String expectedHex =
        "0100009FA9BCEE6D3B6E5890FD35818960C7B18B72F49A5598FA9F712A354DB38EB076020A00000000000000010000000000000014000000000000000200000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    MosaicRestrictionEntryBuilder builder =
        MosaicRestrictionEntryBuilder.loadFromBinary(
            SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }

  @Test
  public void serializeWithWrongOrder() {

    Map<BigInteger, BigInteger> restrictions = new LinkedHashMap<>();
    restrictions.put(BigInteger.valueOf(75425), BigInteger.valueOf(1));
    restrictions.put(BigInteger.valueOf(60947), BigInteger.valueOf(1));
    MosaicAddressRestriction restriction =
        new MosaicAddressRestriction(
            "a",
            1,
            "DEB23060603C93293673A3C50B46351F2C746A821CA815A66C0396363EE7F228",
            MosaicRestrictionEntryType.ADDRESS,
            new MosaicId("4C8206CB11492AD5"),
            Address.createFromEncoded("98D2C95780CE2BE891597AE8984CE4977FB54FAC9913CEA1"),
            restrictions);

    byte[] serializedState = restriction.serialize();
    byte[] hash = Hashes.sha3_256(serializedState);
    String expectedHashHex = "11F67D85CDF3A8DF1DC74021F6029C9E625E33B2F2F14D166D5FC3EE8E595390";
    String expectedHex =
        "010000D52A4911CB06824C98D2C95780CE2BE891597AE8984CE4977FB54FAC9913CEA10213EE0000000000000100000000000000A1260100000000000100000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));
    assertEquals(expectedHashHex, ConvertUtils.toHex(hash));
    MosaicRestrictionEntryBuilder builder =
        MosaicRestrictionEntryBuilder.loadFromBinary(
            SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }
}
