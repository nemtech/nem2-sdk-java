/*
 * Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.nem.sdk.infrastructure.vertx;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.Listener;
import io.nem.sdk.infrastructure.ListenerChannel;
import io.nem.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.message.PlainMessage;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.HashLockTransaction;
import io.nem.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionStatusError;
import io.nem.sdk.model.transaction.TransactionStatusException;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.TransferTransactionFactory;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferFactoryImpl;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Tests for the {@link ListenerVertx} implementation of the {@link Listener}
 */
public class ListenerVertxTest {

    private ListenerVertx listener;

    private HttpClient httpClientMock;

    private WebSocket webSocketMock;

    private JsonHelper jsonHelper;

    private String wsId = "TheWSid";

    @BeforeEach
    public void setUp() {
        httpClientMock = Mockito.mock(HttpClient.class);
        String url = "http://nem.com:3000/";
        listener = new ListenerVertx(httpClientMock, url);
        jsonHelper = listener.getJsonHelper();
        webSocketMock = Mockito.mock(WebSocket.class);
    }


    @AfterEach
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(webSocketMock);
    }


    @Test
    public void shouldOpen() throws ExecutionException, InterruptedException, TimeoutException {

        Assertions.assertNull(listener.getUid());

        Assertions.assertEquals(
            "Listener has not been opened yet. Please call the open method before subscribing.",
            Assertions
                .assertThrows(IllegalStateException.class, () -> listener.newBlock()).getMessage());

        simulateWebSocketStartup();

        Assertions.assertNotNull(listener.newBlock());

        Assertions.assertEquals(wsId, listener.getUid());

        listener.close();
        listener.close();

        Assertions.assertNull(listener.getUid());

        Assertions.assertEquals(
            "Listener has not been opened yet. Please call the open method before subscribing.",
            Assertions
                .assertThrows(IllegalStateException.class, () -> listener.newBlock()).getMessage());

        Map<String, String> sendPayload = new HashMap<>();
        sendPayload.put("uid", wsId);
        sendPayload.put("subscribe", "block");

        Mockito.verify(webSocketMock).writeTextMessage(jsonHelper.print(sendPayload));
        Mockito.verify(webSocketMock).handler(Mockito.any());
        Mockito.verify(webSocketMock).close();
    }

    @Test
    public void shouldHandleTransaction()
        throws InterruptedException, ExecutionException, TimeoutException {
        simulateWebSocketStartup();

        TransactionInfoDTO transactionInfo = TestHelperVertx.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        ObjectNode transactionInfoDtoJsonObject = jsonHelper
            .convert(transactionInfo, ObjectNode.class);

        Address address = Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NetworkType.MIJIN_TEST);

        String channelName = ListenerChannel.CONFIRMED_ADDED.toString();

        ((ObjectNode) transactionInfoDtoJsonObject.get("meta")).put("channelName", channelName);

        List<Transaction> transactions = new ArrayList<>();
        List<Throwable> exceptions = new ArrayList<>();
        listener.confirmed(address, transactionInfo.getMeta().getHash()).doOnError(exceptions::add)
            .forEach(transactions::add);

        listener.handle(transactionInfoDtoJsonObject, null);

        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(0, exceptions.size());

        Assertions.assertEquals(address, transactions.get(0).getSigner().get().getAddress());

        Mockito.verify(webSocketMock).handler(Mockito.any());
        Mockito.verify(webSocketMock)
            .writeTextMessage(jsonHelper.print(new ListenerSubscribeMessage(this.wsId,
                channelName + "/" + address.plain())));
        Mockito.verify(webSocketMock).writeTextMessage(jsonHelper
            .print(new ListenerSubscribeMessage(this.wsId, "status" + "/" + address.plain())));

    }

    @Test
    public void confirmedUsingHash()
        throws InterruptedException, ExecutionException, TimeoutException {
        simulateWebSocketStartup();

        TransactionInfoDTO transactionInfo = TestHelperVertx.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        ObjectNode transactionInfoDtoJsonObject = jsonHelper
            .convert(transactionInfo, ObjectNode.class);

        Address address = Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NetworkType.MIJIN_TEST);

        String channelName = ListenerChannel.CONFIRMED_ADDED.toString();

        ((ObjectNode) transactionInfoDtoJsonObject.get("meta"))
            .put("channelName", channelName);

        List<Transaction> transactions = new ArrayList<>();
        List<Throwable> exceptions = new ArrayList<>();
        listener.confirmed(address, transactionInfo.getMeta().getHash()).doOnError(exceptions::add)
            .forEach(transactions::add);

        listener.handle(transactionInfoDtoJsonObject, null);

        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(0, exceptions.size());

        Assertions.assertEquals(address, transactions.get(0).getSigner().get().getAddress());

        Mockito.verify(webSocketMock).handler(Mockito.any());
        Mockito.verify(webSocketMock)
            .writeTextMessage(jsonHelper.print(new ListenerSubscribeMessage(this.wsId,
                channelName + "/" + address.plain())));

        Mockito.verify(webSocketMock).writeTextMessage(jsonHelper
            .print(new ListenerSubscribeMessage(this.wsId, "status" + "/" + address.plain())));

    }

    @Test
    public void confirmedUsingHashRaiseError()
        throws InterruptedException, ExecutionException, TimeoutException {
        simulateWebSocketStartup();

        TransactionInfoDTO transactionInfo = TestHelperVertx.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        ObjectNode transactionInfoDtoJsonObject = jsonHelper
            .convert(transactionInfo, ObjectNode.class);

        Address address = Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NetworkType.MIJIN_TEST);

        String channelName = ListenerChannel.CONFIRMED_ADDED.toString();

        Map<String, Object> transactionStatusError = new HashMap<>();
        transactionStatusError.put("address", address.encoded());
        transactionStatusError.put("code", "Fail 666");
        transactionStatusError.put("hash", transactionInfo.getMeta().getHash());
        transactionStatusError.put("deadline", 123);

        List<Transaction> transactions = new ArrayList<>();
        List<Throwable> exceptions = new ArrayList<>();
        listener.confirmed(address, transactionInfo.getMeta().getHash()).doOnError(exceptions::add)
            .forEach(transactions::add);

        listener.handle(transactionStatusError, null);

        Assertions.assertEquals(0, transactions.size());
        Assertions.assertEquals(1, exceptions.size());
        Assertions.assertEquals(TransactionStatusException.class, exceptions.get(0).getClass());
        Assertions
            .assertEquals("Fail 666 processing transaction " + transactionInfo.getMeta().getHash(),
                exceptions.get(0).getMessage());

        Mockito.verify(webSocketMock).handler(Mockito.any());
        Mockito.verify(webSocketMock)
            .writeTextMessage(jsonHelper.print(new ListenerSubscribeMessage(this.wsId,
                channelName + "/" + address.plain())));

        Mockito.verify(webSocketMock)
            .writeTextMessage(jsonHelper
                .print(new ListenerSubscribeMessage(this.wsId, "status" + "/" + address.plain())));

    }

    @Test
    public void aggregateBondedAddedUsingHash()
        throws InterruptedException, ExecutionException, TimeoutException {
        simulateWebSocketStartup();

        TransactionInfoDTO transactionInfo = TestHelperVertx.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        ObjectNode transactionInfoDtoJsonObject = jsonHelper
            .convert(transactionInfo, ObjectNode.class);

        Address address = Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NetworkType.MIJIN_TEST);

        String channelName = ListenerChannel.AGGREGATE_BONDED_ADDED.toString();

        ((ObjectNode) transactionInfoDtoJsonObject.get("meta"))
            .put("channelName", channelName);

        List<Transaction> transactions = new ArrayList<>();
        List<Throwable> exceptions = new ArrayList<>();
        listener.aggregateBondedAdded(address, transactionInfo.getMeta().getHash())
            .doOnError(exceptions::add)
            .forEach(transactions::add);

        listener.handle(transactionInfoDtoJsonObject, null);

        Assertions.assertEquals(1, transactions.size());
        Assertions.assertEquals(0, exceptions.size());

        Assertions.assertEquals(address, transactions.get(0).getSigner().get().getAddress());

        Mockito.verify(webSocketMock).handler(Mockito.any());
        Mockito.verify(webSocketMock)
            .writeTextMessage(jsonHelper.print(new ListenerSubscribeMessage(this.wsId,
                channelName + "/" + address.plain())));

        Mockito.verify(webSocketMock).writeTextMessage(jsonHelper
            .print(new ListenerSubscribeMessage(this.wsId, "status" + "/" + address.plain())));

    }

    @Test
    public void aggregateBondedAddedUsingHashOnError()
        throws InterruptedException, ExecutionException, TimeoutException {
        simulateWebSocketStartup();

        TransactionInfoDTO transactionInfo = TestHelperVertx.loadTransactionInfoDTO(
            "shouldCreateAggregateMosaicCreationTransaction.json");

        ObjectNode transactionInfoDtoJsonObject = jsonHelper
            .convert(transactionInfo, ObjectNode.class);

        Address address = Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NetworkType.MIJIN_TEST);

        String channelName = ListenerChannel.AGGREGATE_BONDED_ADDED.toString();

        Map<String, Object> transactionStatusError = new HashMap<>();
        transactionStatusError.put("address", address.encoded());
        transactionStatusError.put("code", "Fail 666");
        transactionStatusError.put("hash", transactionInfo.getMeta().getHash());
        transactionStatusError.put("deadline", 123);

        List<Transaction> transactions = new ArrayList<>();
        List<Throwable> exceptions = new ArrayList<>();
        listener.aggregateBondedAdded(address, transactionInfo.getMeta().getHash())
            .doOnError(exceptions::add)
            .forEach(transactions::add);

        listener.handle(transactionStatusError, null);

        Assertions.assertEquals(0, transactions.size());
        Assertions.assertEquals(1, exceptions.size());
        Assertions.assertEquals(TransactionStatusException.class, exceptions.get(0).getClass());
        Assertions
            .assertEquals("Fail 666 processing transaction " + transactionInfo.getMeta().getHash(),
                exceptions.get(0).getMessage());

        Mockito.verify(webSocketMock).handler(Mockito.any());
        Mockito.verify(webSocketMock)
            .writeTextMessage(jsonHelper.print(new ListenerSubscribeMessage(this.wsId,
                channelName + "/" + address.plain())));

        Mockito.verify(webSocketMock)
            .writeTextMessage(jsonHelper
                .print(new ListenerSubscribeMessage(this.wsId, "status" + "/" + address.plain())));

    }

    private void simulateWebSocketStartup()
        throws InterruptedException, ExecutionException, TimeoutException {

        ArgumentCaptor<Handler> webSocketHandlerCapture = ArgumentCaptor.forClass(Handler.class);
        ArgumentCaptor<Handler> bufferHandlerCapture = ArgumentCaptor.forClass(Handler.class);

        when(httpClientMock.websocket(any(RequestOptions.class), webSocketHandlerCapture.capture()))
            .thenReturn(httpClientMock);
        when(webSocketMock.handler(bufferHandlerCapture.capture())).thenReturn(webSocketMock);

        CompletableFuture<Void> future = listener.open();

        Handler<WebSocket> webSocketHandler = webSocketHandlerCapture.getValue();
        Assertions.assertNotNull(webSocketHandler);

        webSocketHandler.handle(webSocketMock);

        Handler<Buffer> bufferHandler = bufferHandlerCapture.getValue();
        Assertions.assertNotNull(bufferHandler);

        Buffer event = new BufferFactoryImpl()
            .buffer(jsonHelper.print(Collections.singletonMap("uid", wsId)));
        bufferHandler.handle(event);

        future.get(3, TimeUnit.SECONDS);
    }

    @Test
    public void shouldCheckTransactionFromAddressTransferTransaction() {
        Account account1 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Account account2 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Account account3 = Account.generateNewAccount(NetworkType.MIJIN_TEST);

        Assertions.assertTrue(listener
            .transactionFromAddress(
                transferTransaction(account1.getPublicAccount(), account3.getAddress()),
                account1.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(
                transferTransaction(account2.getPublicAccount(), account3.getAddress()),
                account1.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(transferTransaction(null, account3.getAddress()),
                account1.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                transferTransaction(account1.getPublicAccount(), account3.getAddress()),
                account3.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                transferTransaction(account2.getPublicAccount(), account3.getAddress()),
                account3.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(transferTransaction(null, account3.getAddress()),
                account3.getAddress()));

    }


    @Test
    public void shouldCheckMultisigAccountModificationTransaction() {
        Account account1 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Account account2 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Account account3 = Account.generateNewAccount(NetworkType.MIJIN_TEST);

        Assertions.assertTrue(listener
            .transactionFromAddress(
                multisigAccountModificationTransaction(account1.getPublicAccount(),
                    account3.getPublicAccount()),
                account1.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(
                multisigAccountModificationTransaction(account2.getPublicAccount(),

                    account3.getPublicAccount()),
                account1.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(
                multisigAccountModificationTransaction(null,
                    account3.getPublicAccount()),
                account1.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                multisigAccountModificationTransaction(account1.getPublicAccount(),

                    account3.getPublicAccount()),
                account3.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                multisigAccountModificationTransaction(account2.getPublicAccount(),

                    account3.getPublicAccount()),
                account3.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                multisigAccountModificationTransaction(null,
                    account3.getPublicAccount()),
                account3.getAddress()));
    }

    @Test
    public void shouldHandleStatus()
        throws InterruptedException, ExecutionException, TimeoutException {
        Account account1 = Account.generateNewAccount(NetworkType.MIJIN_TEST);

        AtomicReference<TransactionStatusError> reference = new AtomicReference<>();

        simulateWebSocketStartup();

        Assertions.assertNotNull(listener.status(account1.getAddress()).subscribe(reference::set));

        Map<String, Object> message = new HashMap<>();
        message.put("hash", "1234hash");
        message.put("address", account1.getAddress().encoded());
        message.put("code", "some error");
        message.put("deadline", 5555);
        listener.handle(message, null);

        Assertions.assertNotNull(reference.get());

        Assertions.assertEquals(message.get("hash"), reference.get().getHash());
        Assertions.assertEquals(message.get("code"), reference.get().getStatus());
        Assertions.assertEquals(account1.getAddress(), reference.get().getAddress());

        Mockito.verify(webSocketMock).handler(Mockito.any());

        Mockito.verify(webSocketMock).writeTextMessage(jsonHelper
            .print(new ListenerSubscribeMessage(this.wsId,
                "status" + "/" + account1.getAddress().plain())));


    }

    @Test
    public void shouldFilterOutHandleStatus()
        throws InterruptedException, ExecutionException, TimeoutException {
        Account account1 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Account account2 = Account.generateNewAccount(NetworkType.MIJIN_TEST);

        AtomicReference<TransactionStatusError> reference = new AtomicReference<>();

        simulateWebSocketStartup();

        Assertions.assertNotNull(listener.status(account2.getAddress()).subscribe(reference::set));

        Map<String, Object> message = new HashMap<>();
        message.put("hash", "1234hash");
        message.put("address", account1.getAddress().encoded());
        message.put("code", "some error");
        message.put("deadline", 5555);
        listener.handle(message, null);

        Assertions.assertNull(reference.get());

        Mockito.verify(webSocketMock).handler(Mockito.any());

        Mockito.verify(webSocketMock).writeTextMessage(jsonHelper
            .print(new ListenerSubscribeMessage(this.wsId,
                "status" + "/" + account2.getAddress().plain())));
    }

    @Test
    public void shouldCheckTransactionFromAddressHashLockTransaction() {
        Account account1 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Account account2 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Assertions.assertTrue(listener
            .transactionFromAddress(hashLockTransaction(account1.getPublicAccount()),
                account1.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(hashLockTransaction(account2.getPublicAccount()),
                account1.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(hashLockTransaction(null),
                account1.getAddress()));

    }

    private TransferTransaction transferTransaction(PublicAccount signer, Address recipient) {
        TransferTransactionFactory factory = TransferTransactionFactory
            .create(NetworkType.MIJIN_TEST,
                recipient,
                Collections.emptyList(),
                PlainMessage.Empty);
        if (signer != null) {
            factory.signer(signer);
        }
        return factory.build();
    }

    private MultisigAccountModificationTransaction multisigAccountModificationTransaction(
        PublicAccount signer,
        PublicAccount cosignatoryPublicAccount) {
        List<PublicAccount> additions = Collections.singletonList(cosignatoryPublicAccount);
        List<PublicAccount> deletions = Collections.singletonList(cosignatoryPublicAccount);

        MultisigAccountModificationTransactionFactory factory = MultisigAccountModificationTransactionFactory
            .create(NetworkType.MIJIN_TEST, (byte) 0, (byte) 0, additions, deletions);
        if (signer != null) {
            factory.signer(signer);
        }
        return factory.build();
    }

    @Test
    public void shouldCheckTransactionAggregateTransaction() {
        Account account1 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Account account2 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Account account3 = Account.generateNewAccount(NetworkType.MIJIN_TEST);
        Transaction anotherTransaction = hashLockTransaction(account2.getPublicAccount());
        Assertions.assertTrue(listener
            .transactionFromAddress(
                aggregateTransaction(account1.getPublicAccount(),
                    anotherTransaction, account3.getPublicAccount()),
                account1.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(
                aggregateTransaction(account2.getPublicAccount(),
                    anotherTransaction,
                    account3.getPublicAccount()),
                account1.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(
                aggregateTransaction(null, anotherTransaction,
                    account3.getPublicAccount()),
                account1.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                aggregateTransaction(account1.getPublicAccount(),
                    anotherTransaction,
                    account3.getPublicAccount()),
                account3.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                aggregateTransaction(account2.getPublicAccount(),
                    anotherTransaction,
                    account3.getPublicAccount()),
                account3.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                aggregateTransaction(null, anotherTransaction,
                    account3.getPublicAccount()),
                account3.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                aggregateTransaction(account1.getPublicAccount(),
                    anotherTransaction,
                    account3.getPublicAccount()),
                account3.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(
                aggregateTransaction(account1.getPublicAccount(),
                    anotherTransaction,
                    account2.getPublicAccount()),
                account3.getAddress()));

        Assertions.assertTrue(listener
            .transactionFromAddress(
                aggregateTransaction(account2.getPublicAccount(),
                    anotherTransaction,
                    account3.getPublicAccount()),
                account2.getAddress()));

        Assertions.assertFalse(listener
            .transactionFromAddress(
                aggregateTransaction(null,
                    anotherTransaction,
                    account2.getPublicAccount()),
                account3.getAddress()));
    }

    private AggregateTransaction aggregateTransaction(PublicAccount signer,
        Transaction anotherTransaction, PublicAccount consignauturePublicAccount) {
        List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
        cosignatures
            .add(new AggregateTransactionCosignature("Signature", consignauturePublicAccount));
        AggregateTransactionFactory factory = AggregateTransactionFactory.create(
            TransactionType.AGGREGATE_COMPLETE, NetworkType.MIJIN_TEST,
            Collections.singletonList(anotherTransaction),
            cosignatures);
        if (signer != null) {
            factory.signer(signer);
        }
        return factory.build();
    }


    private HashLockTransaction hashLockTransaction(PublicAccount signer) {
        SignedTransaction signedTransaction =
            new SignedTransaction(
                signer == null ? null : signer, "payload",
                "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
                TransactionType.AGGREGATE_BONDED);
        MosaicId mosaicId = MapperUtils.toMosaicId("123");
        Mosaic mosaic = new Mosaic(mosaicId, BigInteger.TEN);
        HashLockTransactionFactory factory = HashLockTransactionFactory.create(
            NetworkType.MIJIN_TEST, mosaic,
            BigInteger.TEN, signedTransaction.getHash());
        if (signer != null) {
            factory.signer(signer);
        }
        return factory.build();
    }

}
