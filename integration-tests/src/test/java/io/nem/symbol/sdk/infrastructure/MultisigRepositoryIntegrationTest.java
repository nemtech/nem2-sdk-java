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

package io.nem.symbol.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.account.PublicAccount;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.internal.util.collections.Sets;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//TODO BROKEN!
public class MultisigRepositoryIntegrationTest extends BaseIntegrationTest {

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMultisigAccountInfo(RepositoryType type) {
        MultisigAccountInfo multisigAccountInfo = get(getRepositoryFactory(type)
            .createMultisigRepository()
            .getMultisigAccountInfo(
                config().getMultisigAccount().getAddress())
        );

        Set<Address> cosignatoriesSet = multisigAccountInfo.getCosignatories().stream()
            .map(PublicAccount::getAddress).collect(
                Collectors.toSet());

        Assertions.assertEquals(Sets.newSet(config().getCosignatoryAccount().getAddress(),
            config().getCosignatory2Account().getAddress()), cosignatoriesSet);

        Assertions.assertTrue(multisigAccountInfo.isMultisig());

        assertEquals(
            config().getMultisigAccount().getPublicKey(),
            multisigAccountInfo.getAccount().getPublicKey().toHex());

        Assertions.assertEquals(1,
            multisigAccountInfo.getMinApproval());
        Assertions.assertEquals(1,
            multisigAccountInfo.getMinRemoval());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMultisigAccountGraphInfo(RepositoryType type) {
        MultisigAccountGraphInfo multisigAccountGraphInfos = get(
            this.getRepositoryFactory(type).createMultisigRepository()
                .getMultisigAccountGraphInfo(
                    config().getMultisigAccount().getAddress())
        );

        assertEquals(2,
            multisigAccountGraphInfos.getLevelsNumber().size());

        assertEquals(2,
            multisigAccountGraphInfos.getMultisigAccounts().size());

        assertEquals(1,
            multisigAccountGraphInfos.getMultisigAccounts().get(0).size());

        assertEquals(1,
            multisigAccountGraphInfos.getMultisigAccounts().get(0).size());

        assertEquals(2,
            multisigAccountGraphInfos.getMultisigAccounts().get(1).size());

        assertEquals(config().getMultisigAccount().getAddress(),
            multisigAccountGraphInfos.getMultisigAccounts().get(0).get(0).getAccount()
                .getAddress());

    }

}
