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
package io.nem.symbol.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.catapult.builders.MosaicDefinitionBuilder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MosaicInfoTest {

  @Test
  void createAMosaicInfoViaConstructor() {
    MosaicFlags mosaicFlags = MosaicFlags.create(true, true, true);
    MosaicId mosaicId = new MosaicId(new BigInteger("-3087871471161192663"));

    Address address = Address.createFromRawAddress("SDY3NFHBQAPO7ZBII3USHG2UZHJYD7G7FICKIII");
    MosaicInfo info =
        new MosaicInfo(
            "abc",
            mosaicId,
            new BigInteger("100"),
            new BigInteger("0"),
            address,
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    assertEquals(mosaicId, info.getMosaicId());
    assertEquals(new BigInteger("100"), info.getSupply());
    assertEquals(new BigInteger("0"), info.getStartHeight());
    assertEquals(address, info.getOwnerAddress());
    assertTrue(info.isSupplyMutable());
    assertTrue(info.isTransferable());
    assertTrue(info.isTransferable());
    assertEquals(1L, info.getRevision());
    assertEquals(3, info.getDivisibility());
    assertEquals(BigInteger.valueOf(10), info.getDuration());
    assertEquals("abc", info.getRecordId().get());

    byte[] serializedState = info.serialize();
    String expectedHex =
        "000000000000000090F1B694E1801EEFE42846E9239B54C9D381FCDF2A04A4210100000007030A00000000000000";
    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(serializedState));
    MosaicDefinitionBuilder builder =
        MosaicDefinitionBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(builder.serialize()));
  }

  @Test
  void toNetworkCurrency() {
    MosaicFlags mosaicFlags = MosaicFlags.create(true, true, true);
    MosaicId mosaicId = new MosaicId(new BigInteger("-3087871471161192663"));

    Address address = Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress();
    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            mosaicId,
            new BigInteger("100"),
            new BigInteger("0"),
            address,
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    Currency currency = mosaicInfo.toCurrency();
    assertEquals(mosaicId, currency.getMosaicId().get());
    assertFalse(currency.getNamespaceId().isPresent());
    assertTrue(currency.isSupplyMutable());
    assertTrue(currency.isTransferable());
    assertEquals(3, currency.getDivisibility());
  }

  @Test
  void shouldReturnIsSupplyMutableWhenIsMutable() {
    MosaicFlags mosaicFlags = MosaicFlags.create(true, true, true);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            new MosaicId(new BigInteger("-3087871471161192663")),
            new BigInteger("100"),
            new BigInteger("0"),
            Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress(),
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    assertTrue(mosaicInfo.isSupplyMutable());
  }

  @Test
  void shouldReturnIsSupplyMutableWhenIsImmutable() {
    MosaicFlags mosaicFlags = MosaicFlags.create(false, true, true);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            new MosaicId(new BigInteger("-3087871471161192663")),
            new BigInteger("100"),
            new BigInteger("0"),
            Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress(),
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    assertFalse(mosaicInfo.isSupplyMutable());
  }

  @Test
  void shouldReturnIsTransferableWhenItsTransferable() {
    MosaicFlags mosaicFlags = MosaicFlags.create(true, true, true);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            new MosaicId(new BigInteger("-3087871471161192663")),
            new BigInteger("100"),
            new BigInteger("0"),
            Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress(),
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    assertTrue(mosaicInfo.isTransferable());
  }

  @Test
  void shouldReturnIsTransferableWhenItsNotTransferable() {
    MosaicFlags mosaicFlags = MosaicFlags.create(true, false, true);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            new MosaicId(new BigInteger("-3087871471161192663")),
            new BigInteger("100"),
            new BigInteger("0"),
            Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress(),
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    assertFalse(mosaicInfo.isTransferable());
  }

  @Test
  void shouldReturnIsRestrictableWhenItsRestrictable() {
    MosaicFlags mosaicFlags = MosaicFlags.create(true, true, true);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            new MosaicId(new BigInteger("-3087871471161192663")),
            new BigInteger("100"),
            new BigInteger("0"),
            Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress(),
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    assertTrue(mosaicInfo.isRestrictable());
  }

  @Test
  void shouldReturnIsRestrictableWhenItsNotRestrictable() {
    MosaicFlags mosaicFlags = MosaicFlags.create(true, true, false);

    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            new MosaicId(new BigInteger("-3087871471161192663")),
            new BigInteger("100"),
            new BigInteger("0"),
            Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress(),
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    assertFalse(mosaicInfo.isRestrictable());
  }
}
