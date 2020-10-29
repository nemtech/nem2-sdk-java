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

import io.nem.symbol.catapult.builders.MosaicGlobalRestrictionEntryBuilder;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MosaicGlobalRestrictionTest {

  private final PublicKey accountPublicKey =
      PublicKey.fromHexString("1111111111111111111111111111111111111111111111111111111111111111");
  private final NetworkType networkType = NetworkType.MIJIN_TEST;
  private final PublicAccount account =
      PublicAccount.createFromPublicKey(accountPublicKey.toHex(), networkType);
  private final MosaicId mosaicId1 = MosaicId.createFromNonce(new MosaicNonce(1), account);
  private final BigInteger restrictionKey = BigInteger.TEN;

  @Test
  public void serialize() {
    Map<BigInteger, MosaicGlobalRestrictionItem> map =
        Collections.singletonMap(
            restrictionKey,
            new MosaicGlobalRestrictionItem(
                mosaicId1, BigInteger.valueOf(20), MosaicRestrictionType.EQ));
    MosaicGlobalRestriction restriction =
        new MosaicGlobalRestriction("AAAA", MosaicRestrictionEntryType.GLOBAL, mosaicId1, map);

    byte[] serializedState = restriction.serialize();
    String expectedHex = "9FA9BCEE6D3B6E58010A000000000000009FA9BCEE6D3B6E58140000000000000001";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    MosaicGlobalRestrictionEntryBuilder builder =
        MosaicGlobalRestrictionEntryBuilder.loadFromBinary(
            SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }
}
