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

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.account.AccountRestrictions;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlags;
import io.nem.symbol.sdk.openapi.vertx.model.AccountRestrictionDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountRestrictionFlagsEnum;
import io.nem.symbol.sdk.openapi.vertx.model.AccountRestrictionsDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountRestrictionsInfoDTO;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link RestrictionAccountRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class RestrictionAccountRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

    private RestrictionAccountRepositoryVertxImpl repository;


    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new RestrictionAccountRepositoryVertxImpl(apiClientMock);
    }


    @Test
    public void shouldGetAccountRestrictions() throws Exception {
        Address address =
            Address.createFromRawAddress(
                "SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        AccountRestrictionsDTO dto = new AccountRestrictionsDTO();
        dto.setAddress(address.encoded());
        AccountRestrictionDTO restriction = new AccountRestrictionDTO();
        restriction.setRestrictionFlags(AccountRestrictionFlagsEnum.NUMBER_32770);
        restriction.setValues(Arrays.asList("9636553580561478212"));
        dto.setRestrictions(Collections.singletonList(restriction));

        AccountRestrictionsInfoDTO info = new AccountRestrictionsInfoDTO();
        info.setAccountRestrictions(dto);
        mockRemoteCall(info);

        AccountRestrictions accountRestrictions = repository
            .getAccountRestrictions(address).toFuture().get();

        Assertions.assertEquals(address, accountRestrictions.getAddress());
        Assertions.assertEquals(1, accountRestrictions.getRestrictions().size());
        Assertions.assertEquals(AccountRestrictionFlags.BLOCK_MOSAIC,
            accountRestrictions.getRestrictions().get(0).getRestrictionFlags());
        Assertions.assertEquals(
            Arrays.asList(MapperUtils.toMosaicId("9636553580561478212")),
            accountRestrictions.getRestrictions().get(0).getValues());

    }

    @Test
    public void shouldGetAccountsRestrictionsFromAddresses() throws Exception {
        Address address =
            Address.createFromEncoded(
                "9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142");

        AccountRestrictionsDTO dto = new AccountRestrictionsDTO();
        dto.setAddress(address.encoded());
        AccountRestrictionDTO restriction = new AccountRestrictionDTO();
        restriction.setRestrictionFlags(AccountRestrictionFlagsEnum.NUMBER_1);
        restriction.setValues(Arrays.asList("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142"));
        dto.setRestrictions(Collections.singletonList(restriction));

        AccountRestrictionsInfoDTO info = new AccountRestrictionsInfoDTO();
        info.setAccountRestrictions(dto);
        mockRemoteCall(Collections.singletonList(info));

        AccountRestrictions accountRestrictions = repository
            .getAccountsRestrictions(Collections.singletonList(address)).toFuture()
            .get().get(0);

        Assertions.assertEquals(address, accountRestrictions.getAddress());
        Assertions.assertEquals(1, accountRestrictions.getRestrictions().size());
        Assertions.assertEquals(AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            accountRestrictions.getRestrictions().get(0).getRestrictionFlags());
        Assertions.assertEquals(Collections.singletonList(MapperUtils
                .toUnresolvedAddress("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142")),
            accountRestrictions.getRestrictions().get(0).getValues());

    }

}
