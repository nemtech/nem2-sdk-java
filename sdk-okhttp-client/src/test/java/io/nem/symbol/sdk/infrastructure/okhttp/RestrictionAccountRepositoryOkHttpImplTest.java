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
package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.AccountRestrictionSearchCriteria;
import io.nem.symbol.sdk.model.account.AccountRestrictions;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionFlags;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountRestrictionDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountRestrictionFlagsEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountRestrictionsDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountRestrictionsInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountRestrictionsPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MerkleStateInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link RestrictionAccountRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class RestrictionAccountRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

  private RestrictionAccountRepositoryOkHttpImpl repository;

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new RestrictionAccountRepositoryOkHttpImpl(apiClientMock);
  }

  @Test
  public void shouldGetAccountRestrictions() throws Exception {
    Address address = Address.generateRandom(networkType);

    AccountRestrictionsDTO dto = new AccountRestrictionsDTO();
    dto.setAddress(address.encoded());
    AccountRestrictionDTO restriction = new AccountRestrictionDTO();
    restriction.setRestrictionFlags(AccountRestrictionFlagsEnum.NUMBER_32770);
    restriction.setValues(Arrays.asList("9636553580561478212"));
    dto.setRestrictions(Collections.singletonList(restriction));

    AccountRestrictionsInfoDTO info = new AccountRestrictionsInfoDTO();
    info.setAccountRestrictions(dto);
    mockRemoteCall(info);

    AccountRestrictions accountRestrictions =
        repository.getAccountRestrictions(address).toFuture().get();

    Assertions.assertEquals(address, accountRestrictions.getAddress());
    Assertions.assertEquals(1, accountRestrictions.getRestrictions().size());
    Assertions.assertEquals(
        AccountMosaicRestrictionFlags.BLOCK_MOSAIC,
        accountRestrictions.getRestrictions().get(0).getRestrictionFlags());
    Assertions.assertEquals(
        Arrays.asList(MapperUtils.toMosaicId("9636553580561478212")),
        accountRestrictions.getRestrictions().get(0).getValues());
  }

  @Test
  public void getAccountRestrictionsMerkle() throws Exception {
    Address address = Address.generateRandom(this.networkType);
    mockRemoteCall(new MerkleStateInfoDTO().raw("abc"));
    MerkleStateInfo merkle = repository.getAccountRestrictionsMerkle(address).toFuture().get();
    Assertions.assertEquals("abc", merkle.getRaw());
  }

  @Test
  public void search() throws Exception {
    Address address = Address.generateRandom(this.networkType);

    AccountRestrictionsDTO dto = new AccountRestrictionsDTO();
    dto.setAddress(address.encoded());
    AccountRestrictionDTO restriction = new AccountRestrictionDTO();
    restriction.setRestrictionFlags(AccountRestrictionFlagsEnum.NUMBER_32770);
    restriction.setValues(Collections.singletonList("9636553580561478212"));
    dto.setRestrictions(Collections.singletonList(restriction));

    AccountRestrictionsInfoDTO info = new AccountRestrictionsInfoDTO();
    info.setAccountRestrictions(dto);
    mockRemoteCall(toPage(info));

    AccountRestrictions accountRestrictions =
        repository.search(new AccountRestrictionSearchCriteria()).toFuture().get().getData().get(0);

    Assertions.assertEquals(address, accountRestrictions.getAddress());
    Assertions.assertEquals(1, accountRestrictions.getRestrictions().size());
    Assertions.assertEquals(
        AccountMosaicRestrictionFlags.BLOCK_MOSAIC,
        accountRestrictions.getRestrictions().get(0).getRestrictionFlags());
    Assertions.assertEquals(
        Collections.singletonList(MapperUtils.toMosaicId("9636553580561478212")),
        accountRestrictions.getRestrictions().get(0).getValues());
  }

  private AccountRestrictionsPage toPage(AccountRestrictionsInfoDTO dto) {
    return new AccountRestrictionsPage()
        .data(Collections.singletonList(dto))
        .pagination(new Pagination().pageNumber(1).pageSize(2));
  }

  @Override
  public RestrictionAccountRepositoryOkHttpImpl getRepository() {
    return repository;
  }
}
