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

package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MosaicAddressRestrictionTransactionTest {

    static Account account;
    static String generationHash;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930",
                NetworkType.MIJIN_TEST);
        generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
    }

    @Test
    void createAMosaicAddressRestrictionTransactionViaStaticConstructor() {
        MosaicAddressRestrictionTransaction mosaicAddressRestrictionTx =
            MosaicAddressRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("0")), // restricted MosaicId
                BigInteger.valueOf(1), // restrictionKey
                account.getAddress(),  // targetAddress
                BigInteger.valueOf(9), // previousRestrictionValue
                BigInteger.valueOf(8)  // newRestrictionValue
            ).build();

        assertEquals(NetworkType.MIJIN_TEST, mosaicAddressRestrictionTx.getNetworkType());
        assertTrue(1 == mosaicAddressRestrictionTx.getVersion());
        assertTrue(LocalDateTime.now().isBefore(mosaicAddressRestrictionTx.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), mosaicAddressRestrictionTx.getMaxFee());
        assertEquals(new BigInteger("0"), mosaicAddressRestrictionTx.getMosaicId().getId());
        assertEquals(BigInteger.valueOf(1), mosaicAddressRestrictionTx.getRestrictionKey());
        assertEquals(account.getAddress(), mosaicAddressRestrictionTx.getTargetAddress());
        assertEquals(BigInteger.valueOf(9), mosaicAddressRestrictionTx.getPreviousRestrictionValue());
        assertEquals(BigInteger.valueOf(8), mosaicAddressRestrictionTx.getNewRestrictionValue());
    }

    @Test
    void serializeAndSignTransaction() {
        MosaicAddressRestrictionTransaction mosaicAddressRestrictionTx =
            MosaicAddressRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("1")), // restricted MosaicId
                BigInteger.valueOf(1), // restrictionKey
                account.getAddress(),  // targetAddress
                BigInteger.valueOf(9), // previousRestrictionValue
                BigInteger.valueOf(8)  // newRestrictionValue
            ).build();

        SignedTransaction signedTransaction = mosaicAddressRestrictionTx.signWith(account, generationHash);

        assertEquals("0100000000000000010000000000000090A75B6B63D31BDA93808727940F24699AE" +
            "CDDF17C568508BA09000000000000000800000000000000", signedTransaction.getPayload().substring(240));
    }
}
