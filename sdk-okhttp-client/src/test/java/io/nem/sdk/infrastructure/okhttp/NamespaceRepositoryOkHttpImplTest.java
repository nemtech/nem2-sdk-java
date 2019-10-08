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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.sdk.openapi.okhttp_gson.model.AliasDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AliasTypeEnum;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceDTO;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceMetaDTO;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceNameDTO;
import io.nem.sdk.openapi.okhttp_gson.model.NamespaceRegistrationTypeEnum;
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
        resolveNetworkType();

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

        Assertions.assertEquals(NamespaceRegistrationType.SUB_NAMESPACE, info.getRegistrationType());

        Assertions.assertEquals(meta.getId(), info.getMetaId());
        Assertions.assertEquals(meta.getIndex(), info.getIndex());
        Assertions.assertEquals(meta.getActive(), info.isActive());

        Assertions.assertEquals(BigInteger.valueOf(4), info.getStartHeight());
        Assertions.assertEquals(BigInteger.valueOf(5), info.getEndHeight());
    }

    @Test
    public void shouldGetNamespacesFromAccount() throws Exception {

        resolveNetworkType();

        Address address = MapperUtils.toAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

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
        alias.setAddress(address.plain());
        namespace.setAlias(alias);

        dto.setNamespace(namespace);

        mockRemoteCall(Collections.singletonList(dto));

        NamespaceInfo info = repository.getNamespacesFromAccount(address).toFuture().get().get(0);

        Assertions.assertNotNull(info);

        Assertions.assertEquals(NamespaceRegistrationType.SUB_NAMESPACE, info.getRegistrationType());

        Assertions.assertEquals(meta.getId(), info.getMetaId());
        Assertions.assertEquals(meta.getIndex(), info.getIndex());
        Assertions.assertEquals(meta.getActive(), info.isActive());

        Assertions.assertEquals(BigInteger.valueOf(4), info.getStartHeight());
        Assertions.assertEquals(BigInteger.valueOf(5), info.getEndHeight());
    }

    @Test
    public void shouldGetNamespacesFromAccounts() throws Exception {

        resolveNetworkType();

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
        alias.setAddress(address.plain());
        namespace.setAlias(alias);

        dto.setNamespace(namespace);

        mockRemoteCall(Collections.singletonList(dto));

        NamespaceInfo info = repository
            .getNamespacesFromAccounts(Collections.singletonList(address)).toFuture().get().get(0);

        Assertions.assertNotNull(info);

        Assertions.assertEquals(NamespaceRegistrationType.SUB_NAMESPACE, info.getRegistrationType());

        Assertions.assertEquals(meta.getId(), info.getMetaId());
        Assertions.assertEquals(meta.getIndex(), info.getIndex());
        Assertions.assertEquals(meta.getActive(), info.isActive());

        Assertions.assertEquals(BigInteger.valueOf(4), info.getStartHeight());
        Assertions.assertEquals(BigInteger.valueOf(5), info.getEndHeight());
    }

    @Test
    public void shouldGetNamespaceNames() throws Exception {
        resolveNetworkType();
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

        resolveNetworkType();

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
        alias.setAddress(address.plain());
        namespace.setAlias(alias);

        dto.setNamespace(namespace);

        mockRemoteCall(dto);

        Address linkedAddress = repository.getLinkedAddress(namespaceId).toFuture().get();

        Assertions.assertNotNull(linkedAddress);

        Assertions.assertEquals(address, linkedAddress);
    }

    @Test
    public void shouldGetLinkedMosaicId() throws Exception {
        resolveNetworkType();
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
        alias.setMosaicId("528280977531AAA");
        namespace.setAlias(alias);

        dto.setNamespace(namespace);

        mockRemoteCall(dto);

        MosaicId linkedMosaicId = repository.getLinkedMosaicId(namespaceId).toFuture().get();

        Assertions.assertNotNull(linkedMosaicId);

        Assertions.assertEquals(MapperUtils.fromHex("528280977531AAA"), linkedMosaicId.getId());
    }


    @Override
    public NamespaceRepositoryOkHttpImpl getRepository() {
        return repository;
    }
}
