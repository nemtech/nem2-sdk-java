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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.MultisigRepository;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionStatusError;
import io.nem.symbol.sdk.model.transaction.TransactionStatusException;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Created by fernando on 19/08/19.
 *
 * @author Fernando Boucquez
 */
public abstract class ListenerBase implements Listener {

  private final Subject<ListenerMessage<?>> messageSubject = PublishSubject.create();

  private final JsonHelper jsonHelper;

  private final NamespaceRepository namespaceRepository;

  private final MultisigRepository multisigRepository;

  private final Observable<NetworkType> networkTypeObservable;

  private String uid;

  protected ListenerBase(
      JsonHelper jsonHelper,
      NamespaceRepository namespaceRepository,
      MultisigRepository multisigRepository,
      Observable<NetworkType> networkTypeObservable) {
    this.jsonHelper = jsonHelper;
    this.namespaceRepository = namespaceRepository;
    this.multisigRepository = multisigRepository;
    this.networkTypeObservable = networkTypeObservable;
  }

  /**
   * It knows how to handle a ws wsPayload coming from the server. Each subclass is responsible of
   * hooking the web socket implementation with this method.
   *
   * @param wsPayload the generic json with the wsPayload.
   * @param future to tell the user that the connection to the ws has been stabilised.
   */
  public void handle(Object wsPayload, CompletableFuture<Void> future) {
    if (jsonHelper.contains(wsPayload, "uid")) {
      uid = jsonHelper.getString(wsPayload, "uid");
      future.complete(null);
      return;
    }
    this.createListenerMessage(wsPayload).subscribe(getMessageSubject()::onNext);
  }

  /**
   * It create the ListenerMessage for the message subject.
   *
   * @param wsPayload the generic json with the wsPayload.
   */
  private Observable<ListenerMessage<?>> createListenerMessage(Object wsPayload) {
    String topic = jsonHelper.getString(wsPayload, "topic");
    Validate.notNull(topic, "Topic must be included in the WebSocket payload!");
    ListenerChannel channel = ListenerChannel.rawValueOf(StringUtils.substringBefore(topic, "/"));
    String channelParams = StringUtils.substringAfter(topic, "/");
    Object message = jsonHelper.getObject(wsPayload, "data");
    Validate.notNull(message, "Data must be included in the WebSocket payload!");
    switch (channel) {
      case CONFIRMED_ADDED:
      case UNCONFIRMED_ADDED:
      case AGGREGATE_BONDED_ADDED:
        final Transaction transaction = toTransaction(toGroup(channel), message);
        final String transactionHash = transaction.getTransactionInfo().get().getHash().get();
        return Observable.just(
            new ListenerMessage<>(topic, channel, channelParams, transaction, transactionHash));
      case BLOCK:
        return Observable.just(
            new ListenerMessage<>(topic, channel, channelParams, toBlockInfo(message), null));
      case FINALIZED_BLOCK:
        return Observable.just(
            new ListenerMessage<>(topic, channel, channelParams, toFinalizedBlock(message), null));
      case STATUS:
        final TransactionStatusError status = toStatus(message, channelParams);
        return Observable.just(
            new ListenerMessage<>(topic, channel, channelParams, status, status.getHash()));
      case COSIGNATURE:
        return networkTypeObservable.map(
            networkType -> {
              final CosignatureSignedTransaction cosignature =
                  toCosignatureSignedTransaction(message, networkType);
              return new ListenerMessage<>(
                  topic, channel, channelParams, cosignature, cosignature.getParentHash());
            });
      case AGGREGATE_BONDED_REMOVED:
      case UNCONFIRMED_REMOVED:
        final String hash = jsonHelper.getString(message, "meta", "hash");
        return Observable.just(new ListenerMessage<>(topic, channel, channelParams, hash, hash));
      default:
        throw new IllegalArgumentException("Channel " + channel + "is not supported.");
    }
  }

