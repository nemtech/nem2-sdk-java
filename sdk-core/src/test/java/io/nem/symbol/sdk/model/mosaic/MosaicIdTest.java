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

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class MosaicIdTest {

    String publicKey = "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf";

    @Test
    void createAMosaicIdFromIdViaConstructor() {
        MosaicId mosaicId = new MosaicId(new BigInteger("-8810190493148073404"));
        assertEquals(mosaicId.getId(), new BigInteger("-8810190493148073404"));
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
        byte[] bytes = new byte[]{0x0, 0x0, 0x0, 0x0};
        MosaicNonce nonce = new MosaicNonce(bytes);
        MosaicId mosaicId = MosaicId.createFromNonce(nonce, owner);
        MosaicId mosaicId2 = new MosaicId(new BigInteger("992621222383397347"));
        assertTrue(mosaicId.equals(mosaicId2));
    }

    @Test
    void createAMosaicIdFromNonceAndOwnerTwiceTheSame() {
        PublicAccount owner = PublicAccount.createFromPublicKey(publicKey, NetworkType.MIJIN_TEST);
        byte[] bytes = new byte[]{0x0, 0x0, 0x0, 0x0};
        MosaicNonce nonce = new MosaicNonce(bytes);
        MosaicId mosaicId1 = MosaicId.createFromNonce(nonce, owner);
        MosaicId mosaicId2 = MosaicId.createFromNonce(nonce, owner);
        assertTrue(mosaicId1.equals(mosaicId2));
    }
}
