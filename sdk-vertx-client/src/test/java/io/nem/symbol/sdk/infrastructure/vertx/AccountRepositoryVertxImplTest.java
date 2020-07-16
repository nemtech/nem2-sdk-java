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

import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.AccountSearchCriteria;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountType;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.openapi.vertx.model.AccountDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountLinkPublicKeyDTO;
import io.nem.symbol.sdk.openapi.vertx.model.AccountPage;
import io.nem.symbol.sdk.openapi.vertx.model.AccountTypeEnum;
import io.nem.symbol.sdk.openapi.vertx.model.ActivityBucketDTO;
import io.nem.symbol.sdk.openapi.vertx.model.Mosaic;
import io.nem.symbol.sdk.openapi.vertx.model.Pagination;
import io.nem.symbol.sdk.openapi.vertx.model.SupplementalPublicKeysDTO;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link AccountRepositoryVertxImpl}
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryVertxImplTest extends AbstractVertxRespositoryTest {

    private AccountRepositoryVertxImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new AccountRepositoryVertxImpl(apiClientMock);
    }

    @Test
    public void shouldGetAccountInfo() throws Exception {
        Address address = Address.generateRandom(this.networkType);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));
        List<Mosaic> mosaicDtos = new ArrayList<>();
        mosaicDtos.add(new Mosaic().id("0000000000000ABC").amount(BigInteger.TEN));
        accountDTO.setMosaics(mosaicDtos);
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccount(accountDTO);

        mockRemoteCall(accountInfoDTO);

        AccountInfo resolvedAccountInfo = repository.getAccountInfo(address).toFuture().get();
        Assertions.assertEquals(address, resolvedAccountInfo.getAddress());
        Assertions.assertEquals(AccountType.MAIN, resolvedAccountInfo.getAccountType());
        Assertions.assertEquals(1, resolvedAccountInfo.getMosaics().size());
        Assertions.assertEquals("0000000000000ABC", resolvedAccountInfo.getMosaics().get(0).getId().getIdAsHex());
        Assertions.assertEquals(BigInteger.TEN, resolvedAccountInfo.getMosaics().get(0).getAmount());
    }

    @Test
    public void shouldGetAccountsInfoFromAddresses() throws ExecutionException, InterruptedException {

        Address address = Address.generateRandom(this.networkType);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));
        accountDTO.setSupplementalPublicKeys(
            new SupplementalPublicKeysDTO().node(new AccountLinkPublicKeyDTO().publicKey("abc")));

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccount(accountDTO);

        BigInteger startHeight = BigInteger.ONE;
        BigInteger totalFeesPaid = BigInteger.valueOf(2);
        long beneficiaryCount = 3;
        BigInteger rawScore = BigInteger.valueOf(4);
        accountDTO.addActivityBucketsItem(new ActivityBucketDTO().startHeight(startHeight).totalFeesPaid(totalFeesPaid)
            .beneficiaryCount(beneficiaryCount).rawScore(rawScore));

        mockRemoteCall(Collections.singletonList(accountInfoDTO));

        List<AccountInfo> resolvedAccountInfos = repository.getAccountsInfo(Collections.singletonList(address))
            .toFuture().get();

        Assertions.assertEquals(1, resolvedAccountInfos.size());

        AccountInfo resolvedAccountInfo = resolvedAccountInfos.get(0);

        Assertions.assertEquals(address, resolvedAccountInfo.getAddress());
        Assertions.assertEquals(AccountType.MAIN, resolvedAccountInfo.getAccountType());
        Assertions.assertEquals("abc", resolvedAccountInfo.getSupplementalAccountKeys().getNode().get());

        Assertions.assertEquals(1, resolvedAccountInfo.getActivityBuckets().size());
        Assertions.assertEquals(startHeight, resolvedAccountInfo.getActivityBuckets().get(0).getStartHeight());
        Assertions.assertEquals(totalFeesPaid, resolvedAccountInfo.getActivityBuckets().get(0).getTotalFeesPaid());
        Assertions
            .assertEquals(beneficiaryCount, resolvedAccountInfo.getActivityBuckets().get(0).getBeneficiaryCount());
        Assertions.assertEquals(rawScore, resolvedAccountInfo.getActivityBuckets().get(0).getRawScore());

    }


    @Test
    public void shouldProcessExceptionWhenNotFound() {
        Address address = Address.generateRandom(this.networkType);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccount(accountDTO);

        mockErrorCode(404, "Account not found!");

        Assertions.assertEquals("ApiException: Not Found - 404 - Code Not Found - Account not found!",
            Assertions.assertThrows(RepositoryCallException.class, () -> {
                ExceptionUtils.propagate(() -> repository.getAccountInfo(address).toFuture().get());
            }).getMessage());

    }

    @Test
    public void shouldProcessExceptionWhenNotFoundInvalidResponse() {

        Address address = Address.generateRandom(this.networkType);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccount(accountDTO);

        mockErrorCodeRawResponse(400, "I'm a raw error, not json");

        Assertions.assertEquals("ApiException: Bad Request - 400 - I'm a raw error, not json",
            Assertions.assertThrows(RepositoryCallException.class, () -> {
                ExceptionUtils.propagate(() -> repository.getAccountInfo(address).toFuture().get());
            }).getMessage());
    }

    @Test
    public void search() throws Exception {
        Address address = Address.generateRandom(this.networkType);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));
        accountDTO.setSupplementalPublicKeys(
            new SupplementalPublicKeysDTO().node(new AccountLinkPublicKeyDTO().publicKey("abc")));

        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setAccount(accountDTO);

        BigInteger startHeight = BigInteger.ONE;
        BigInteger totalFeesPaid = BigInteger.valueOf(2);
        long beneficiaryCount = 3;
        BigInteger rawScore = BigInteger.valueOf(4);
        accountDTO.addActivityBucketsItem(new ActivityBucketDTO().startHeight(startHeight).totalFeesPaid(totalFeesPaid)
            .beneficiaryCount(beneficiaryCount).rawScore(rawScore));

        mockRemoteCall(toPage(accountInfoDTO));

        List<AccountInfo> resolvedAccountInfos = repository.search(new AccountSearchCriteria()).toFuture().get()
            .getData();

        Assertions.assertEquals(1, resolvedAccountInfos.size());

        AccountInfo resolvedAccountInfo = resolvedAccountInfos.get(0);

        Assertions.assertEquals(address, resolvedAccountInfo.getAddress());
        Assertions.assertEquals(AccountType.MAIN, resolvedAccountInfo.getAccountType());
        Assertions.assertEquals("abc", resolvedAccountInfo.getSupplementalAccountKeys().getNode().get());

        Assertions.assertEquals(1, resolvedAccountInfo.getActivityBuckets().size());
        Assertions.assertEquals(startHeight, resolvedAccountInfo.getActivityBuckets().get(0).getStartHeight());
        Assertions.assertEquals(totalFeesPaid, resolvedAccountInfo.getActivityBuckets().get(0).getTotalFeesPaid());
        Assertions
            .assertEquals(beneficiaryCount, resolvedAccountInfo.getActivityBuckets().get(0).getBeneficiaryCount());
        Assertions.assertEquals(rawScore, resolvedAccountInfo.getActivityBuckets().get(0).getRawScore());

    }


    private AccountPage toPage(AccountInfoDTO dto) {
        return new AccountPage().data(Collections.singletonList(dto))
            .pagination(new Pagination().pageNumber(1).pageSize(2).totalEntries(3).totalPages(4));
    }


}