  /**
   * Subclasses are in charge of creating the finalized blocked model object
   *
   * @param message the payload
   * @return the finalized object
   */
  protected abstract FinalizedBlock toFinalizedBlock(Object message);

  private TransactionStatusError toStatus(Object message, String channelParams) {
    UnresolvedAddress unresolvedAddress = getUnresolvedAddress(channelParams);
    String hash = jsonHelper.getString(message, "hash");
    String code = jsonHelper.getString(message, "code");
    Deadline deadline = new Deadline(new BigInteger(jsonHelper.getString(message, "deadline")));
    return (new TransactionStatusError(unresolvedAddress, hash, code, deadline));
  }

  private TransactionGroup toGroup(ListenerChannel channel) {
    switch (channel) {
      case CONFIRMED_ADDED:
        return TransactionGroup.CONFIRMED;
      case AGGREGATE_BONDED_ADDED:
        return TransactionGroup.PARTIAL;
      case UNCONFIRMED_ADDED:
        return TransactionGroup.UNCONFIRMED;
    }
    throw new IllegalArgumentException(
        "Cannot map channel " + channel + " to a transaction group.");
  }

  @Override
  public Observable<BlockInfo> newBlock() {
    return this.subscribe(ListenerRequest.block()).map(ListenerMessage::getMessage);
  }

  @Override
  public Observable<FinalizedBlock> finalizedBlock() {
    return this.subscribe(ListenerRequest.finalizedBlock()).map(ListenerMessage::getMessage);
  }

  @Override
  public Observable<Transaction> confirmed(
      final UnresolvedAddress unresolvedAddress, final String transactionHash) {
    return this.subscribe(
            ListenerRequest.confirmed(unresolvedAddress).transactionHash(transactionHash))
        .map(ListenerMessage::getMessage);
  }

  @Override
  public Observable<Transaction> confirmedOrError(
      UnresolvedAddress unresolvedAddress, String transactionHash) {
    return this.subscribe(
            ListenerRequest.confirmed(unresolvedAddress).transactionHashOrError(transactionHash))
        .map(ListenerMessage::getMessage);
  }

  @Override
  public Observable<Transaction> unconfirmedAdded(
      UnresolvedAddress unresolvedAddress, String transactionHash) {
    return this.subscribe(
            ListenerRequest.unconfirmedAdded(unresolvedAddress).transactionHash(transactionHash))
        .map(ListenerMessage::getMessage);
  }

  @Override
  public Observable<String> unconfirmedRemoved(
      UnresolvedAddress unresolvedAddress, String transactionHash) {
    return this.subscribe(
            ListenerRequest.unconfirmedRemoved(unresolvedAddress).transactionHash(transactionHash))
        .map(ListenerMessage::getMessage);
  }

  @Override
  public Observable<AggregateTransaction> aggregateBondedAdded(
      UnresolvedAddress unresolvedAddress, String transactionHash) {
    return this.subscribe(
            ListenerRequest.aggregateBondedAdded(unresolvedAddress)
                .transactionHash(transactionHash))
        .map(ListenerMessage::getMessage);
  }

  @Override
  public Observable<String> aggregateBondedRemoved(
      UnresolvedAddress unresolvedAddress, String transactionHash) {
    return this.subscribe(
            ListenerRequest.aggregateBondedRemoved(unresolvedAddress)
                .transactionHash(transactionHash))
        .map(ListenerMessage::getMessage);
  }

  @Override
  public Observable<TransactionStatusError> status(
      UnresolvedAddress unresolvedAddress, String transactionHash) {
    return this.subscribe(
            ListenerRequest.status(unresolvedAddress).transactionHash(transactionHash))
        .map(ListenerMessage::getMessage);
  }

  @Override
  public Observable<CosignatureSignedTransaction> cosignatureAdded(
      UnresolvedAddress unresolvedAddress, String parentTransactionHash) {
    return this.subscribe(
            ListenerRequest.cosignature(unresolvedAddress).transactionHash(parentTransactionHash))
        .map(ListenerMessage::getMessage);
  }

