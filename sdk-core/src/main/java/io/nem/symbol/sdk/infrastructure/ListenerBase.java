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

import io.nem.symbol.sdk.api.Listener;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceName;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MetadataTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.PublicKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.RecipientTransaction;
import io.nem.symbol.sdk.model.transaction.TargetAddressTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionStatusError;
import io.nem.symbol.sdk.model.transaction.TransactionStatusException;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
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

  private final Subject<ListenerMessage> messageSubject = PublishSubject.create();

  private final JsonHelper jsonHelper;

  private final NamespaceRepository namespaceRepository;

  private final Observable<NetworkType> networkTypeObservable;

  private String uid;

  protected ListenerBase(
      JsonHelper jsonHelper,
      NamespaceRepository namespaceRepository,
      Observable<NetworkType> networkTypeObservable) {
    this.jsonHelper = jsonHelper;
    this.namespaceRepository = namespaceRepository;
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
        onNext(channel, channelParams, toTransaction(toGroup(channel), message));
        break;
      case BLOCK:
        onNext(channel, channelParams, toBlockInfo(message));
        break;
      case FINALIZED_BLOCK:
        onNext(channel, channelParams, toFinalizedBlock(message));
        break;
      case STATUS:
        onNext(channel, channelParams, toStatus(message, channelParams));
        break;
      case COSIGNATURE:
        networkTypeObservable.subscribe(
            networkType ->
                onNext(
                    channel, channelParams, toCosignatureSignedTransaction(message, networkType)));
        break;
      case AGGREGATE_BONDED_REMOVED:
      case UNCONFIRMED_REMOVED:
        onNext(channel, channelParams, jsonHelper.getString(message, "meta", "hash"));
        break;
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
    Address address = Address.createFromRawAddress(channelParams);
    String hash = jsonHelper.getString(message, "hash");
    String code = jsonHelper.getString(message, "code");
    Deadline deadline = new Deadline(new BigInteger(jsonHelper.getString(message, "deadline")));
    return new TransactionStatusError(address, hash, code, deadline);
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
    validateOpen();
    this.subscribeTo(ListenerChannel.BLOCK.toString());
    return getMessageSubject()
        .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.BLOCK))
        .map(rawMessage -> (BlockInfo) rawMessage.getMessage());
  }

  @Override
  public Observable<FinalizedBlock> finalizedBlock() {
    validateOpen();
    this.subscribeTo(ListenerChannel.FINALIZED_BLOCK.toString());
    return getMessageSubject()
        .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.FINALIZED_BLOCK))
        .map(rawMessage -> (FinalizedBlock) rawMessage.getMessage());
  }

  @Override
  public Observable<Transaction> confirmed(final Address address, final String transactionHash) {
    return subscribeTransaction(ListenerChannel.CONFIRMED_ADDED, address, transactionHash);
  }

  @Override
  public Observable<Transaction> confirmedOrError(Address address, String transactionHash) {
    // I may move this method to the Listener
    Validate.notNull(transactionHash, "TransactionHash is required");
    return getTransactionOrRaiseError(
        address, transactionHash, confirmed(address, transactionHash));
  }

  @Override
  public Observable<Transaction> unconfirmedAdded(Address address, String transactionHash) {
    return subscribeTransaction(ListenerChannel.UNCONFIRMED_ADDED, address, transactionHash);
  }

  @Override
  public Observable<String> unconfirmedRemoved(Address address, String transactionHash) {
    return subscribeTransactionHash(ListenerChannel.UNCONFIRMED_REMOVED, address, transactionHash);
  }

  @Override
  public Observable<AggregateTransaction> aggregateBondedAdded(
      Address address, String transactionHash) {
    return subscribeTransaction(ListenerChannel.AGGREGATE_BONDED_ADDED, address, transactionHash);
  }

  @Override
  public Observable<String> aggregateBondedRemoved(Address address, String transactionHash) {
    return subscribeTransactionHash(
        ListenerChannel.AGGREGATE_BONDED_REMOVED, address, transactionHash);
  }

  @Override
  public Observable<TransactionStatusError> status(Address address, String transactionHash) {
    Validate.notNull(address, "Address is required");
    validateOpen();
    this.subscribeTo(ListenerChannel.STATUS + "/" + address.plain());
    return getMessageSubject()
        .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.STATUS))
        .filter(rawMessage -> fromAddress(rawMessage, address))
        .map(rawMessage -> (TransactionStatusError) rawMessage.getMessage())
        .filter(
            status ->
                transactionHash == null || transactionHash.equalsIgnoreCase(status.getHash()));
  }

  @Override
  public Observable<CosignatureSignedTransaction> cosignatureAdded(
      Address address, String parentTransactionHash) {
    Validate.notNull(address, "Address is required");
    validateOpen();
    ListenerChannel channel = ListenerChannel.COSIGNATURE;
    this.subscribeTo(channel + "/" + address.plain());
    return getMessageSubject()
        .filter(rawMessage -> rawMessage.getChannel().equals(channel))
        .filter(rawMessage -> fromAddress(rawMessage, address))
        .map(rawMessage -> (CosignatureSignedTransaction) rawMessage.getMessage())
        .filter(
            status ->
                parentTransactionHash == null
                    || parentTransactionHash.equalsIgnoreCase(status.getParentHash()));
  }

  private void validateOpen() {
    if (getUid() == null) {
      throw new IllegalStateException(
          "Listener has not been opened yet. Please call the open method before subscribing.");
    }
  }

  @Override
  public Observable<AggregateTransaction> aggregateBondedAddedOrError(
      Address address, String transactionHash) {
    return getTransactionOrRaiseError(
        address, transactionHash, aggregateBondedAdded(address, transactionHash));
  }

  private <T extends Transaction> Observable<T> getTransactionOrRaiseError(
      Address address, String transactionHash, Observable<T> transactionListener) {
    // I may move this method to the Listener
    IllegalStateException caller = new IllegalStateException("The Caller");
    Observable<TransactionStatusError> errorListener = status(address, transactionHash);
    Observable<Object> errorOrTransactionObservable =
        Observable.merge(transactionListener, errorListener).take(1);
    return errorOrTransactionObservable.map(
        errorOrTransaction -> {
          if (errorOrTransaction instanceof TransactionStatusError) {
            throw new TransactionStatusException(
                caller, (TransactionStatusError) errorOrTransaction);
          } else {
            //noinspection unchecked
            return (T) errorOrTransaction;
          }
        });
  }

  private <T extends Transaction> Observable<T> subscribeTransaction(
      ListenerChannel channel, Address address, String transactionHash) {
    Validate.notNull(address, "Address is required");
    validateOpen();
    this.subscribeTo(channel.toString() + "/" + address.plain());
    return getMessageSubject()
        .filter(rawMessage -> rawMessage.getChannel().equals(channel))
        .flatMap(
            rawMessage -> this.processTransactionFromMessage(rawMessage, address, transactionHash));
  }

  private <T extends Transaction> Observable<T> processTransactionFromMessage(
      ListenerMessage rawMessage, Address address, String transactionHash) {
    @SuppressWarnings("unchecked")
    T transaction = (T) rawMessage.getMessage();

    if (transactionHash != null
        && !transaction
            .getTransactionInfo()
            .filter(info -> info.getHash().filter(transactionHash::equalsIgnoreCase).isPresent())
            .isPresent()) {
      return Observable.empty();
    }
    if (fromAddress(rawMessage, address)) {
      return Observable.just(transaction);
    }
    return this.transactionFromAddress(transaction, address, getNamespaceIds(address))
        .flatMap(include -> include ? Observable.just(transaction) : Observable.empty());
  }

  private Observable<String> subscribeTransactionHash(
      ListenerChannel channel, Address address, String transactionHash) {
    Validate.notNull(address, "Address is required");
    validateOpen();
    this.subscribeTo(channel + "/" + address.plain());
    return getMessageSubject()
        .filter(rawMessage -> rawMessage.getChannel().equals(channel))
        .filter(rawMessage -> fromAddress(rawMessage, address))
        .map(rawMessage -> (String) rawMessage.getMessage())
        .filter(hash -> transactionHash == null || transactionHash.equalsIgnoreCase(hash));
  }

  private boolean fromAddress(ListenerMessage rawMessage, Address address) {
    try {
      return address.equals(Address.createFromRawAddress(rawMessage.getChannelParams()));
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  public Observable<Boolean> transactionFromAddress(
      final Transaction transaction,
      final Address address,
      final Observable<List<NamespaceId>> namespaceIdsObservable) {
    if (transaction.getSigner().filter(s -> s.getAddress().equals(address)).isPresent()) {
      return Observable.just(true);
    }
    if (transaction instanceof AggregateTransaction) {
      final AggregateTransaction aggregateTransaction = (AggregateTransaction) transaction;
      if (aggregateTransaction.getCosignatures().stream()
          .anyMatch(c -> c.getSigner().getAddress().equals(address))) {
        return Observable.just(true);
      }
      // Recursion...
      Observable<Transaction> innerTransactionObservable =
          Observable.fromIterable(aggregateTransaction.getInnerTransactions());

      return innerTransactionObservable
          .flatMap(
              t -> this.transactionFromAddress(t, address, namespaceIdsObservable).filter(a -> a))
          .first(false)
          .toObservable();
    }
    if (transaction instanceof PublicKeyLinkTransaction) {
      return Observable.just(
          Address.createFromPublicKey(
                  ((PublicKeyLinkTransaction) transaction).getLinkedPublicKey().toHex(),
                  transaction.getNetworkType())
              .equals(address));
    }

    if (transaction instanceof MetadataTransaction) {
      MetadataTransaction metadataTransaction = (MetadataTransaction) transaction;
      return Observable.just(metadataTransaction.getTargetAddress().equals(address));
    }

    if (transaction instanceof TargetAddressTransaction) {
      TargetAddressTransaction targetAddressTransaction = (TargetAddressTransaction) transaction;
      if (targetAddressTransaction.getTargetAddress() instanceof Address) {
        return Observable.just(targetAddressTransaction.getTargetAddress().equals(address));
      }
      return namespaceIdsObservable.map(
          namespaceIds -> namespaceIds.contains(targetAddressTransaction.getTargetAddress()));
    }

    if (transaction instanceof MultisigAccountModificationTransaction) {
      MultisigAccountModificationTransaction multisigAccountModificationTransaction =
          (MultisigAccountModificationTransaction) transaction;
      if (multisigAccountModificationTransaction.getAddressAdditions().stream()
          .anyMatch(a -> a.equals(address))) {
        return Observable.just(true);
      }

      return Observable.just(
          multisigAccountModificationTransaction.getAddressDeletions().stream()
              .anyMatch(a -> a.equals(address)));
    }

    if (transaction instanceof AccountAddressRestrictionTransaction) {
      AccountAddressRestrictionTransaction accountAddressRestrictionTransaction =
          (AccountAddressRestrictionTransaction) transaction;
      if (accountAddressRestrictionTransaction.getRestrictionAdditions().contains(address)) {
        return Observable.just(true);
      }
      if (accountAddressRestrictionTransaction.getRestrictionDeletions().contains(address)) {
        return Observable.just(true);
      }
      return namespaceIdsObservable.flatMap(
          namespaceIds -> {
            if (namespaceIds.stream()
                .anyMatch(
                    namespaceId ->
                        accountAddressRestrictionTransaction
                            .getRestrictionAdditions()
                            .contains(namespaceId))) {
              return Observable.just(true);
            }
            if (namespaceIds.stream()
                .anyMatch(
                    namespaceId ->
                        accountAddressRestrictionTransaction
                            .getRestrictionDeletions()
                            .contains(namespaceId))) {
              return Observable.just(true);
            }
            return Observable.just(false);
          });
    }

    if (transaction instanceof RecipientTransaction) {
      RecipientTransaction recipientTransaction = (RecipientTransaction) transaction;
      if (recipientTransaction.getRecipient() instanceof NamespaceId) {
        return namespaceIdsObservable.map(
            namespaceIds -> namespaceIds.contains(recipientTransaction.getRecipient()));
      }
      return Observable.just(recipientTransaction.getRecipient().equals(address));
    }

    return Observable.just(false);
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
   * I fires the new message object to the subject listenrs.
   *
   * @param channel the channel
   * @param channelParams the topic param.
   * @param messageObject the message object.
   */
  private void onNext(ListenerChannel channel, String channelParams, Object messageObject) {
    this.getMessageSubject().onNext(new ListenerMessage(channel, channelParams, messageObject));
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

  public Subject<ListenerMessage> getMessageSubject() {
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
