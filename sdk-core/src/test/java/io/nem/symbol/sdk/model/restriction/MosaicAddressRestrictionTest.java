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

import io.nem.symbol.catapult.builders.MosaicAddressRestrictionEntryBuilder;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
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
            "BBBB",
            MosaicRestrictionEntryType.GLOBAL,
            mosaicId,
            account.getAddress(),
            restrictions);

    byte[] serializedState = restriction.serialize();
    String expectedHex =
        "9FA9BCEE6D3B6E5890FD35818960C7B18B72F49A5598FA9F712A354DB38EB076020A00000000000000010000000000000014000000000000000200000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    MosaicAddressRestrictionEntryBuilder builder =
        MosaicAddressRestrictionEntryBuilder.loadFromBinary(
            SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }
}
