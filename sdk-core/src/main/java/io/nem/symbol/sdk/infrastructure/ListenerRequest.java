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

import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.BlockInfo;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionStatusError;
import org.apache.commons.lang3.Validate;

/**
 * Generic way of creating to subscribe to a message.
 *
 * @param <T> the type of the message.
 */
public class ListenerRequest<T> {

  private final ListenerChannel channel;

  private final UnresolvedAddress unresolvedAddress;

  private String transactionHash;

  private boolean orError;

  public ListenerRequest(ListenerChannel channel, UnresolvedAddress unresolvedAddress) {
    this.channel = channel;
    this.unresolvedAddress = unresolvedAddress;
  }

  public static ListenerRequest<BlockInfo> block() {
    return new ListenerRequest<>(ListenerChannel.BLOCK, null);
  }

  public static ListenerRequest<FinalizedBlock> finalizedBlock() {
    return new ListenerRequest<>(ListenerChannel.FINALIZED_BLOCK, null);
  }

  public static ListenerRequest<Transaction> unconfirmedAdded(UnresolvedAddress unresolvedAddress) {
    Validate.notNull(unresolvedAddress, "unresolvedAddress is required");
    return new ListenerRequest<>(ListenerChannel.UNCONFIRMED_ADDED, unresolvedAddress);
  }

  public static ListenerRequest<String> unconfirmedRemoved(UnresolvedAddress unresolvedAddress) {
    Validate.notNull(unresolvedAddress, "unresolvedAddress is required");
    return new ListenerRequest<>(ListenerChannel.UNCONFIRMED_REMOVED, unresolvedAddress);
  }

  public static ListenerRequest<AggregateTransaction> aggregateBondedAdded(
      UnresolvedAddress unresolvedAddress) {
    Validate.notNull(unresolvedAddress, "unresolvedAddress is required");
    return new ListenerRequest<>(ListenerChannel.AGGREGATE_BONDED_ADDED, unresolvedAddress);
  }

  public static ListenerRequest<String> aggregateBondedRemoved(
      UnresolvedAddress unresolvedAddress) {
    Validate.notNull(unresolvedAddress, "unresolvedAddress is required");
    ;
    return new ListenerRequest<>(ListenerChannel.AGGREGATE_BONDED_REMOVED, unresolvedAddress);
  }

  public static ListenerRequest<CosignatureSignedTransaction> cosignature(
      UnresolvedAddress unresolvedAddress) {
    Validate.notNull(unresolvedAddress, "unresolvedAddress is required");
    ;
    return new ListenerRequest<>(ListenerChannel.COSIGNATURE, unresolvedAddress);
  }

  public static ListenerRequest<Transaction> confirmed(UnresolvedAddress unresolvedAddress) {
    Validate.notNull(unresolvedAddress, "unresolvedAddress is required");
    return new ListenerRequest<>(ListenerChannel.CONFIRMED_ADDED, unresolvedAddress);
  }

  public static ListenerRequest<TransactionStatusError> status(
      UnresolvedAddress unresolvedAddress) {
    Validate.notNull(unresolvedAddress, "unresolvedAddress is required");
    return new ListenerRequest<>(ListenerChannel.STATUS, unresolvedAddress);
  }

  public ListenerRequest<T> transactionHash(String transactionHash) {
    Validate.isTrue(
        channel != ListenerChannel.FINALIZED_BLOCK,
        "Cannot subscribe transaction hash on finalized block");
    Validate.isTrue(channel != ListenerChannel.BLOCK, "Cannot subscribe transaction hash on block");
    this.transactionHash = transactionHash;
    return this;
  }

  public ListenerRequest<T> transactionHashOrError(String transactionHash) {
    Validate.notNull(unresolvedAddress, "transactionHash is required");
    Validate.isTrue(channel != ListenerChannel.STATUS, "Cannot subscribe on error with Status");
    this.orError = true;
    return this.transactionHash(transactionHash);
  }

  public ListenerRequest<T> transactionHashOrError(String transactionHash, boolean orError) {
    if (orError) {
      this.transactionHashOrError(transactionHash);
    } else {
      this.orError = false;
      this.transactionHash = transactionHash;
    }
    return this;
  }

  public ListenerChannel getChannel() {
    return channel;
  }

  public UnresolvedAddress getUnresolvedAddress() {
    return unresolvedAddress;
  }

  public String getTransactionHash() {
    return transactionHash;
  }

  public boolean isOrError() {
    return orError;
  }

  public String getTopic() {
    if (unresolvedAddress == null) {
      return channel.toString();
    } else {
      return channel.toString() + "/" + unresolvedAddress.plain();
    }
  }
}
