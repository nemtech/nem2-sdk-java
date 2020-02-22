/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.AccountNames;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.openapi.vertx.model.AccountNamesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountsNamesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AliasDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AliasTypeEnum;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicNamesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicsNamesDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NamespaceDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NamespaceInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NamespaceMetaDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NamespaceNameDTO;
import io.nem.symbol.sdk.openapi.vertx.model.NamespaceRegistrationTypeEnum;
import io.nem.symbol.sdk.openapi.vertx.model.NamespacesInfoDTO;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link NamespaceRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class NamespaceRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

    private NamespaceRepositoryVertxImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new NamespaceRepositoryVertxImpl(apiClientMock, networkTypeObservable);
    }

    @Test
    public void shouldGetNamespace() throws Exception {

        NamespaceId namespaceId = NamespaceId.createFromName("accountalias");

        NamespaceInfoDTO dto = new NamespaceInfoDTO();
        NamespaceMetaDTO meta = new NamespaceMetaDTO();
        meta.setActive(true);
        meta.setId("SomeId");
        meta.setIndex(123);
        dto.setMeta(meta);

        NamespaceDTO namespace = new NamespaceDTO();
        namespace.setDepth(111);
        namespace.setStartHeight(BigInteger.valueOf(4));
        namespace.setEndHeight(BigInteger.valueOf(5));
        namespace.setRegistrationType(NamespaceRegistrationTypeEnum.NUMBER_1);
        namespace
            .setOwnerPublicKey("AC1A6E1D8DE5B17D2C6B1293F1CAD3829EEACF38D09311BB3C8E5A880092DE26");

        AliasDTO alias = new AliasDTO();
        alias.setType(AliasTypeEnum.NUMBER_1);
        alias.setMosaicId("123");
        namespace.setAlias(alias);

        dto.setNamespace(namespace);

        mockRemoteCall(dto);

        NamespaceInfo info = repository.getNamespace(namespaceId).toFuture().get();

        Assertions.assertNotNull(info);

        Assertions
            .assertEquals(NamespaceRegistrationType.SUB_NAMESPACE, info.getRegistrationType());

        Assertions.assertEquals(meta.getId(), info.getMetaId());
        Assertions.assertEquals(meta.getIndex(), info.getIndex());
        Assertions.assertEquals(meta.getActive(), info.isActive());

        Assertions.assertEquals(BigInteger.valueOf(4), info.getStartHeight());
        Assertions.assertEquals(BigInteger.valueOf(5), info.getEndHeight());
    }

    @Test
    public void shouldGetNamespacesFromAccount() throws Exception {

        Address address = MapperUtils
            .toAddressFromRawAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        NamespaceInfoDTO dto = new NamespaceInfoDTO();
        NamespaceMetaDTO meta = new NamespaceMetaDTO();
        meta.setActive(true);
        meta.setId("SomeId");
        meta.setIndex(123);
        dto.setMeta(meta);

        NamespaceDTO namespace = new NamespaceDTO();
        namespace.setDepth(111);
        namespace.setStartHeight(BigInteger.valueOf(4));
        namespace.setEndHeight(BigInteger.valueOf(5));
        namespace.setRegistrationType(NamespaceRegistrationTypeEnum.NUMBER_1);
        namespace
            .setOwnerPublicKey("AC1A6E1D8DE5B17D2C6B1293F1CAD3829EEACF38D09311BB3C8E5A880092DE26");

        AliasDTO alias = new AliasDTO();
        alias.setType(AliasTypeEnum.NUMBER_2);
        alias.setAddress(address.encoded());
        namespace.setAlias(alias);

        dto.setNamespace(namespace);

        mockRemoteCall(new NamespacesInfoDTO().addNamespacesItem(dto));

        NamespaceInfo info = repository.getNamespacesFromAccount(address).toFuture().get().get(0);

        Assertions.assertNotNull(info);

        Assertions
            .assertEquals(NamespaceRegistrationType.SUB_NAMESPACE, info.getRegistrationType());

        Assertions.assertEquals(meta.getId(), info.getMetaId());
        Assertions.assertEquals(meta.getIndex(), info.getIndex());
        Assertions.assertEquals(meta.getActive(), info.isActive());

        Assertions.assertEquals(BigInteger.valueOf(4), info.getStartHeight());
        Assertions.assertEquals(BigInteger.valueOf(5), info.getEndHeight());
    }

    @Test
    public void shouldGetNamespacesFromAccounts() throws Exception {

        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        NamespaceInfoDTO dto = new NamespaceInfoDTO();
        NamespaceMetaDTO meta = new NamespaceMetaDTO();
        meta.setActive(true);
        meta.setId("SomeId");
        meta.setIndex(123);
        dto.setMeta(meta);

        NamespaceDTO namespace = new NamespaceDTO();
        namespace.setDepth(111);
        namespace.setStartHeight(BigInteger.valueOf(4));
        namespace.setEndHeight(BigInteger.valueOf(5));
        namespace.setRegistrationType(NamespaceRegistrationTypeEnum.NUMBER_1);
        namespace
            .setOwnerPublicKey("AC1A6E1D8DE5B17D2C6B1293F1CAD3829EEACF38D09311BB3C8E5A880092DE26");

        AliasDTO alias = new AliasDTO();
        alias.setType(AliasTypeEnum.NUMBER_2);
        alias.setAddress(address.encoded());
        namespace.setAlias(alias);

        dto.setNamespace(namespace);

        mockRemoteCall(new NamespacesInfoDTO().addNamespacesItem(dto));

        NamespaceInfo info = repository
            .getNamespacesFromAccounts(Collections.singletonList(address)).toFuture().get().get(0);

        Assertions.assertNotNull(info);

        Assertions
            .assertEquals(NamespaceRegistrationType.SUB_NAMESPACE, info.getRegistrationType());

        Assertions.assertEquals(meta.getId(), info.getMetaId());
        Assertions.assertEquals(meta.getIndex(), info.getIndex());
        Assertions.assertEquals(meta.getActive(), info.isActive());

        Assertions.assertEquals(BigInteger.valueOf(4), info.getStartHeight());
        Assertions.assertEquals(BigInteger.valueOf(5), info.getEndHeight());
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

        List<NamespaceName> names = repository.getNamespaceNames(Arrays.asList(namespaceId))
            .toFuture().get();

        Assertions.assertNotNull(names);
        Assertions.assertEquals(2, names.size());
        Assertions.assertEquals("someName1", names.get(0).getName());
        Assertions.assertEquals(BigInteger.valueOf(1L), names.get(0).getNamespaceId().getId());
        Assertions.assertEquals(BigInteger.valueOf(2L),
            names.get(0).getParentId().orElseThrow(() -> new IllegalStateException("No parent id"))
                .getId());

        Assertions.assertEquals("someName2", names.get(1).getName());
        Assertions.assertEquals(BigInteger.valueOf(3L), names.get(1).getNamespaceId().getId());
        Assertions.assertFalse(names.get(1).getParentId().isPresent());
    }

    @Test
    public void shouldGetLinkedAddress() throws Exception {

        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        NamespaceId namespaceId = NamespaceId.createFromName("accountalias");

        NamespaceInfoDTO dto = new NamespaceInfoDTO();
        NamespaceMetaDTO meta = new NamespaceMetaDTO();
        meta.setActive(true);
        meta.setId("SomeId");
        meta.setIndex(123);
        dto.setMeta(meta);

        NamespaceDTO namespace = new NamespaceDTO();
        namespace.setDepth(111);
        namespace.setRegistrationType(NamespaceRegistrationTypeEnum.NUMBER_0);
        namespace
            .setOwnerPublicKey("AC1A6E1D8DE5B17D2C6B1293F1CAD3829EEACF38D09311BB3C8E5A880092DE26");

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

        NamespaceId namespaceId = NamespaceId.createFromName("accountalias");

        NamespaceInfoDTO dto = new NamespaceInfoDTO();
        NamespaceMetaDTO meta = new NamespaceMetaDTO();
        meta.setActive(true);
        meta.setId("SomeId");
        meta.setIndex(123);
        dto.setMeta(meta);

        NamespaceDTO namespace = new NamespaceDTO();
        namespace.setDepth(111);
        namespace.setRegistrationType(NamespaceRegistrationTypeEnum.NUMBER_0);
        namespace
            .setOwnerPublicKey("AC1A6E1D8DE5B17D2C6B1293F1CAD3829EEACF38D09311BB3C8E5A880092DE26");

        AliasDTO alias = new AliasDTO();
        alias.setType(AliasTypeEnum.NUMBER_1);
        alias.setMosaicId("528280977531");
        namespace.setAlias(alias);

        dto.setNamespace(namespace);

        mockRemoteCall(dto);

        MosaicId linkedMosaicId = repository.getLinkedMosaicId(namespaceId).toFuture().get();

        Assertions.assertNotNull(linkedMosaicId);

        Assertions.assertEquals("0000528280977531", linkedMosaicId.getIdAsHex());
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

        List<MosaicNames> resolvedList = repository
            .getMosaicsNames(Collections.singletonList(mosaicId))
            .toFuture().get();

        Assertions.assertEquals(1, resolvedList.size());

        MosaicNames accountNames = resolvedList.get(0);

        Assertions.assertEquals(mosaicId, accountNames.getMosaicId());
        Assertions.assertEquals("accountalias", accountNames.getNames().get(0).getName());
    }

    @Test
    public void shouldGetAccountsNamesFromAddresses() throws Exception {
        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        AccountNamesDTO dto = new AccountNamesDTO();
        dto.setAddress(encodeAddress(address));
        dto.setNames(Collections.singletonList("accountalias"));

        AccountsNamesDTO accountsNamesDTO = new AccountsNamesDTO();
        accountsNamesDTO.setAccountNames(Collections.singletonList(dto));

        mockRemoteCall(accountsNamesDTO);

        List<AccountNames> resolvedList = repository
            .getAccountsNames(Collections.singletonList(address)).toFuture().get();

        Assertions.assertEquals(1, resolvedList.size());

        AccountNames accountNames = resolvedList.get(0);

        Assertions.assertEquals(address, accountNames.getAddress());
        Assertions.assertEquals("accountalias", accountNames.getNames().get(0).getName());
    }


}
