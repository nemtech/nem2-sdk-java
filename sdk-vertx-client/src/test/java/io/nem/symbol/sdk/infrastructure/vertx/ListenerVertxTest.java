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

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.infrastructure.ListenerChannel;
import io.nem.symbol.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.AccountNames;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionStatusError;
import io.nem.symbol.sdk.model.transaction.TransactionStatusException;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.nem.symbol.sdk.model.transaction.VrfKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.VrfKeyLinkTransactionFactory;
import io.nem.symbol.sdk.openapi.vertx.model.Cosignature;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionMetaDTO;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferFactoryImpl;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/** Tests for the {@link ListenerVertx} implementation of the {@link Listener} */
public class ListenerVertxTest {

  public static final NetworkType NETWORK_TYPE = NetworkType.MIJIN_TEST;
  private final String wsId = "TheWSid";
  private ListenerVertx listener;
  private HttpClient httpClientMock;
  private WebSocket webSocketMock;
  private JsonHelper jsonHelper;
  private NamespaceRepository namespaceRepository;

  @BeforeEach
  public void setUp() {
    httpClientMock = Mockito.mock(HttpClient.class);
    String url = "http://nem.com:3000/";
    namespaceRepository = Mockito.mock(NamespaceRepository.class);
    listener =
        new ListenerVertx(
            httpClientMock, url, namespaceRepository, Observable.just(NetworkType.MIJIN_TEST));
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
        Assertions.assertThrows(IllegalStateException.class, () -> listener.newBlock())
            .getMessage());

    simulateWebSocketStartup();

    Assertions.assertNotNull(listener.newBlock());

    Assertions.assertEquals(wsId, listener.getUid());

    listener.close();
    listener.close();

    Assertions.assertNull(listener.getUid());

    Assertions.assertEquals(
        "Listener has not been opened yet. Please call the open method before subscribing.",
        Assertions.assertThrows(IllegalStateException.class, () -> listener.newBlock())
            .getMessage());

    Map<String, String> sendPayload = new HashMap<>();
    sendPayload.put("uid", wsId);
    sendPayload.put("subscribe", "block");

