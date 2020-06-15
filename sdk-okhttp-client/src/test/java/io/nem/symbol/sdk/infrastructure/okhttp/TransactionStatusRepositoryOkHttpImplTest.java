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

import static io.nem.symbol.sdk.infrastructure.okhttp.TestHelperOkHttp.loadTransactionInfoDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.model.transaction.TransactionStatus;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionGroupEnum;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionStatusDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.TransactionStatusEnum;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link TransactionStatusRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class TransactionStatusRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private TransactionStatusRepositoryOkHttpImpl repository;


    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new TransactionStatusRepositoryOkHttpImpl(apiClientMock);
    }
    @Test
    public void shouldGetTransactionStatus() throws Exception {

        TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
        transactionStatusDTO.setGroup(TransactionGroupEnum.FAILED);
        transactionStatusDTO.setDeadline(BigInteger.valueOf(5));
        transactionStatusDTO.setHeight(BigInteger.valueOf(6));
        transactionStatusDTO
            .setCode(TransactionStatusEnum.FAILURE_ACCOUNTLINK_LINK_ALREADY_EXISTS);
        transactionStatusDTO.setHash("someHash");
        mockRemoteCall(transactionStatusDTO);

        TransactionStatus transaction = repository
            .getTransactionStatus(transactionStatusDTO.getHash()).toFuture().get();

        Assertions.assertNotNull(transaction);

        Assertions.assertEquals(transactionStatusDTO.getHash(), transaction.getHash());
        Assertions.assertEquals(5L, transaction.getDeadline().getInstant());
        Assertions.assertEquals(BigInteger.valueOf(6L), transaction.getHeight());
        Assertions.assertEquals("Failure_AccountLink_Link_Already_Exists", transaction.getCode());
        Assertions.assertEquals(transaction.getGroup().getValue(),
            transactionStatusDTO.getGroup().getValue());
    }

    @Test
    public void shouldGetTransactionStatuses() throws Exception {

        TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
        transactionStatusDTO.setGroup(TransactionGroupEnum.FAILED);
        transactionStatusDTO.setDeadline(BigInteger.valueOf(5));
        transactionStatusDTO.setHeight(BigInteger.valueOf(6));
        transactionStatusDTO
            .setCode(TransactionStatusEnum.FAILURE_ACCOUNTLINK_LINK_ALREADY_EXISTS);
        transactionStatusDTO.setHash("someHash");
        mockRemoteCall(Collections.singletonList(transactionStatusDTO));

        TransactionStatus transaction = repository
            .getTransactionStatuses(Collections.singletonList(transactionStatusDTO.getHash()))
            .toFuture().get().get(0);

        Assertions.assertNotNull(transaction);

        Assertions.assertEquals(transactionStatusDTO.getHash(), transaction.getHash());
        Assertions.assertEquals(5L, transaction.getDeadline().getInstant());
        Assertions.assertEquals(BigInteger.valueOf(6L), transaction.getHeight());
        Assertions.assertEquals("Failure_AccountLink_Link_Already_Exists", transaction.getCode());
        Assertions.assertEquals(transaction.getGroup().getValue(),
            transactionStatusDTO.getGroup().getValue());
    }


    @Override
    public TransactionStatusRepositoryOkHttpImpl getRepository() {
        return repository;
    }

}
