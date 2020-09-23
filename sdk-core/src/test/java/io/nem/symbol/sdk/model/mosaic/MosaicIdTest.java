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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MosaicIdTest {

  String publicKey = "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf";

  @Test
  void createAMosaicIdFromIdViaConstructor() {
    MosaicId mosaicId = new MosaicId(new BigInteger("-8810190493148073404"));
    assertEquals(mosaicId.getId(), new BigInteger("-8810190493148073404"));
  }

  @Test
  void testCurrencyTestnet() {
    String nemesisSignerPublicKey =
        "871B2F2F9D825252FFF43543066EE8D9141A3373F2F013BB449C6425A03362D8";
    Address nemesisAddress =
        Address.createFromPublicKey(nemesisSignerPublicKey, NetworkType.TEST_NET);
    MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createFromInteger(0), nemesisAddress);
    assertEquals("5E62990DCAC5BE8A", mosaicId.getIdAsHex());
  }

  @Test
  void testCurrency() {
    String nemesisSignerPublicKey =
        "E0AC0720AA389B8DBF9DB9D2672991CDA7DEC284737984702CE7FD7C6E07A5E2";
    Address nemesisAddress =
        Address.createFromPublicKey(nemesisSignerPublicKey, NetworkType.TEST_NET);
    MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createFromInteger(0), nemesisAddress);
    assertEquals("61B0856247BD3A71", mosaicId.getIdAsHex());
  }

  @Test
  void testHarvestCurrency() {
    String nemesisSignerPublicKey =
        "AA4174DBA4C6CABFF16DEDA628ACE549701FD1618BC6CE89E10BEFE33459CD12";
    Address nemesisAddress =
        Address.createFromPublicKey(nemesisSignerPublicKey, NetworkType.TEST_NET);
    MosaicNonce nonce = MosaicNonce.createFromInteger(1);
    Assertions.assertEquals(1, nonce.getNonceAsLong());
    MosaicId mosaicId = MosaicId.createFromNonce(nonce, nemesisAddress);
    assertEquals("1646351CC29EBDCB", mosaicId.getIdAsHex());
  }

  @Test
  void createAMosaicIdFromHexViaConstructor() {
    MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
    assertEquals(mosaicId.getId(), new BigInteger("9636553580561478212"));
  }

  @Test
  void shouldCompareMosaicIdsForEquality() {
    MosaicId mosaicId = new MosaicId(new BigInteger("-8810190493148073404"));
    MosaicId mosaicId2 = new MosaicId(new BigInteger("-8810190493148073404"));
    assertTrue(mosaicId.equals(mosaicId2));
  }

  @Test
  void shouldCompareMosaicIdsHexForEquality() {
    MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
    MosaicId mosaicId2 = new MosaicId(new BigInteger("9636553580561478212"));
    assertEquals(mosaicId.getIdAsHex(), mosaicId2.getIdAsHex());
  }

  @Test
  void shouldCompareMosaicIdsArraysForEquality() {
    MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
    BigInteger bigInt1 = new BigInteger("9636553580561478212");
    MosaicId mosaicId1 = new MosaicId(bigInt1);
    assertEquals(mosaicId.getId(), mosaicId1.getId());
  }

  @Test
  void shouldCompareMosaicIdsForNotEquality() {
    BigInteger bigInt1 = new BigInteger("9636553580561478212");
    MosaicId mosaicId1 = new MosaicId(bigInt1);
    BigInteger bigInt2 = new BigInteger("-8810190493148073404");
    MosaicId mosaicId2 = new MosaicId(bigInt2);
    assertNotEquals(bigInt1, bigInt2);
    assertNotEquals(mosaicId1.getId(), mosaicId2.getId());
  }

  @Test
  void createAMosaicIdFromNonceAndOwner() {
    PublicAccount owner = PublicAccount.createFromPublicKey(publicKey, NetworkType.MIJIN_TEST);
    MosaicNonce nonce = MosaicNonce.createFromInteger(0);
    MosaicId mosaicId = MosaicId.createFromNonce(nonce, owner);
    MosaicId mosaicId2 = new MosaicId(new BigInteger("5331590414131997017"));
    MosaicId mosaicId3 = MosaicId.createFromNonce(nonce, owner.getAddress());
    assertEquals(mosaicId, mosaicId);
    assertEquals(mosaicId, mosaicId2);
    assertEquals(mosaicId, mosaicId3);
    assertEquals(5331590414131997017L, mosaicId2.getIdAsLong());
  }

  @Test
  void hashCodeAndEquals() {
    PublicAccount owner = PublicAccount.createFromPublicKey(publicKey, NetworkType.MIJIN_TEST);
    MosaicNonce nonce = MosaicNonce.createFromInteger(0);
    MosaicId mosaicId = MosaicId.createFromNonce(nonce, owner);
    MosaicId mosaicId2 = new MosaicId(new BigInteger("5331590414131997017"));
    MosaicId mosaicId3 = MosaicId.createFromNonce(nonce, owner.getAddress());
    assertEquals(mosaicId, mosaicId);
    assertNotEquals("", mosaicId);
    assertNotEquals(mosaicId, "");
    assertEquals(mosaicId, mosaicId2);
    assertEquals(mosaicId.hashCode(), mosaicId2.hashCode());
    assertEquals(mosaicId, mosaicId3);
  }

  @Test
  void createAMosaicIdFromNonceAndOwnerTwiceTheSame() {
    PublicAccount owner = PublicAccount.createFromPublicKey(publicKey, NetworkType.MIJIN_TEST);
    MosaicNonce nonce = MosaicNonce.createFromInteger(0);
    MosaicId mosaicId1 = MosaicId.createFromNonce(nonce, owner);
    MosaicId mosaicId2 = MosaicId.createFromNonce(nonce, owner);
    assertEquals(mosaicId1, mosaicId2);
    assertEquals(mosaicId1.hashCode(), mosaicId2.hashCode());
  }
}
