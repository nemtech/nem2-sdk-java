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
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionStatusError;
import io.nem.symbol.sdk.model.transaction.TransactionStatusException;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
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

    private String uid;

    protected ListenerBase(JsonHelper jsonHelper,
        NamespaceRepository namespaceRepository) {
        this.jsonHelper = jsonHelper;
        this.namespaceRepository = namespaceRepository;
    }

    /**
     * It knows how to handle a ws message coming from the server. Each subclass is responsible of
     * hooking the web socket implementation with this method.
     *
     * @param message the generic json with the message.
     * @param future to tell the user that the connection to the ws has been stabilised.
     */
    public void handle(Object message, CompletableFuture<Void> future) {
        if (jsonHelper.contains(message, "uid")) {
            uid = jsonHelper.getString(message, "uid");
            future.complete(null);
        } else if (jsonHelper.contains(message, "transaction")) {
            Transaction messageObject = toTransaction(message);
            ListenerChannel channel = ListenerChannel
                .rawValueOf(jsonHelper.getString(message, "meta", "channelName"));
            onNext(channel, messageObject);
        } else if (jsonHelper.contains(message, "block")) {
            BlockInfo messageObject = toBlockInfo(message);
            onNext(ListenerChannel.BLOCK, messageObject);
        } else if (jsonHelper.contains(message, "code")) {
            TransactionStatusError messageObject = new TransactionStatusError(
                MapperUtils
                    .toAddressFromEncoded(jsonHelper.getString(message, "address")),
                jsonHelper.getString(message, "hash"),
                jsonHelper.getString(message, "code"),
                new Deadline(
                    new BigInteger(jsonHelper.getString(message, "deadline"))));
            onNext(ListenerChannel.STATUS, messageObject);
        } else if (jsonHelper.contains(message, "parentHash")) {
            CosignatureSignedTransaction messageObject = toCosignatureSignedTransaction(message);
            onNext(ListenerChannel.COSIGNATURE, messageObject);
        } else if (jsonHelper.contains(message, "meta")) {
            onNext(ListenerChannel.rawValueOf(
                jsonHelper.getString(message, "meta", "channelName")),
                jsonHelper.getString(message, "meta", "hash"));
        }
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
    public Observable<Transaction> confirmed(final Address address, final String transactionHash) {
        return subscribeTransaction(ListenerChannel.CONFIRMED_ADDED, address, transactionHash);
    }

    @Override
    public Observable<Transaction> confirmedOrError(Address address, String transactionHash) {
        // I may move this method to the Listener
        Validate.notNull(transactionHash, "TransactionHash is required");
        return getTransactionOrRaiseError(address, transactionHash,
            confirmed(address, transactionHash));
    }

    @Override
    public Observable<Transaction> unconfirmedAdded(Address address, String transactionHash) {
        return subscribeTransaction(ListenerChannel.UNCONFIRMED_ADDED, address, transactionHash);
    }

    @Override
    public Observable<String> unconfirmedRemoved(Address address, String transactionHash) {
        return subscribeTransactionHash(ListenerChannel.UNCONFIRMED_REMOVED, address,
            transactionHash);
    }

    @Override
    public Observable<AggregateTransaction> aggregateBondedAdded(Address address,
        String transactionHash) {
        return subscribeTransaction(ListenerChannel.AGGREGATE_BONDED_ADDED, address,
            transactionHash);
    }

    @Override
    public Observable<String> aggregateBondedRemoved(Address address, String transactionHash) {
        return subscribeTransactionHash(ListenerChannel.AGGREGATE_BONDED_REMOVED, address,
            transactionHash);
    }

    @Override
    public Observable<TransactionStatusError> status(Address address, String transactionHash) {
        Validate.notNull(address, "Address is required");
        validateOpen();
        this.subscribeTo(ListenerChannel.STATUS + "/" + address.plain());
        return getMessageSubject()
            .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.STATUS))
            .map(rawMessage -> (TransactionStatusError) rawMessage.getMessage())
            .filter(status -> address.equals(status.getAddress()))
            .filter(status -> transactionHash == null || transactionHash
                .equalsIgnoreCase(status.getHash()));
    }

    @Override
    public Observable<CosignatureSignedTransaction> cosignatureAdded(Address address,
        String parentTransactionHash) {
        Validate.notNull(address, "Address is required");
        validateOpen();
        this.subscribeTo(ListenerChannel.COSIGNATURE + "/" + address.plain());
        return getMessageSubject()
            .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.COSIGNATURE))
            .map(rawMessage -> (CosignatureSignedTransaction) rawMessage.getMessage())
            .filter(status -> parentTransactionHash == null || parentTransactionHash
                .equalsIgnoreCase(status.getParentHash()));
    }

    private void validateOpen() {
        if (getUid() == null) {
            throw new IllegalStateException(
                "Listener has not been opened yet. Please call the open method before subscribing.");
        }
    }

    @Override
    public Observable<AggregateTransaction> aggregateBondedAddedOrError(Address address,
        String transactionHash) {
        return getTransactionOrRaiseError(address, transactionHash,
            aggregateBondedAdded(address, transactionHash));
    }


    private <T extends Transaction> Observable<T> getTransactionOrRaiseError(Address address,
        String transactionHash, Observable<T> transactionListener) {
        // I may move this method to the Listener
        IllegalStateException caller = new IllegalStateException("The Caller");
        Observable<TransactionStatusError> errorListener = status(address)
            .filter(m -> transactionHash.equalsIgnoreCase(m.getHash()));
        Observable<Object> errorOrTransactionObservable = Observable
            .merge(transactionListener, errorListener).take(1);
        return errorOrTransactionObservable.map(errorOrTransaction -> {
            if (errorOrTransaction instanceof TransactionStatusError) {
                throw new TransactionStatusException(caller,
                    (TransactionStatusError) errorOrTransaction);
            } else {
                return (T) errorOrTransaction;
            }
        });
    }


    private <T extends Transaction> Observable<T> subscribeTransaction(ListenerChannel channel,
        Address address,
        String transactionHash) {
        Validate.notNull(address, "Address is required");
        validateOpen();
        this.subscribeTo(channel.toString() + "/" + address.plain());
        return getMessageSubject()
            .filter(rawMessage -> rawMessage.getChannel().equals(channel))
            .map(rawMessage -> (T) rawMessage.getMessage())
            .filter(t -> t.getTransactionInfo()
                .filter(
                    info -> transactionHash == null || info.getHash()
                        .filter(transactionHash::equalsIgnoreCase).isPresent())
                .isPresent())
            .filter(transaction -> this.transactionFromAddress(transaction, address));
    }

    private Observable<String> subscribeTransactionHash(ListenerChannel channel, Address address,
        String transactionHash) {
        Validate.notNull(address, "Address is required");
        validateOpen();
        this.subscribeTo(channel + "/" + address.plain());
        return getMessageSubject()
            .filter(rawMessage -> rawMessage.getChannel().equals(channel))
            .map(rawMessage -> (String) rawMessage.getMessage())
            .filter(hash -> transactionHash == null || transactionHash.equalsIgnoreCase(hash));
    }

    public boolean transactionFromAddress(final Transaction transaction, final Address address) {
        if (transaction.getSigner().filter(s -> s.getAddress().equals(address)).isPresent()) {
            return true;
        }

        if (transaction instanceof TransferTransaction) {
            return ((TransferTransaction) transaction).getRecipient().equals(address);
        }

        if (transaction instanceof MultisigAccountModificationTransaction) {
            return ((MultisigAccountModificationTransaction) transaction).getPublicKeyAdditions()
                .stream().anyMatch(m -> m.getAddress().equals(address));
        }
        if (transaction instanceof AggregateTransaction) {
            final AggregateTransaction aggregateTransaction = (AggregateTransaction) transaction;
            if (aggregateTransaction.getCosignatures()
                .stream().anyMatch(c -> c.getSigner().getAddress().equals(address))) {
                return true;
            }
            //Recursion...
            return aggregateTransaction.getInnerTransactions()
                .stream().anyMatch(t -> this.transactionFromAddress(t, address));
        }
        return false;
    }


    /**
     * I fires the new message object to the subject listenrs.
     *
     * @param channel the channel
     * @param messageObject the message object.
     */
    private void onNext(ListenerChannel channel, Object messageObject) {
        this.getMessageSubject().onNext(new ListenerMessage(channel, messageObject));
    }

    /**
     * Subclasses know how to map a generic blockInfoDTO json to a BlockInfo using the generated
     * DTOs of the implementation.
     *
     * @param blockInfoDTO the generic json
     * @return the model {@link BlockInfo}
     */
    protected abstract BlockInfo toBlockInfo(Object blockInfoDTO);

    /**
     * Subclasses know how to map a generic TransactionInfoDto json to a Transaction using the
     * generated DTOs of the implementation.
     *
     * @param transactionInfo the generic json
     * @return the model {@link Transaction}
     */
    protected abstract Transaction toTransaction(Object transactionInfo);

    /**
     * Subclasses know how to map a generic Consignature DTO json to a CosignatureSignedTransaction
     * using the generated DTOs of the implementation.
     *
     * @param cosignature the generic json
     * @return the model {@link CosignatureSignedTransaction}
     */
    protected abstract CosignatureSignedTransaction toCosignatureSignedTransaction(
        Object cosignature);

    protected abstract void subscribeTo(String channel);

    public Subject<ListenerMessage> getMessageSubject() {
        return messageSubject;
    }

    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }

    /**
     * @return the UID connected to
     */
    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
