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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AccountOperationRestrictionTransactionTest {

    static Account account;


    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
                NetworkType.MIJIN_TEST);
    }

    @Test
    void create() {
        final List<AccountRestrictionModification<TransactionType>> modifications = new ArrayList<>();
        AccountRestrictionModification<TransactionType> modification = AccountRestrictionModification
            .createForTransactionType(AccountRestrictionModificationAction.ADD,
                TransactionType.SECRET_PROOF);
        modifications.add(modification);
        AccountOperationRestrictionTransaction transaction =
            AccountOperationRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionType.ALLOW_OUTGOING_TRANSACTION_TYPE,
                modifications).deadline(new FakeDeadline()).build();
        Assertions.assertEquals(AccountRestrictionType.ALLOW_OUTGOING_TRANSACTION_TYPE,
            transaction.getRestrictionType());
        Assertions.assertEquals(modifications, transaction.getModifications());
        Assertions.assertEquals(AccountRestrictionModificationAction.ADD,
            modification.getModificationAction());
        Assertions.assertEquals(TransactionType.SECRET_PROOF,
            modification.getValue());
    }

    @Test
    void shouldGenerateBytes() {

        final List<AccountRestrictionModification<TransactionType>> modifications = new ArrayList<>();
        AccountRestrictionModification<TransactionType> modification = AccountRestrictionModification
            .createForTransactionType(AccountRestrictionModificationAction.ADD,
                TransactionType.SECRET_PROOF);
        modifications.add(modification);
        AccountOperationRestrictionTransaction transaction =
            AccountOperationRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                AccountRestrictionType.ALLOW_INCOMING_MOSAIC,
                modifications).signer(account.getPublicAccount()).deadline(new FakeDeadline())
                .build();

        String expected = "7d00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001905043000000000000000001000000000000000201015242";
        Assertions.assertEquals(expected, HexEncoder.getString(transaction.generateBytes()));

        String expectedEmbeddedHash = "2d0000009a49366406aca952b88badf5f1e9be6ce4968141035a60be503273ea65456b24019050430201015242";
        Assertions.assertEquals(expectedEmbeddedHash,
            HexEncoder.getString(transaction.generateEmbeddedBytes()));
    }
}
