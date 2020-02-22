/*
 * Copyright 2019 NEM
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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MultisigAccountGraphInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MultisigAccountInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MultisigDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link MultisigRepositoryOkHttpImplTest}
 *
 * @author Fernando Boucquez
 */
public class MultisigRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private MultisigRepositoryOkHttpImpl repository;
    private Account account = Account.generateNewAccount(networkType);
    private Account account1 = Account.generateNewAccount(networkType);
    private Account account2 = Account.generateNewAccount(networkType);
    private Account account3 = Account.generateNewAccount(networkType);

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new MultisigRepositoryOkHttpImpl(apiClientMock, networkTypeObservable);
    }

    @Test
    void getMultisigAccountInfo() throws Exception {

        MultisigAccountInfoDTO dto = createMultisigAccountInfoDTO();

        mockRemoteCall(dto);

        MultisigAccountInfo multisigAccountInfo = repository
            .getMultisigAccountInfo(account.getAddress())
            .toFuture().get();

        assertMultisignAccountInfo(multisigAccountInfo);

    }

    private void assertMultisignAccountInfo(MultisigAccountInfo multisigAccountInfo) {
        Assertions
            .assertEquals(Arrays.asList(account1.getPublicAccount(), account2.getPublicAccount()),
                multisigAccountInfo.getCosignatories());

        Assertions
            .assertEquals(Arrays.asList(account2.getPublicAccount(), account3.getPublicAccount()),
                multisigAccountInfo.getMultisigAccounts());

        Assertions
            .assertEquals(1,
                multisigAccountInfo.getMinApproval());

        Assertions
            .assertEquals(2,
                multisigAccountInfo.getMinRemoval());

        Assertions
            .assertEquals(account.getPublicAccount(),
                multisigAccountInfo.getAccount());
    }

    @Test
    void getMultisigAccountGraphInfo() throws Exception {

        MultisigAccountGraphInfoDTO dto = new MultisigAccountGraphInfoDTO();
        dto.setLevel(10);
        dto.setMultisigEntries(new ArrayList<>());
        dto.getMultisigEntries().add(createMultisigAccountInfoDTO());

        List<MultisigAccountGraphInfoDTO> dtos = new ArrayList<>();
        dtos.add(dto);

        mockRemoteCall(dtos);

        MultisigAccountGraphInfo multisigAccountInfo = repository
            .getMultisigAccountGraphInfo(account.getAddress())
            .toFuture().get();

        Assertions.assertEquals(1, multisigAccountInfo.getMultisigAccounts().size());
        List<MultisigAccountInfo> multisigAccountInfos = multisigAccountInfo.getMultisigAccounts()
            .get(10);
        Assertions.assertEquals(1, multisigAccountInfos.size());

        assertMultisignAccountInfo(multisigAccountInfos.get(0));


    }


    private MultisigAccountInfoDTO createMultisigAccountInfoDTO() {
        MultisigAccountInfoDTO dto = new MultisigAccountInfoDTO();

        MultisigDTO multisigDto = new MultisigDTO();
        multisigDto.setAccountAddress(account.getAddress().encoded());
        multisigDto.setAccountPublicKey(account.getPublicKey());
        multisigDto.setMinApproval(1);
        multisigDto.setMinRemoval(2);
        multisigDto.setCosignatoryPublicKeys(
            Arrays.asList(account1.getPublicKey(), account2.getPublicKey()));
        multisigDto
            .setMultisigPublicKeys(Arrays.asList(account2.getPublicKey(), account3.getPublicKey()));
        dto.setMultisig(multisigDto);
        return dto;
    }


    @Override
    protected AbstractRepositoryOkHttpImpl getRepository() {
        return repository;
    }

}
