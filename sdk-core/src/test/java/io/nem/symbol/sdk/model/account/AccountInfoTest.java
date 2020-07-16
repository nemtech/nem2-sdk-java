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

package io.nem.symbol.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.ResolvedMosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AccountInfoTest {

    @Test
    void shouldCreateAccountInfoViaConstructor() {
        List<ResolvedMosaic> mosaics = Collections
            .singletonList(new ResolvedMosaic(new MosaicId(BigInteger.TEN), BigInteger.valueOf(10000)));

        SupplementalAccountKeys supplementalAccountKeys = new SupplementalAccountKeys(Optional.of("linkedKey"),
            Optional.of("nodeKey"), Optional.of("vrfKey"), Collections
            .singletonList(new AccountLinkVotingKey("votingKey", BigInteger.valueOf(10), BigInteger.valueOf(20))));

        BigInteger startHeight = BigInteger.ONE;
        BigInteger totalFeesPaid = BigInteger.valueOf(2);
        int beneficiaryCount = 3;
        BigInteger rawScore = BigInteger.valueOf(4);
        ActivityBucket bucket = new ActivityBucket(startHeight, totalFeesPaid, beneficiaryCount, rawScore);
        List<ActivityBucket> activityBuckets = Collections.singletonList(bucket);
        Address address = Address.generateRandom(NetworkType.MIJIN_TEST);
        String publicKey = PublicKey.generateRandom().toHex();
        AccountInfo accountInfo = new AccountInfo("abc", address, new BigInteger("964"), publicKey,
            new BigInteger("966"), new BigInteger("777"), new BigInteger("0"), mosaics, AccountType.REMOTE_UNLINKED,
            supplementalAccountKeys, activityBuckets);

        assertEquals("abc", accountInfo.getRecordId().get());
        assertEquals(address, accountInfo.getAddress());
        assertEquals(new BigInteger("964"), accountInfo.getAddressHeight());
        assertEquals(publicKey, accountInfo.getPublicKey());
        assertEquals(new BigInteger("966"), accountInfo.getPublicKeyHeight());
        assertEquals(new BigInteger("777"), accountInfo.getImportances().get(0).getValue());
        assertEquals(new BigInteger("0"), accountInfo.getImportances().get(0).getHeight());
        assertEquals(mosaics, accountInfo.getMosaics());
        assertEquals(PublicAccount.createFromPublicKey(publicKey, NetworkType.MIJIN_TEST),
            accountInfo.getPublicAccount());

        assertEquals(AccountType.REMOTE_UNLINKED, accountInfo.getAccountType());

        assertEquals(supplementalAccountKeys, accountInfo.getSupplementalAccountKeys());
        assertEquals(activityBuckets, accountInfo.getActivityBuckets());

        assertEquals("linkedKey", supplementalAccountKeys.getLinked().get());
        assertEquals("nodeKey", supplementalAccountKeys.getNode().get());
        assertEquals("vrfKey", supplementalAccountKeys.getVrf().get());
        assertEquals("votingKey", supplementalAccountKeys.getVoting().get(0).getPublicKey());
        assertEquals(BigInteger.valueOf(10), supplementalAccountKeys.getVoting().get(0).getStartPoint());
        assertEquals(BigInteger.valueOf(20), supplementalAccountKeys.getVoting().get(0).getEndPoint());
    }
}
