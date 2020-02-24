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

package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.blockchain.NetworkType;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountMosaicRestrictionTransactionTest extends AbstractTransactionTester {

    static Account account =
        new Account(
            "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
            NetworkType.MIJIN_TEST);

    static UnresolvedMosaicId mosaicId = new MosaicId(BigInteger.valueOf(1000));

    static UnresolvedMosaicId mosaicId2 = new MosaicId(BigInteger.valueOf(2000));


    @Test
    void create() {
        List<UnresolvedMosaicId> additions = Collections.singletonList(mosaicId);
        List<UnresolvedMosaicId> deletions = Collections.singletonList(mosaicId2);

        AccountMosaicRestrictionTransaction transaction =
            AccountMosaicRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionFlags.ALLOW_INCOMING_MOSAIC,
                additions, deletions).deadline(new FakeDeadline()).build();
        Assertions.assertEquals(AccountRestrictionFlags.ALLOW_INCOMING_MOSAIC,
            transaction.getRestrictionFlags());
        Assertions.assertEquals(additions, transaction.getRestrictionAdditions());
        Assertions.assertEquals(deletions, transaction.getRestrictionDeletions());
    }

    @Test
    void shouldGenerateBytes() {

        List<UnresolvedMosaicId> additions = Collections.singletonList(mosaicId);
        List<UnresolvedMosaicId> deletions = Collections.singletonList(mosaicId2);
        AccountMosaicRestrictionTransaction transaction =
            AccountMosaicRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionFlags.ALLOW_INCOMING_MOSAIC,
                additions, deletions).deadline(new FakeDeadline())
                .signer(account.getPublicAccount())
                .build();

        String expected = "980000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e60000000001905042000000000000000001000000000000000200010100000000e803000000000000d007000000000000";
        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "4800000000000000f6503f78fbf99544b906872ddb392f4be707180d285e7919dbacef2e9573b1e600000000019050420200010100000000e803000000000000d007000000000000";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