  private void validateOpen() {
    if (getUid() == null) {
      throw new IllegalStateException(
          "Listener has not been opened yet. Please call the open method before subscribing.");
    }
  }

  @Override
  public Observable<AggregateTransaction> aggregateBondedAddedOrError(
      UnresolvedAddress unresolvedAddress, String transactionHash) {
    return this.subscribe(
            ListenerRequest.aggregateBondedAdded(unresolvedAddress)
                .transactionHashOrError(transactionHash))
        .map(ListenerMessage::getMessage);
  }

  private <T> Observable<ListenerMessage<T>> getMessageOrError(
      ListenerRequest<T> request, Observable<ListenerMessage<T>> transactionListener) {
    // I may move this method to the Listener
    Validate.notNull(request.getUnresolvedAddress(), "address is required");
    Validate.notNull(request.getTransactionHash(), "transactionHash is required");
    IllegalStateException caller = new IllegalStateException("The Caller");
    Observable<TransactionStatusError> errorListener =
        this.subscribe(
                ListenerRequest.status(request.getUnresolvedAddress())
                    .transactionHash(request.getTransactionHash()))
            .map(ListenerMessage::getMessage);
    Observable<Object> errorOrTransactionObservable =
        Observable.merge(transactionListener, errorListener).take(1);
    return errorOrTransactionObservable.map(
        errorOrTransaction -> {
          if (errorOrTransaction instanceof TransactionStatusError) {
            throw new TransactionStatusException(
                caller, (TransactionStatusError) errorOrTransaction);
          } else {
            //noinspection unchecked
            return (ListenerMessage<T>) errorOrTransaction;
          }
        });
  }

  @Override
  public <T> Observable<ListenerMessage<T>> subscribe(ListenerRequest<T> request) {
    final Observable<ListenerMessage<T>> transactionObservable = basicSubscribe(request);
    if (request.isOrError()) {
      return this.getMessageOrError(request, transactionObservable);
    } else {
      return transactionObservable;
    }
  }

  public <T> Observable<ListenerMessage<T>> basicSubscribe(ListenerRequest<T> request) {
    validateOpen();
    String topic = request.getTopic();
    String transactionHash = request.getTransactionHash();
    this.subscribeTo(topic);
    return getMessageSubject()
        .filter(rawMessage -> rawMessage.getTopic().equalsIgnoreCase(topic))
        .map(listenerMessage -> (ListenerMessage<T>) listenerMessage)
        .filter(
            rawMessage ->
                transactionHash == null
                    || transactionHash.equalsIgnoreCase(rawMessage.getTransactionHash()))
        .distinctUntilChanged(this::sameMessage);
  }

  private <T> boolean sameMessage(ListenerMessage<T> message1, ListenerMessage<T> message2) {
    // Could have different topic addresses (an alias an a real address).
    if (message1.getChannel() != message2.getChannel()) {
      return false;
    }
    if (message1.getTransactionHash() == null) {
      return false;
    }
    if (message1.getChannel() == ListenerChannel.COSIGNATURE) {
      return false;
    }
    return StringUtils.equalsIgnoreCase(
        message1.getTransactionHash(), message2.getTransactionHash());
  }

  private Observable<Address> getAddress(UnresolvedAddress unresolvedAddress) {
    if (unresolvedAddress instanceof Address) {
      return Observable.just((Address) unresolvedAddress);
    }
    return Observable.defer(
        () -> namespaceRepository.getLinkedAddress((NamespaceId) unresolvedAddress).cache());
  }

  private UnresolvedAddress getUnresolvedAddress(String channelParams) {
    return MapperUtils.toUnresolvedAddressFromPlain(channelParams);
  }

