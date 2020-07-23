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

import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.sdk.api.AccountOrderBy;
import io.nem.symbol.sdk.api.AccountSearchCriteria;
import io.nem.symbol.sdk.api.RepositoryCallException;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountType;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountLinkPublicKeyDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountPage;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.AccountTypeEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.ActivityBucketDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Pagination;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SupplementalPublicKeysDTO;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link AccountRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private AccountRepositoryOkHttpImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new AccountRepositoryOkHttpImpl(apiClientMock);
    }

    @Test
    public void shouldGetAccountInfo() throws Exception {

        Address address = Address.generateRandom(networkType);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType(AccountTypeEnum.NUMBER_1);
        accountDTO.setAddress(encodeAddress(address));
        accountDTO.setSupplementalPublicKeys(
            new SupplementalPublicKeysDTO().linked(new AccountLinkPublicKeyDTO().publicKey("abc")));

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
        Assertions.assertEquals("abc", resolvedAccountInfo.getSupplementalAccountKeys().getLinked().get());

        Assertions.assertEquals(1, resolvedAccountInfo.getActivityBuckets().size());
        Assertions.assertEquals(startHeight, resolvedAccountInfo.getActivityBuckets().get(0).getStartHeight());
        Assertions.assertEquals(totalFeesPaid, resolvedAccountInfo.getActivityBuckets().get(0).getTotalFeesPaid());
        Assertions
            .assertEquals(beneficiaryCount, resolvedAccountInfo.getActivityBuckets().get(0).getBeneficiaryCount());
        Assertions.assertEquals(rawScore, resolvedAccountInfo.getActivityBuckets().get(0).getRawScore());
    }


    @Test
    public void shouldGetAccountsInfoFromAddresses() throws Exception {

        Address address = Address.generateRandom(networkType);

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
    public void shouldProcessExceptionWhenNotFound() throws Exception {

        Address address = Address.generateRandom(networkType);

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
    public void shouldProcessExceptionWhenNotFoundInvalidResponse() throws Exception {

        Address address = Address.generateRandom(networkType);

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


    @Override
    public AccountRepositoryOkHttpImpl getRepository() {
        return repository;
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

        List<AccountInfo> resolvedAccountInfos = repository
            .search(new AccountSearchCriteria().orderBy(AccountOrderBy.BALANCE)).toFuture().get().getData();

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
