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
package io.nem.symbol.sdk.infrastructure.vertx;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.openapi.vertx.model.MultisigAccountGraphInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MultisigAccountInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MultisigDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link MultisigRepositoryVertxImplTest}
 *
 * @author Fernando Boucquez
 */
public class MultisigRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

  private MultisigRepositoryVertxImpl repository;
  private final Account account = Account.generateNewAccount(networkType);
  private final Account account1 = Account.generateNewAccount(networkType);
  private final Account account2 = Account.generateNewAccount(networkType);
  private final Account account3 = Account.generateNewAccount(networkType);

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new MultisigRepositoryVertxImpl(apiClientMock, networkTypeObservable);
  }

  @Test
  void getMultisigAccountInfo() throws Exception {

    MultisigAccountInfoDTO dto = createMultisigAccountInfoDTO();

    mockRemoteCall(dto);

    MultisigAccountInfo multisigAccountInfo =
        repository.getMultisigAccountInfo(account.getAddress()).toFuture().get();

    assertMultisignAccountInfo(multisigAccountInfo);
  }

  private void assertMultisignAccountInfo(MultisigAccountInfo multisigAccountInfo) {
    Assertions.assertEquals(
        Arrays.asList(account1.getAddress(), account2.getAddress()),
        multisigAccountInfo.getCosignatoryAddresses());

    Assertions.assertEquals(
        Arrays.asList(account2.getAddress(), account3.getAddress()),
        multisigAccountInfo.getMultisigAddresses());

    Assertions.assertEquals(1, multisigAccountInfo.getMinApproval());

    Assertions.assertEquals(2, multisigAccountInfo.getMinRemoval());

    Assertions.assertEquals(account.getAddress(), multisigAccountInfo.getAccountAddress());
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

    MultisigAccountGraphInfo multisigAccountInfo =
        repository.getMultisigAccountGraphInfo(account.getAddress()).toFuture().get();

    Assertions.assertEquals(1, multisigAccountInfo.getMultisigEntries().size());
    List<MultisigAccountInfo> multisigAccountInfos =
        multisigAccountInfo.getMultisigEntries().get(10);
    Assertions.assertEquals(1, multisigAccountInfos.size());

    assertMultisignAccountInfo(multisigAccountInfos.get(0));
  }

  private MultisigAccountInfoDTO createMultisigAccountInfoDTO() {
    MultisigAccountInfoDTO dto = new MultisigAccountInfoDTO();

    MultisigDTO multisigDto = new MultisigDTO();
    multisigDto.setAccountAddress(account.getAddress().encoded());
    multisigDto.setMinApproval(1L);
    multisigDto.setMinRemoval(2L);
    multisigDto.setCosignatoryAddresses(
        Arrays.asList(account1.getAddress().encoded(), account2.getAddress().encoded()));
    multisigDto.setMultisigAddresses(
        Arrays.asList(account2.getAddress().encoded(), account3.getAddress().encoded()));
    dto.setMultisig(multisigDto);
    return dto;
  }
}
