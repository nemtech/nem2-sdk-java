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
import io.nem.symbol.sdk.api.NamespaceSearchCriteria;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountNames;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.MerkleStateInfo;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNames;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountsNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AliasDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AliasTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MerkleStateInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.MosaicsNamesDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceMetaDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceNameDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespacePage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.NamespaceRegistrationTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link NamespaceRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class NamespaceRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

  private NamespaceRepositoryOkHttpImpl repository;

  @BeforeEach
  public void setUp() {
    super.setUp();
    repository = new NamespaceRepositoryOkHttpImpl(apiClientMock);
  }

  @Test
  public void shouldGetNamespace() throws Exception {

    Address ownerAccount = Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress();

    NamespaceId namespaceId = NamespaceId.createFromName("accountalias");

    NamespaceInfoDTO dto = new NamespaceInfoDTO();
    NamespaceMetaDTO meta = new NamespaceMetaDTO();
    meta.setActive(true);
    dto.setId("SomeId");
    meta.setIndex(123);
    dto.setMeta(meta);

    NamespaceDTO namespace = new NamespaceDTO();
    namespace.setDepth(111);
    namespace.setStartHeight(BigInteger.valueOf(4));
    namespace.setEndHeight(BigInteger.valueOf(5));
    namespace.setRegistrationType(NamespaceRegistrationTypeEnum.NUMBER_1);
    namespace.setOwnerAddress(ownerAccount.encoded());
    namespace.setVersion(1);

    AliasDTO alias = new AliasDTO();
    alias.setType(AliasTypeEnum.NUMBER_1);
    alias.setMosaicId("123");
    namespace.setAlias(alias);

    dto.setNamespace(namespace);

    mockRemoteCall(dto);

    NamespaceInfo info = repository.getNamespace(namespaceId).toFuture().get();

    Assertions.assertNotNull(info);

    Assertions.assertEquals(NamespaceRegistrationType.SUB_NAMESPACE, info.getRegistrationType());

    Assertions.assertEquals(dto.getId(), info.getRecordId().get());
    Assertions.assertEquals(meta.getIndex(), info.getIndex());
    Assertions.assertEquals(meta.getActive(), info.isActive());

    Assertions.assertEquals(BigInteger.valueOf(4), info.getStartHeight());
    Assertions.assertEquals(BigInteger.valueOf(5), info.getEndHeight());
  }

  @Test
  public void search() throws Exception {
    Address address = Address.generateRandom(networkType);
    Address ownerAccount = Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress();

    NamespaceInfoDTO dto = new NamespaceInfoDTO();
    NamespaceMetaDTO meta = new NamespaceMetaDTO();
    meta.setActive(true);
    dto.setId("SomeId");
    meta.setIndex(123);
    dto.setMeta(meta);

    NamespaceDTO namespace = new NamespaceDTO();
    namespace.setDepth(111);
    namespace.setStartHeight(BigInteger.valueOf(4));
    namespace.setEndHeight(BigInteger.valueOf(5));
    namespace.setRegistrationType(NamespaceRegistrationTypeEnum.NUMBER_1);
    namespace.setOwnerAddress(ownerAccount.encoded());
    namespace.setVersion(1);

    AliasDTO alias = new AliasDTO();
    alias.setType(AliasTypeEnum.NUMBER_2);
    alias.setAddress(address.encoded());
    namespace.setAlias(alias);

    dto.setNamespace(namespace);

    mockRemoteCall(toPage(dto));

    NamespaceInfo info =
        repository
            .search(new NamespaceSearchCriteria().ownerAddress(address))
            .toFuture()
            .get()
            .getData()
            .get(0);

    Assertions.assertNotNull(info);

    Assertions.assertEquals(NamespaceRegistrationType.SUB_NAMESPACE, info.getRegistrationType());

    Assertions.assertEquals(dto.getId(), info.getRecordId().get());
    Assertions.assertEquals(meta.getIndex(), info.getIndex());
    Assertions.assertEquals(meta.getActive(), info.isActive());

    Assertions.assertEquals(BigInteger.valueOf(4), info.getStartHeight());
    Assertions.assertEquals(BigInteger.valueOf(5), info.getEndHeight());
  }

  private NamespacePage toPage(NamespaceInfoDTO dto) {
    return new NamespacePage()
        .data(Collections.singletonList(dto))
        .pagination(new Pagination().pageNumber(1).pageSize(2));
  }

  @Test
  public void shouldGetNamespaceNames() throws Exception {

    NamespaceId namespaceId = NamespaceId.createFromName("accountalias");
    NamespaceNameDTO dto1 = new NamespaceNameDTO();
    dto1.setName("someName1");
    dto1.setId("1");
    dto1.setParentId("2");

    NamespaceNameDTO dto2 = new NamespaceNameDTO();
    dto2.setName("someName2");
    dto2.setId("3");

    mockRemoteCall(Arrays.asList(dto1, dto2));

    List<NamespaceName> names =
        repository.getNamespaceNames(Arrays.asList(namespaceId)).toFuture().get();

    Assertions.assertNotNull(names);
    Assertions.assertEquals(2, names.size());
    Assertions.assertEquals("someName1", names.get(0).getName());
    Assertions.assertEquals(BigInteger.valueOf(1L), names.get(0).getNamespaceId().getId());
    Assertions.assertEquals(
        BigInteger.valueOf(2L),
        names
            .get(0)
            .getParentId()
            .orElseThrow(() -> new IllegalStateException("No parent id"))
            .getId());

    Assertions.assertEquals("someName2", names.get(1).getName());
    Assertions.assertEquals(BigInteger.valueOf(3L), names.get(1).getNamespaceId().getId());
    Assertions.assertFalse(names.get(1).getParentId().isPresent());
  }

  @Test
  public void shouldGetLinkedAddress() throws Exception {
    Address address = Address.generateRandom(networkType);

    NamespaceId namespaceId = NamespaceId.createFromName("accountalias");
    Address ownerAccount = Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress();

    NamespaceInfoDTO dto = new NamespaceInfoDTO();
    NamespaceMetaDTO meta = new NamespaceMetaDTO();
    meta.setActive(true);
    dto.setId("SomeId");
    meta.setIndex(123);
    dto.setMeta(meta);

    NamespaceDTO namespace = new NamespaceDTO();
    namespace.setDepth(111);
    namespace.setRegistrationType(NamespaceRegistrationTypeEnum.NUMBER_0);
    namespace.setOwnerAddress(ownerAccount.encoded());

    AliasDTO alias = new AliasDTO();
    alias.setType(AliasTypeEnum.NUMBER_2);
    alias.setAddress(address.encoded());
    namespace.setAlias(alias);

    dto.setNamespace(namespace);

    mockRemoteCall(dto);

    Address linkedAddress = repository.getLinkedAddress(namespaceId).toFuture().get();

    Assertions.assertNotNull(linkedAddress);

    Assertions.assertEquals(address, linkedAddress);
  }

  @Test
  public void shouldGetLinkedMosaicId() throws Exception {

    Address ownerAccount = Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress();
    NamespaceId namespaceId = NamespaceId.createFromName("accountalias");
    NamespaceInfoDTO dto = new NamespaceInfoDTO();
    NamespaceMetaDTO meta = new NamespaceMetaDTO();
    meta.setActive(true);
    dto.setId("SomeId");
    meta.setIndex(123);
    dto.setMeta(meta);

    NamespaceDTO namespace = new NamespaceDTO();
    namespace.setDepth(111);
    namespace.setRegistrationType(NamespaceRegistrationTypeEnum.NUMBER_0);
    namespace.setOwnerAddress(ownerAccount.encoded());

    AliasDTO alias = new AliasDTO();
    alias.setType(AliasTypeEnum.NUMBER_1);
    alias.setMosaicId("528280977531AAA");
    namespace.setAlias(alias);

    dto.setNamespace(namespace);

    mockRemoteCall(dto);

    MosaicId linkedMosaicId = repository.getLinkedMosaicId(namespaceId).toFuture().get();

    Assertions.assertNotNull(linkedMosaicId);

    Assertions.assertEquals(
        MapperUtils.fromHexToBigInteger("528280977531AAA"), linkedMosaicId.getId());
  }

  @Test
  public void shouldGetMosaicsNamesFromPublicKeys() throws Exception {

    MosaicId mosaicId = MapperUtils.toMosaicId("99262122238339734");

    MosaicNamesDTO dto = new MosaicNamesDTO();
    dto.setMosaicId("99262122238339734");
    dto.setNames(Collections.singletonList("accountalias"));

    MosaicsNamesDTO accountsNamesDTO = new MosaicsNamesDTO();
    accountsNamesDTO.setMosaicNames(Collections.singletonList(dto));

    mockRemoteCall(accountsNamesDTO);

    List<MosaicNames> resolvedList =
        repository.getMosaicsNames(Collections.singletonList(mosaicId)).toFuture().get();

    Assertions.assertEquals(1, resolvedList.size());

    MosaicNames accountNames = resolvedList.get(0);

    Assertions.assertEquals(mosaicId, accountNames.getMosaicId());
    Assertions.assertEquals("accountalias", accountNames.getNames().get(0).getName());
  }

  @Test
  public void shouldGetAccountsNamesFromAddresses() throws Exception {
    Address address = Address.generateRandom(networkType);

    AccountNamesDTO dto = new AccountNamesDTO();
    dto.setAddress(encodeAddress(address));
    dto.setNames(Collections.singletonList("accountalias"));

    AccountsNamesDTO accountsNamesDTO = new AccountsNamesDTO();
    accountsNamesDTO.setAccountNames(Collections.singletonList(dto));

    mockRemoteCall(accountsNamesDTO);

    List<AccountNames> resolvedList =
        repository.getAccountsNames(Collections.singletonList(address)).toFuture().get();

    Assertions.assertEquals(1, resolvedList.size());

    AccountNames accountNames = resolvedList.get(0);

    Assertions.assertEquals(address, accountNames.getAddress());
    Assertions.assertEquals("accountalias", accountNames.getNames().get(0).getName());
  }

  @Test
  public void getNamespaceMerkle() throws Exception {
    NamespaceId namespaceId = NamespaceId.createFromName("accountalias");
    mockRemoteCall(new MerkleStateInfoDTO().raw("abc"));
    MerkleStateInfo merkle = repository.getNamespaceMerkle(namespaceId).toFuture().get();
    Assertions.assertEquals("abc", merkle.getRaw());
  }

  @Override
  public NamespaceRepositoryOkHttpImpl getRepository() {
    return repository;
  }
}
