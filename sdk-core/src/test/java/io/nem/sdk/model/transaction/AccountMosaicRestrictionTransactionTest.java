/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.model.transaction;

import io.nem.core.utils.HexEncoder;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AccountMosaicRestrictionTransactionTest {

    static Account account;

    static MosaicId mosaicId;

    @BeforeAll
    public static void setup() {
        mosaicId = new MosaicId(BigInteger.valueOf(1000));
        account =
            new Account(
                "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
                NetworkType.MIJIN_TEST);
    }

    @Test
    void create() {
        final List<AccountRestrictionModification<MosaicId>> modifications = new ArrayList<>();
        AccountRestrictionModification<MosaicId> modification = AccountRestrictionModification
            .createForMosaic(AccountRestrictionModificationAction.ADD, mosaicId);
        modifications.add(modification);
        AccountMosaicRestrictionTransaction transaction =
            AccountMosaicRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionType.ALLOW_INCOMING_MOSAIC,
                modifications).deadline(new FakeDeadline()).build();
        Assertions.assertEquals(AccountRestrictionType.ALLOW_INCOMING_MOSAIC, transaction.getRestrictionType());
        Assertions.assertEquals(modifications, transaction.getModifications());
        Assertions.assertEquals(AccountRestrictionModificationAction.ADD,
            modification.getModificationAction());
        Assertions.assertEquals(mosaicId,
            modification.getValue());
    }

    @Test
    void shouldGenerateBytes() {

        final List<AccountRestrictionModification<MosaicId>> modifications = new ArrayList<>();
        AccountRestrictionModification<MosaicId> modification = AccountRestrictionModification
            .createForMosaic(AccountRestrictionModificationAction.ADD, mosaicId);
        modifications.add(modification);
        AccountMosaicRestrictionTransaction transaction =
            AccountMosaicRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionType.ALLOW_INCOMING_MOSAIC,
                modifications).deadline(new FakeDeadline()).signer(account.getPublicAccount())
                .build();

        String expected = "830000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000190504200000000000000000100000000000000020101e803000000000000";
        Assertions.assertEquals(expected, HexEncoder.getString(transaction.generateBytes()));

        String expectedEmbeddedHash = "330000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b2401905042020101e803000000000000";
        Assertions.assertEquals(expectedEmbeddedHash,
            HexEncoder.getString(transaction.generateEmbeddedBytes()));
    }
}
