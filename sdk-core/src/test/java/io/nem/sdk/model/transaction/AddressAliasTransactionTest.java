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

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class AddressAliasTransactionTest extends AbstractTransactionTester {

    @Test
    void shouldSerialize() {

        NetworkType networkType = NetworkType.MIJIN_TEST;
        BigInteger fee = BigInteger.ONE;
        NamespaceId namespaceId = NamespaceId.createFromId(new BigInteger("-8884663987180930485"));
        PublicAccount signature = PublicAccount.createFromPublicKey(
            "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763",
            NetworkType.MIJIN_TEST);
        TransactionInfo transactionInfo =
            TransactionInfo.createAggregate(
                new BigInteger("121855"),
                1,
                "5A3D23889CD1E800015929A9",
                "3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006",
                "5A0069D83F17CF0001777E55");

        AddressAliasTransaction transaction = AddressAliasTransactionFactory.create(networkType,
            AliasAction.LINK, namespaceId, signature.getAddress()).signer(signature)
            .transactionInfo(transactionInfo).signature("signing").deadline(new FakeDeadline())
            .maxFee(fee).build();

        String expectedHash = "9a00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904e4201000000000000000100000000000000014bfa5f372d55b3849049e14bebca93758eb36805bae760a57239976f009a545cad";
        assertSerialization(expectedHash, transaction);

        String expectedEmbeddedHash = "4a00000068b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b76301904e42014bfa5f372d55b3849049e14bebca93758eb36805bae760a57239976f009a545cad";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