    Mockito.verify(webSocketMock).writeTextMessage(jsonHelper.print(sendPayload));
    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock).close();
  }

  @Test
  public void cosignatureAdded() throws InterruptedException, ExecutionException, TimeoutException {
    simulateWebSocketStartup();
    ListenerChannel consignature = ListenerChannel.COSIGNATURE;
    NetworkType networkType = NETWORK_TYPE;
    Cosignature cosignature =
        new Cosignature()
            .parentHash("aParentHash")
            .signature("aSignature")
            .version(BigInteger.ONE)
            .signerPublicKey(Account.generateNewAccount(networkType).getPublicKey());

    ObjectNode transactionInfoDtoJsonObject = jsonHelper.convert(cosignature, ObjectNode.class);

    Address address = Address.createFromPublicKey(cosignature.getSignerPublicKey(), networkType);

    String channelName = consignature.toString();

    List<CosignatureSignedTransaction> transactions = new ArrayList<>();
    listener.cosignatureAdded(address).forEach(transactions::add);

    handle(transactionInfoDtoJsonObject, consignature.toString() + "/" + address.plain());

    Assertions.assertEquals(1, transactions.size());

    Assertions.assertEquals(
        cosignature.getSignerPublicKey(), transactions.get(0).getSigner().getPublicKey().toHex());
    Assertions.assertEquals(cosignature.getParentHash(), transactions.get(0).getParentHash());
    Assertions.assertEquals(cosignature.getSignature(), transactions.get(0).getSignature());
    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));
  }

  @Test
  public void subscribeFinalizedBlock()
      throws ExecutionException, InterruptedException, TimeoutException {

    simulateWebSocketStartup();

    String channelName = ListenerChannel.FINALIZED_BLOCK.toString();

    FinalizedBlock finalizedBlock = new FinalizedBlock(1L, 2L, BigInteger.valueOf(3), "abc");

    ObjectNode transactionInfoDtoJsonObject = jsonHelper.convert(finalizedBlock, ObjectNode.class);

    List<FinalizedBlock> finalizedBlocks = new ArrayList<>();
    listener.finalizedBlock().forEach(finalizedBlocks::add);

    handle(transactionInfoDtoJsonObject, channelName);

    Assertions.assertEquals(1, finalizedBlocks.size());

    Assertions.assertEquals(1, finalizedBlocks.size());
    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(jsonHelper.print(new ListenerSubscribeMessage(this.wsId, channelName)));
  }

  @ParameterizedTest
  @ValueSource(strings = {"CONFIRMED_ADDED", "AGGREGATE_BONDED_ADDED"})
  public void subscribeValid(ListenerChannel channel)
      throws InterruptedException, ExecutionException, TimeoutException {
    simulateWebSocketStartup();

    TransactionInfoDTO transactionInfo =
        TestHelperVertx.loadTransactionInfoDTO("aggregateMosaicCreationTransaction.json");

    ObjectNode transactionInfoDtoJsonObject = jsonHelper.convert(transactionInfo, ObjectNode.class);

    Address address =
        Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NETWORK_TYPE);

    String channelName = channel.toString();

    List<Transaction> transactions = new ArrayList<>();
    List<Throwable> exceptions = new ArrayList<>();
    BiFunction<Address, String, Observable<? extends Transaction>> subscriber =
        channel == ListenerChannel.CONFIRMED_ADDED
            ? listener::confirmedOrError
            : listener::aggregateBondedAddedOrError;

    TransactionMetaDTO meta =
        jsonHelper.convert(transactionInfo.getMeta(), TransactionMetaDTO.class);
    subscriber.apply(address, meta.getHash()).doOnError(exceptions::add).forEach(transactions::add);

    handle(
        transactionInfoDtoJsonObject,
        channelName + "/" + Address.generateRandom(NETWORK_TYPE).plain());

    Assertions.assertEquals(1, transactions.size());
    Assertions.assertEquals(0, exceptions.size());

    Assertions.assertEquals(address, transactions.get(0).getSigner().get().getAddress());

    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, "status" + "/" + address.plain())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"CONFIRMED_ADDED", "AGGREGATE_BONDED_ADDED"})
  public void subscribeOnHash(ListenerChannel channel)
      throws InterruptedException, ExecutionException, TimeoutException {
    simulateWebSocketStartup();

    TransactionInfoDTO transactionInfo =
        TestHelperVertx.loadTransactionInfoDTO("aggregateMosaicCreationTransaction.json");

    ObjectNode transactionInfoDtoJsonObject = jsonHelper.convert(transactionInfo, ObjectNode.class);

    Address address =
        Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NETWORK_TYPE);

    String channelName = channel.toString();

    List<Transaction> transactions = new ArrayList<>();
    List<Throwable> exceptions = new ArrayList<>();

    BiFunction<Address, String, Observable<? extends Transaction>> subscriber =
        channel == ListenerChannel.CONFIRMED_ADDED
            ? listener::confirmedOrError
            : listener::aggregateBondedAddedOrError;

    TransactionMetaDTO meta =
        jsonHelper.convert(transactionInfo.getMeta(), TransactionMetaDTO.class);
    subscriber.apply(address, meta.getHash()).doOnError(exceptions::add).forEach(transactions::add);

    handle(
        transactionInfoDtoJsonObject,
        channel.toString() + "/" + Address.generateRandom(NETWORK_TYPE).plain());

    Assertions.assertEquals(1, transactions.size());
    Assertions.assertEquals(0, exceptions.size());

    Assertions.assertEquals(address, transactions.get(0).getSigner().get().getAddress());

    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));

    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, "status" + "/" + address.plain())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"CONFIRMED_ADDED", "AGGREGATE_BONDED_ADDED", "UNCONFIRMED_ADDED"})
  public void subscribeAlias(ListenerChannel channel)
      throws InterruptedException, ExecutionException, TimeoutException {
    String channelName = channel.toString();
    simulateWebSocketStartup();
    TransactionInfoDTO transactionInfo =
        TestHelperVertx.loadTransactionInfoDTO("transferEmptyMessage.json");

    NamespaceId namespaceId = NamespaceId.createFromName("alias");

    ObjectNode transactionInfoDtoJsonObject = jsonHelper.convert(transactionInfo, ObjectNode.class);

    ((ObjectNode) transactionInfoDtoJsonObject.get("transaction"))
        .put(
            "recipientAddress",
            ConvertUtils.toHex(
                SerializationUtils.toUnresolvedAddress(namespaceId, NETWORK_TYPE).serialize()));

    Address address = Address.generateRandom(NETWORK_TYPE);

    Mockito.when(
            namespaceRepository.getAccountsNames(Mockito.eq(Collections.singletonList(address))))
        .thenReturn(
            Observable.just(
                Collections.singletonList(
                    new AccountNames(
                        address,
                        Collections.singletonList(new NamespaceName(namespaceId, "alias"))))));

    List<Transaction> transactions = new ArrayList<>();

    BiFunction<Address, String, Observable<? extends Transaction>> subscriber =
        channel == ListenerChannel.CONFIRMED_ADDED
            ? listener::confirmed
            : channel == ListenerChannel.UNCONFIRMED_ADDED
                ? listener::unconfirmedAdded
                : listener::aggregateBondedAdded;

    TransactionMetaDTO meta =
        jsonHelper.convert(transactionInfo.getMeta(), TransactionMetaDTO.class);
    subscriber.apply(address, meta.getHash()).forEach(transactions::add);

    handle(
        transactionInfoDtoJsonObject,
        channelName + "/" + Address.generateRandom(NETWORK_TYPE).plain());

    Assertions.assertEquals(1, transactions.size());

    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));

    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AGGREGATE_BONDED_REMOVED", "UNCONFIRMED_REMOVED"})
  public void subscribeToHash(ListenerChannel channel)
      throws InterruptedException, ExecutionException, TimeoutException {
    String channelName = channel.toString();
    simulateWebSocketStartup();

    Address address = Address.generateRandom(NETWORK_TYPE);

    String hash = "someHash";
    List<String> hashes = new ArrayList<>();
    BiFunction<Address, String, Observable<String>> subscriber =
        channel == ListenerChannel.AGGREGATE_BONDED_REMOVED
            ? listener::aggregateBondedRemoved
            : listener::unconfirmedRemoved;

    subscriber.apply(address, hash).forEach(hashes::add);

    Map<String, Map<String, String>> message = new HashMap<>();
    Map<String, String> meta = new HashMap<>();
    meta.put("hash", hash);
    message.put("meta", meta);
    handle(message, channel.toString() + "/" + address.plain());

    Assertions.assertEquals(1, hashes.size());
    Assertions.assertEquals(hash, hashes.get(0));

    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));

    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AGGREGATE_BONDED_REMOVED", "UNCONFIRMED_REMOVED"})
  public void subscribeToHashIncorrectAddress(ListenerChannel channel)
      throws InterruptedException, ExecutionException, TimeoutException {
    String channelName = channel.toString();
    simulateWebSocketStartup();

    Address address = Address.generateRandom(NETWORK_TYPE);

    String hash = "someHash";
    List<String> hashes = new ArrayList<>();
    BiFunction<Address, String, Observable<String>> subscriber =
        channel == ListenerChannel.AGGREGATE_BONDED_REMOVED
            ? listener::aggregateBondedRemoved
            : listener::unconfirmedRemoved;

    subscriber.apply(address, hash).forEach(hashes::add);

    Map<String, Map<String, String>> message = new HashMap<>();
    Map<String, String> meta = new HashMap<>();
    meta.put("hash", hash);
    message.put("meta", meta);
    Address invalidAddress = Address.generateRandom(NETWORK_TYPE);
    handle(message, channel.toString() + "/" + invalidAddress.plain());

    Assertions.assertEquals(0, hashes.size());

    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));

    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"AGGREGATE_BONDED_REMOVED", "UNCONFIRMED_REMOVED"})
  public void subscribeToHashWhenInvalidHash(ListenerChannel channel)
      throws InterruptedException, ExecutionException, TimeoutException {
    String channelName = channel.toString();
    simulateWebSocketStartup();
    Address address = Address.generateRandom(NETWORK_TYPE);
    String hash = "someHash";
    List<String> hashes = new ArrayList<>();
    BiFunction<Address, String, Observable<String>> subscriber =
        channel == ListenerChannel.AGGREGATE_BONDED_REMOVED
            ? listener::aggregateBondedRemoved
            : listener::unconfirmedRemoved;
    subscriber.apply(address, hash).forEach(hashes::add);
    Map<String, Map<String, String>> message = new HashMap<>();
    Map<String, String> meta = new HashMap<>();
    meta.put("hash", "invalidHash");
    message.put("meta", meta);
    handle(message, channelName + "/" + address.plain());

    Assertions.assertEquals(0, hashes.size());

    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));

    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));
  }

  @ParameterizedTest
  @ValueSource(strings = {"CONFIRMED_ADDED", "AGGREGATE_BONDED_ADDED"})
  public void subscribeOnError(ListenerChannel channel)
      throws InterruptedException, ExecutionException, TimeoutException {
    simulateWebSocketStartup();
    TransactionInfoDTO transactionInfo =
        TestHelperVertx.loadTransactionInfoDTO("aggregateMosaicCreationTransaction.json");

    ObjectNode transactionInfoDtoJsonObject = jsonHelper.convert(transactionInfo, ObjectNode.class);

    Address address =
        Address.createFromPublicKey(
            jsonHelper.getString(transactionInfoDtoJsonObject, "transaction", "signerPublicKey"),
            NETWORK_TYPE);

    String channelName = channel.toString();

    Map<String, Object> transactionStatusError = new HashMap<>();
    transactionStatusError.put("address", address.encoded());
    transactionStatusError.put("code", "Fail 666");
    TransactionMetaDTO meta =
        jsonHelper.convert(transactionInfo.getMeta(), TransactionMetaDTO.class);
    transactionStatusError.put("hash", meta.getHash());
    transactionStatusError.put("deadline", 123);

    List<Transaction> transactions = new ArrayList<>();
    List<Throwable> exceptions = new ArrayList<>();

    BiFunction<Address, String, Observable<? extends Transaction>> subscriber =
        channel == ListenerChannel.CONFIRMED_ADDED
            ? listener::confirmedOrError
            : listener::aggregateBondedAddedOrError;

    subscriber.apply(address, meta.getHash()).doOnError(exceptions::add).forEach(transactions::add);

    handle(transactionStatusError, "status/" + address.plain());

    Assertions.assertEquals(0, transactions.size());
    Assertions.assertEquals(1, exceptions.size());
    Assertions.assertEquals(TransactionStatusException.class, exceptions.get(0).getClass());
    Assertions.assertEquals(
        "Fail 666 processing transaction " + meta.getHash(), exceptions.get(0).getMessage());

    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + address.plain())));

    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, "status" + "/" + address.plain())));
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

    Buffer event =
        new BufferFactoryImpl().buffer(jsonHelper.print(Collections.singletonMap("uid", wsId)));
    bufferHandler.handle(event);

    future.get(3, TimeUnit.SECONDS);
  }

  @Test
  public void shouldCheckTransactionFromAddressTransferTransaction() {
    Account account1 = Account.generateNewAccount(NETWORK_TYPE);
    Account account2 = Account.generateNewAccount(NETWORK_TYPE);
    Account account3 = Account.generateNewAccount(NETWORK_TYPE);

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            transferTransaction(account1.getPublicAccount(), account3.getAddress()),
            account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            transferTransaction(account2.getPublicAccount(), account3.getAddress()),
            account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener, transferTransaction(null, account3.getAddress()), account1.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            transferTransaction(account1.getPublicAccount(), account3.getAddress()),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            transferTransaction(account2.getPublicAccount(), account3.getAddress()),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener, transferTransaction(null, account3.getAddress()), account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            transferTransaction(account2.getPublicAccount(), NamespaceId.createFromName("alias")),
            account3.getAddress(),
            NamespaceId.createFromName("alias")));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            transferTransaction(account2.getPublicAccount(), NamespaceId.createFromName("alias")),
            account3.getAddress(),
            NamespaceId.createFromName("alias2")));
  }

  private boolean transactionFromAddress(
      ListenerVertx listener,
      Transaction transferTransaction,
      Address address,
      NamespaceId... aliases) {
    try {
      Callable<ObservableSource<? extends List<NamespaceId>>> restCall =
          () -> Observable.just(Arrays.asList(aliases));
      Observable<List<NamespaceId>> namespaceIdObservable = Observable.defer(restCall).cache();
      return listener
          .transactionFromAddress(transferTransaction, address, namespaceIdObservable)
          .toFuture()
          .get();
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  @Test
  public void shouldCheckMultisigAccountModificationTransaction() {
    Account account1 = Account.generateNewAccount(NETWORK_TYPE);
    Account account2 = Account.generateNewAccount(NETWORK_TYPE);
    Account account3 = Account.generateNewAccount(NETWORK_TYPE);

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            multisigAccountModificationTransaction(
                account1.getPublicAccount(), account3.getAddress()),
            account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            multisigAccountModificationTransaction(
                account2.getPublicAccount(), account3.getAddress()),
            account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            multisigAccountModificationTransaction(null, account3.getAddress()),
            account1.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            multisigAccountModificationTransaction(
                account1.getPublicAccount(), account3.getAddress()),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            multisigAccountModificationTransaction(
                account2.getPublicAccount(), account3.getAddress()),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            multisigAccountModificationTransaction(null, account3.getAddress()),
            account3.getAddress()));
  }

  @Test
  public void shouldHandleStatus()
      throws InterruptedException, ExecutionException, TimeoutException {
    Account account1 = Account.generateNewAccount(NETWORK_TYPE);

    AtomicReference<TransactionStatusError> reference = new AtomicReference<>();

    simulateWebSocketStartup();

    Assertions.assertNotNull(listener.status(account1.getAddress()).subscribe(reference::set));

    Map<String, Object> message = new HashMap<>();
    message.put("hash", "1234hash");
    message.put("address", account1.getAddress().encoded());
    message.put("code", "some error");
    message.put("deadline", 5555);
    handle(message, "status/" + account1.getAddress().plain());

    Assertions.assertNotNull(reference.get());

    Assertions.assertEquals(message.get("hash"), reference.get().getHash());
    Assertions.assertEquals(message.get("code"), reference.get().getStatus());
    Assertions.assertEquals(account1.getAddress(), reference.get().getAddress());

    Mockito.verify(webSocketMock).handler(Mockito.any());

    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(
                    this.wsId, "status" + "/" + account1.getAddress().plain())));
  }

  @Test
  public void shouldFilterOutHandleStatus()
      throws InterruptedException, ExecutionException, TimeoutException {
    Account account1 = Account.generateNewAccount(NETWORK_TYPE);
    Account account2 = Account.generateNewAccount(NETWORK_TYPE);

    AtomicReference<TransactionStatusError> reference = new AtomicReference<>();

    simulateWebSocketStartup();

    Assertions.assertNotNull(listener.status(account2.getAddress()).subscribe(reference::set));

    Map<String, Object> message = new HashMap<>();
    message.put("hash", "1234hash");
    message.put("address", account1.getAddress().encoded());
    message.put("code", "some error");
    message.put("deadline", 5555);
    handle(message, "status/" + account1.getAddress().plain());

    Assertions.assertNull(reference.get());

    Mockito.verify(webSocketMock).handler(Mockito.any());

    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(
                    this.wsId, "status" + "/" + account2.getAddress().plain())));
  }

  @Test
  public void shouldCheckTransactionFromAddressHashLockTransaction() {
    Account account1 = Account.generateNewAccount(NETWORK_TYPE);
    Account account2 = Account.generateNewAccount(NETWORK_TYPE);
    Assertions.assertTrue(
        transactionFromAddress(
            listener, hashLockTransaction(account1.getPublicAccount()), account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener, hashLockTransaction(account2.getPublicAccount()), account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(listener, hashLockTransaction(null), account1.getAddress()));
  }

  private TransferTransaction transferTransaction(
      PublicAccount signer, UnresolvedAddress recipient) {
    TransferTransactionFactory factory =
        TransferTransactionFactory.create(NETWORK_TYPE, recipient, Collections.emptyList())
            .message(new PlainMessage(""));
    if (signer != null) {
      factory.signer(signer);
    }
    return factory.build();
  }

  @Test
  public void shouldCheckVrfKeyLinkTransaction() {
    Account account1 = Account.generateNewAccount(NETWORK_TYPE);
    Account account2 = Account.generateNewAccount(NETWORK_TYPE);
    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            vrfKeyLinkTransaction(
                account2.getPublicAccount(), account1.getPublicAccount().getPublicKey()),
            account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            vrfKeyLinkTransaction(
                account2.getPublicAccount(), account2.getPublicAccount().getPublicKey()),
            account1.getAddress()));
  }

  private VrfKeyLinkTransaction vrfKeyLinkTransaction(
      PublicAccount signer, PublicKey linkedAccount) {
    VrfKeyLinkTransactionFactory factory =
        VrfKeyLinkTransactionFactory.create(NETWORK_TYPE, linkedAccount, LinkAction.LINK);
    if (signer != null) {
      factory.signer(signer);
    }
    return factory.build();
  }

  @Test
  public void shouldCheckAccountMetadataTransaction() {
    Account account1 = Account.generateNewAccount(NETWORK_TYPE);
    Account account2 = Account.generateNewAccount(NETWORK_TYPE);
    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            accountMetadataTransaction(account2.getPublicAccount(), account1.getAddress()),
            account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            accountMetadataTransaction(account2.getPublicAccount(), account2.getAddress()),
            account1.getAddress()));
  }

  private AccountMetadataTransaction accountMetadataTransaction(
      PublicAccount signer, Address targetAccount) {
    AccountMetadataTransactionFactory factory =
        AccountMetadataTransactionFactory.create(
            NETWORK_TYPE, targetAccount, BigInteger.ONE, "someValue");
    if (signer != null) {
      factory.signer(signer);
    }
    return factory.build();
  }

  private MultisigAccountModificationTransaction multisigAccountModificationTransaction(
      PublicAccount signer, UnresolvedAddress cosignatoryPublicAccount) {
    List<UnresolvedAddress> additions = Collections.singletonList(cosignatoryPublicAccount);
    List<UnresolvedAddress> deletions = Collections.singletonList(cosignatoryPublicAccount);

    MultisigAccountModificationTransactionFactory factory =
        MultisigAccountModificationTransactionFactory.create(
            NETWORK_TYPE, (byte) 0, (byte) 0, additions, deletions);
    if (signer != null) {
      factory.signer(signer);
    }
    return factory.build();
  }

  @Test
  public void shouldAccountAddressRestrictionTransaction() {
    Account account1 = Account.generateNewAccount(NETWORK_TYPE);
    Account account2 = Account.generateNewAccount(NETWORK_TYPE);
    Account account3 = Account.generateNewAccount(NETWORK_TYPE);
    Account account4 = Account.generateNewAccount(NETWORK_TYPE);
    NamespaceId alias1 = NamespaceId.createFromName("alias1");
    NamespaceId alias2 = NamespaceId.createFromName("alias2");

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            accountAddressRestrictionTransaction(
                account1.getPublicAccount(), account2.getAddress(), account3.getAddress()),
            account1.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            accountAddressRestrictionTransaction(
                account1.getPublicAccount(), account2.getAddress(), account3.getAddress()),
            account2.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            accountAddressRestrictionTransaction(
                account1.getPublicAccount(), account2.getAddress(), account3.getAddress()),
            account3.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            accountAddressRestrictionTransaction(
                account1.getPublicAccount(), account2.getAddress(), account3.getAddress()),
            account4.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            accountAddressRestrictionTransaction(
                account1.getPublicAccount(), alias1, account3.getAddress()),
            account2.getAddress(),
            alias1));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            accountAddressRestrictionTransaction(
                account1.getPublicAccount(), account2.getAddress(), alias1),
            account3.getAddress(),
            alias1));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            accountAddressRestrictionTransaction(
                account1.getPublicAccount(), alias1, account3.getAddress()),
            account2.getAddress(),
            alias2));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            accountAddressRestrictionTransaction(
                account1.getPublicAccount(), account2.getAddress(), alias1),
            account3.getAddress(),
            alias2));
  }

  @Test
  public void shouldCheckTransactionAggregateTransaction() {

    Account account1 = Account.generateNewAccount(NETWORK_TYPE);
    Account account2 = Account.generateNewAccount(NETWORK_TYPE);
    Account account3 = Account.generateNewAccount(NETWORK_TYPE);
    Account account4 = Account.generateNewAccount(NETWORK_TYPE);
    NamespaceId alias1 = NamespaceId.createFromName("alias1");
    NamespaceId alias2 = NamespaceId.createFromName("alias2");
    Transaction hashLockTransaction = hashLockTransaction(account2.getPublicAccount());
    Transaction transferTransaction1 = transferTransaction(account2.getPublicAccount(), alias1);
    Transaction transferTransaction2 = transferTransaction(account2.getPublicAccount(), alias2);

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                account1.getPublicAccount(), account3.getPublicAccount(), hashLockTransaction),
            account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                account2.getPublicAccount(), account3.getPublicAccount(), hashLockTransaction),
            account1.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            aggregateTransaction(null, account3.getPublicAccount(), hashLockTransaction),
            account1.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                account1.getPublicAccount(), account3.getPublicAccount(), hashLockTransaction),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                account2.getPublicAccount(), account3.getPublicAccount(), hashLockTransaction),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(null, account3.getPublicAccount(), hashLockTransaction),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                account1.getPublicAccount(), account3.getPublicAccount(), hashLockTransaction),
            account3.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                account1.getPublicAccount(), account2.getPublicAccount(), hashLockTransaction),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                account2.getPublicAccount(), account3.getPublicAccount(), hashLockTransaction),
            account2.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            aggregateTransaction(null, account2.getPublicAccount(), hashLockTransaction),
            account3.getAddress()));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                null,
                account2.getPublicAccount(),
                hashLockTransaction,
                transferTransaction1,
                transferTransaction2),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                null,
                account2.getPublicAccount(),
                hashLockTransaction,
                transferTransaction1,
                transferTransaction2),
            account3.getAddress(),
            alias2));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                null, account2.getPublicAccount(), hashLockTransaction, transferTransaction2),
            account3.getAddress(),
            alias2));

    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                null, account2.getPublicAccount(), hashLockTransaction, transferTransaction1),
            account3.getAddress(),
            alias2));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                account2.getPublicAccount(),
                account4.getPublicAccount(),
                transferTransaction2,
                hashLockTransaction,
                transferTransaction1),
            account2.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                account1.getPublicAccount(),
                account1.getPublicAccount(),
                transferTransaction2,
                hashLockTransaction,
                transferTransaction1),
            account2.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            aggregateTransaction(
                null,
                account2.getPublicAccount(),
                hashLockTransaction,
                transferTransaction1,
                transferTransaction1,
                transferTransaction1,
                transferTransaction2,
                transferTransaction1),
            account3.getAddress(),
            alias2));
  }

  private AggregateTransaction aggregateTransaction(
      PublicAccount signer,
      PublicAccount consignauturePublicAccount,
      Transaction... anotherTransactions) {
    List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
    BigInteger version = AggregateTransactionCosignature.DEFAULT_VERSION;
    cosignatures.add(
        new AggregateTransactionCosignature(version, "Signature", consignauturePublicAccount));
    AggregateTransactionFactory factory =
        AggregateTransactionFactory.create(
            TransactionType.AGGREGATE_COMPLETE,
            NETWORK_TYPE,
            Arrays.asList(anotherTransactions),
            cosignatures);
    if (signer != null) {
      factory.signer(signer);
    }
    return factory.build();
  }

  private HashLockTransaction hashLockTransaction(PublicAccount signer) {
    SignedTransaction signedTransaction =
        new SignedTransaction(
            signer,
            "payload",
            "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
            TransactionType.AGGREGATE_BONDED);
    MosaicId mosaicId = MapperUtils.toMosaicId("123");
    Mosaic mosaic = new Mosaic(mosaicId, BigInteger.TEN);
    HashLockTransactionFactory factory =
        HashLockTransactionFactory.create(
            NETWORK_TYPE, mosaic, BigInteger.TEN, signedTransaction.getHash());
    if (signer != null) {
      factory.signer(signer);
    }
    return factory.build();
  }

  @Test
  public void shouldMosaicAddressRestriction() {
    Account account1 = Account.generateNewAccount(NETWORK_TYPE);
    Account account2 = Account.generateNewAccount(NETWORK_TYPE);
    Account account3 = Account.generateNewAccount(NETWORK_TYPE);
    NamespaceId alias1 = NamespaceId.createFromName("alias1");
    NamespaceId alias2 = NamespaceId.createFromName("alias2");

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            mosaicAddressRestriction(account1.getPublicAccount(), account2.getAddress()),
            account1.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            mosaicAddressRestriction(account1.getPublicAccount(), account2.getAddress()),
            account2.getAddress()));
    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            mosaicAddressRestriction(account1.getPublicAccount(), account2.getAddress()),
            account3.getAddress()));

    Assertions.assertTrue(
        transactionFromAddress(
            listener,
            mosaicAddressRestriction(account1.getPublicAccount(), alias2),
            account3.getAddress(),
            alias2));
    Assertions.assertFalse(
        transactionFromAddress(
            listener,
            mosaicAddressRestriction(account1.getPublicAccount(), alias2),
            account3.getAddress(),
            alias1));
  }

  private AccountAddressRestrictionTransaction accountAddressRestrictionTransaction(
      PublicAccount signer, UnresolvedAddress addition, UnresolvedAddress deletion) {
    List<UnresolvedAddress> additions = Collections.singletonList(addition);
    List<UnresolvedAddress> deletions = Collections.singletonList(deletion);

    AccountAddressRestrictionTransactionFactory factory =
        AccountAddressRestrictionTransactionFactory.create(
            NETWORK_TYPE,
            AccountAddressRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            additions,
            deletions);
    if (signer != null) {
      factory.signer(signer);
    }
    return factory.build();
  }

  private MosaicAddressRestrictionTransaction mosaicAddressRestriction(
      PublicAccount signer, UnresolvedAddress targetAddress) {

    MosaicAddressRestrictionTransactionFactory factory =
        MosaicAddressRestrictionTransactionFactory.create(
            NETWORK_TYPE,
            NamespaceId.createFromName("abc"),
            BigInteger.ONE,
            targetAddress,
            BigInteger.TEN);
    if (signer != null) {
      factory.signer(signer);
    }
    return factory.build();
  }

  private void handle(Object data, String topic) {
    Map<String, Object> map = new HashMap<>();
    map.put("data", data);
    map.put("topic", topic);
    listener.handle(map, null);
  }
}
