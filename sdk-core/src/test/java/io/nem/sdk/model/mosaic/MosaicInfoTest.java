/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class MosaicInfoTest {
    static SignSchema signSchema = SignSchema.DEFAULT;


    @Test
    void createAMosaicInfoViaConstructor() {
        MosaicProperties mosaicProperties =
            MosaicProperties.create(true, true, 3, BigInteger.valueOf(10));
        MosaicId mosaicId = new MosaicId(new BigInteger("-3087871471161192663"));

        MosaicInfo mosaicInfo =
            MosaicInfo.create(
                mosaicId,
                new BigInteger("100"),
                new BigInteger("0"),
                new PublicAccount(
                    "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF",
                    NetworkType.MIJIN_TEST, signSchema),
                1,
                mosaicProperties);

        assertEquals(mosaicId, mosaicInfo.getMosaicId());
        assertEquals(new BigInteger("100"), mosaicInfo.getSupply());
        assertEquals(new BigInteger("0"), mosaicInfo.getHeight());
        assertEquals(
            new PublicAccount(
                "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF",
                NetworkType.MIJIN_TEST, signSchema),
            mosaicInfo.getOwner());
        assertTrue(mosaicInfo.isSupplyMutable());
        assertTrue(mosaicInfo.isTransferable());
        assertEquals(3, mosaicInfo.getDivisibility());
        assertEquals(BigInteger.valueOf(10).intValue(), mosaicInfo.getDuration().longValue());
    }

    @Test
    void shouldReturnIsSupplyMutableWhenIsMutable() {
        MosaicProperties mosaicProperties =
            MosaicProperties.create(true, true, 3, BigInteger.valueOf(10));

        MosaicInfo mosaicInfo =
            MosaicInfo.create(
                new MosaicId(new BigInteger("-3087871471161192663")),
                new BigInteger("100"),
                new BigInteger("0"),
                new PublicAccount(
                    "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF",
                    NetworkType.MIJIN_TEST, signSchema),
                1,
                mosaicProperties);

        assertTrue(mosaicInfo.isSupplyMutable());
    }

    @Test
    void shouldReturnIsSupplyMutableWhenIsImmutable() {
        MosaicProperties mosaicProperties =
            MosaicProperties.create(false, true, 3, BigInteger.valueOf(10));

        MosaicInfo mosaicInfo =
            MosaicInfo.create(
                new MosaicId(new BigInteger("-3087871471161192663")),
                new BigInteger("100"),
                new BigInteger("0"),
                new PublicAccount(
                    "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF",
                    NetworkType.MIJIN_TEST, signSchema),
                1,
                mosaicProperties);

        assertFalse(mosaicInfo.isSupplyMutable());
    }

    @Test
    void shouldReturnIsTransferableWhenItsTransferable() {
        MosaicProperties mosaicProperties =
            MosaicProperties.create(true, true, 3, BigInteger.valueOf(10));

        MosaicInfo mosaicInfo =
            MosaicInfo.create(
                new MosaicId(new BigInteger("-3087871471161192663")),
                new BigInteger("100"),
                new BigInteger("0"),
                new PublicAccount(
                    "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF",
                    NetworkType.MIJIN_TEST, signSchema),
                1,
                mosaicProperties);

        assertTrue(mosaicInfo.isTransferable());
    }

    @Test
    void shouldReturnIsTransferableWhenItsNotTransferable() {
        MosaicProperties mosaicProperties =
            MosaicProperties.create(true, false, 3, BigInteger.valueOf(10));

        MosaicInfo mosaicInfo =
            MosaicInfo.create(
                new MosaicId(new BigInteger("-3087871471161192663")),
                new BigInteger("100"),
                new BigInteger("0"),
                new PublicAccount(
                    "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF",
                    NetworkType.MIJIN_TEST, signSchema),
                1,
                mosaicProperties);

        assertFalse(mosaicInfo.isTransferable());
    }
}