  @Override
  public Observable<Set<UnresolvedAddress>> getAllMultisigAddressesAndAliases(
      UnresolvedAddress unresolvedAddress) {
    return this.getAddress(unresolvedAddress)
        .flatMap(
            address ->
                this.multisigRepository
                    .getMultisigAccountInfo(address)
                    .map(
                        mutlisig -> {
                          Set<UnresolvedAddress> allUnresolvedAddresses =
                              new HashSet<>(mutlisig.getCosignatoryAddresses());
                          allUnresolvedAddresses.add(address);
                          allUnresolvedAddresses.add(unresolvedAddress);
                          return allUnresolvedAddresses;
                        })
                    .onErrorReturn((e) -> Collections.singleton(address)))
        .flatMap(
            addresses ->
                Observable.merge(
                        addresses.stream()
                            .map(this::getAllAddressesAndAliases)
                            .collect(Collectors.toList()))
                    .toList()
                    .map(
                        list ->
                            list.stream().flatMap(Collection::stream).collect(Collectors.toSet()))
                    .toObservable());
  }

  @Override
  public Observable<Set<UnresolvedAddress>> getAllAddressesAndAliases(
      UnresolvedAddress unresolvedAddress) {
    return this.getAddress(unresolvedAddress)
        .flatMap(
            address ->
                this.getNamespaceIds(address)
                    .map(
                        namespaces -> {
                          Set<UnresolvedAddress> allUnresolvedAddresses = new HashSet<>(namespaces);
                          allUnresolvedAddresses.add(address);
                          allUnresolvedAddresses.add(unresolvedAddress);
                          return allUnresolvedAddresses;
                        }));
  }

  @Override
  public <T> Observable<ListenerMessage<T>> subscribeMultipleAddresses(
      ListenerChannel channel,
      Set<UnresolvedAddress> unresolvedAddresses,
      String transactionHash,
      boolean orError) {
    return Observable.merge(
            unresolvedAddresses.stream()
                .map(
                    unresolvedAddress ->
                        this.<T>subscribe(
                            new ListenerRequest<T>(channel, unresolvedAddress)
                                .transactionHashOrError(transactionHash, orError)))
                .collect(Collectors.toList()))
        .distinctUntilChanged(this::sameMessage);
  }

  /**
   * Returns the namespaces ids for the given address.
   *
   * @param address the address
   * @return observable of namespace ids.
   */
  private Observable<List<NamespaceId>> getNamespaceIds(Address address) {
    return Observable.defer(
            () ->
                namespaceRepository
                    .getAccountsNames(Collections.singletonList(address))
                    .map(
                        accountNames ->
                            accountNames.stream()
                                .flatMap(
                                    accountName ->
                                        accountName.getNames().stream()
                                            .map(NamespaceName::getNamespaceId))
                                .collect(Collectors.toList())))
        .cache();
  }

  /**
   * Subclasses know how to map a generic blockInfoDTO json to a BlockInfo using the generated DTOs
   * of the implementation.
   *
   * @param blockInfoDTO the generic json
   * @return the model {@link BlockInfo}
   */
  protected abstract BlockInfo toBlockInfo(Object blockInfoDTO);

  /**
   * Subclasses know how to map a generic TransactionInfoDto json to a Transaction using the
   * generated DTOs of the implementation.
   *
   * @param group the group the transaction belongs
   * @param transactionInfo the generic json
   * @return the model {@link Transaction}
   */
  protected abstract Transaction toTransaction(TransactionGroup group, Object transactionInfo);

  /**
   * Subclasses know how to map a generic Consignature DTO json to a CosignatureSignedTransaction
   * using the generated DTOs of the implementation.
   *
   * @param cosignature the generic json
   * @param networkType networkType
   * @return the model {@link CosignatureSignedTransaction}
   */
  protected abstract CosignatureSignedTransaction toCosignatureSignedTransaction(
      Object cosignature, NetworkType networkType);

  protected abstract void subscribeTo(String channel);

  public Subject<ListenerMessage<?>> getMessageSubject() {
    return messageSubject;
  }

  public JsonHelper getJsonHelper() {
    return jsonHelper;
  }

  /** @return the UID connected to */
  @Override
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }
}
