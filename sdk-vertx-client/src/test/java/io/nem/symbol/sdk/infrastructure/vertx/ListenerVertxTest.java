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
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.infrastructure.ListenerChannel;
import io.nem.symbol.sdk.infrastructure.ListenerMessage;
import io.nem.symbol.sdk.infrastructure.ListenerRequest;
import io.nem.symbol.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionStatusError;
import io.nem.symbol.sdk.model.transaction.TransactionStatusException;
import io.nem.symbol.sdk.openapi.vertx.model.Cosignature;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionMetaDTO;
import io.reactivex.Observable;
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
  private MultisigRepository multisigRepository;

  @BeforeEach
  public void setUp() {
    httpClientMock = Mockito.mock(HttpClient.class);
    String url = "http://nem.com:3000/";
    namespaceRepository = Mockito.mock(NamespaceRepository.class);
    multisigRepository = Mockito.mock(MultisigRepository.class);
    listener =
        new ListenerVertx(
            httpClientMock,
            url,
            namespaceRepository,
            multisigRepository,
            Observable.just(NetworkType.MIJIN_TEST));
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
    BiFunction<UnresolvedAddress, String, Observable<? extends Transaction>> subscriber =
        channel == ListenerChannel.CONFIRMED_ADDED
            ? listener::confirmedOrError
            : listener::aggregateBondedAddedOrError;

    TransactionMetaDTO meta =
        jsonHelper.convert(transactionInfo.getMeta(), TransactionMetaDTO.class);
    subscriber.apply(address, meta.getHash()).doOnError(exceptions::add).forEach(transactions::add);

    handle(transactionInfoDtoJsonObject, channelName + "/" + address.plain());

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
  public void subscribeValidUsingBase(ListenerChannel channel)
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

    List<ListenerMessage<Transaction>> messages = new ArrayList<>();
    List<Throwable> exceptions = new ArrayList<>();

    TransactionMetaDTO meta =
        jsonHelper.convert(transactionInfo.getMeta(), TransactionMetaDTO.class);
    listener
        .subscribe(
            new ListenerRequest<Transaction>(channel, address)
                .transactionHashOrError(meta.getHash()))
        .doOnError(exceptions::add)
        .forEach(messages::add);

    handle(transactionInfoDtoJsonObject, channelName + "/" + address.plain());

    Assertions.assertEquals(1, messages.size());
    Assertions.assertEquals(0, exceptions.size());

    Assertions.assertEquals(meta.getHash(), messages.get(0).getTransactionHash());
    Assertions.assertEquals(channel, messages.get(0).getChannel());
    Assertions.assertEquals(address.plain(), messages.get(0).getChannelParams());
    Assertions.assertEquals(channelName + "/" + address.plain(), messages.get(0).getTopic());
    Assertions.assertEquals(address, messages.get(0).getMessage().getSigner().get().getAddress());

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

    handle(transactionInfoDtoJsonObject, channel.toString() + "/" + address.plain());

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

    List<Transaction> transactions = new ArrayList<>();

    BiFunction<UnresolvedAddress, String, Observable<? extends Transaction>> subscriber =
        channel == ListenerChannel.CONFIRMED_ADDED
            ? listener::confirmed
            : channel == ListenerChannel.UNCONFIRMED_ADDED
                ? listener::unconfirmedAdded
                : listener::aggregateBondedAdded;

    TransactionMetaDTO meta =
        jsonHelper.convert(transactionInfo.getMeta(), TransactionMetaDTO.class);
    subscriber.apply(namespaceId, meta.getHash()).forEach(transactions::add);

    handle(transactionInfoDtoJsonObject, channelName + "/" + namespaceId.plain());

    Assertions.assertEquals(1, transactions.size());

    Mockito.verify(webSocketMock).handler(Mockito.any());
    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + namespaceId.plain())));

    Mockito.verify(webSocketMock)
        .writeTextMessage(
            jsonHelper.print(
                new ListenerSubscribeMessage(this.wsId, channelName + "/" + namespaceId.plain())));
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

  private void handle(Object data, String topic) {
    Map<String, Object> map = new HashMap<>();
    map.put("data", data);
    map.put("topic", topic);
    listener.handle(map, null);
  }
}
