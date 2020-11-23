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
package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 * The transaction factories allow you to create instances of transactions.
 *
 * <p>The benefits of using a factory instead of a constructor:
 *
 * <ul>
 *   <li>Transactions can be immutable
 *   <li>Better support for default values like deadline and max fee
 *   <li>Better extensibility, fields can be added to {@link Transaction} without affecting the
 *       subclasses. It's not necessary to change subclasses constructors.
 *   <li>Massive constructors are not necessary in the {@link Transaction} subclasses
 *   <li>Mappers are easier to implement as top level mappers can set the top level transaction
 *       attributes via the factories without affecting the mapper subclasses
 * </ul>
 *
 * <p>When a new transaction type is added, a new extension of this factory should be added too.
 *
 * @param <T> the transaction class an instance of this factory builds.
 */
public abstract class TransactionFactory<T extends Transaction> {

  /** The transaction type of the new transaction. */
  private final TransactionType type;

  /** The network type of the new transaction. */
  private final NetworkType networkType;

  /** The deadline of the new transaction. */
  private Deadline deadline;

  /** The version of the new transaction, by default the {@link TransactionType} default version. */
  private Integer version;

  /** The max fee of the new transaction. Zero by default. */
  private BigInteger maxFee = BigInteger.ZERO;

  /**
   * The signature of the new transaction. This is generally set when mapping transaction coming
   * from the rest api.
   */
  private Optional<String> signature = Optional.empty();

  /**
   * The signer of the new transaction. This is generally set when mapping transaction coming from
   * the rest api.
   */
  private Optional<PublicAccount> signer = Optional.empty();

  /**
   * The {@link TransactionInfo} of the new transaction. This is generally set when mapping
   * transaction coming from the rest api.
   */
  private Optional<TransactionInfo> transactionInfo = Optional.empty();

  /** The the known group/state of transaction. */
  private Optional<TransactionGroup> group = Optional.empty();

  /**
   * The size provided by rest if known. Otherwise it will be calculated from the serialization
   * size.
   */
  private Optional<Long> size = Optional.empty();

  /**
   * The constructor that sets the required and default attributes.
   *
   * @param type the transaction type, this field is generally defined in the sub classes.
   * @param version the version of the transaction
   * @param networkType the network type of this transaction.
   * @param deadline The deadline of the new transaction based on the server epoch adjustment time.
   */
  public TransactionFactory(
      TransactionType type, int version, NetworkType networkType, Deadline deadline) {
    Validate.notNull(type, "Type must not be null");
    Validate.notNull(networkType, "NetworkType must not be null");
    Validate.notNull(deadline, "deadline must not be null");
    this.type = type;
    this.networkType = networkType;
    this.version = version;
    this.deadline = deadline;
  }

  /**
   * The constructor that sets the required and default attributes.
   *
   * @param type the transaction type, this field is generally defined in the sub classes.
   * @param networkType the network type of this transaction.
   * @param deadline The deadline of the new transaction based on the server epoch adjustment time.
   */
  public TransactionFactory(TransactionType type, NetworkType networkType, Deadline deadline) {
    this(type, type.getCurrentVersion(), networkType, deadline);
  }

  /**
   * Builder method used to change the default deadline.
   *
   * @param deadline a new deadline
   * @return this factory to continue building the transaction.
   */
  public TransactionFactory<T> deadline(Deadline deadline) {
    Validate.notNull(deadline, "Deadline must not be null");
    this.deadline = deadline;
    return this;
  }

  /**
   * Builder method used to change the default maxFee.
   *
   * @param maxFee a new maxFee
   * @return this factory to continue building the transaction.
   */
  public TransactionFactory<T> maxFee(BigInteger maxFee) {
    Validate.notNull(maxFee, "MaxFee must not be null");
    this.maxFee = maxFee;
    return this;
  }

  /**
   * Builder method used to to re-calculate the max fee based on the configured feeMultiplier
   *
   * @param feeMultiplier the fee multiplier greater than 1
   * @return this factory to continue building the transaction.
   */
  public TransactionFactory<T> calculateMaxFeeFromMultiplier(long feeMultiplier) {
    return maxFee(BigInteger.valueOf(getSize()).multiply(BigInteger.valueOf(feeMultiplier)));
  }

  /**
   * Builder method used to set the signature. This method is generally called from the rest api
   * mappers.
   *
   * @param signature the signature.
   * @return this factory to continue building the transaction.
   */
  public TransactionFactory<T> signature(String signature) {
    Validate.notNull(signature, "Signature must not be null");
    this.signature = Optional.of(signature);
    return this;
  }

  /**
   * Builder method used to set the signer. This method is generally called from the rest api
   * mappers.
   *
   * @param signer the signer {@link PublicAccount}.
   * @return this factory to continue building the transaction.
   */
  public TransactionFactory<T> signer(PublicAccount signer) {
    Validate.notNull(signer, "Signer must not be null");
    this.signer = Optional.of(signer);
    return this;
  }

  /**
   * Builder method used to set the {@link TransactionInfo}. This method is generally called from
   * the rest api mappers.
   *
   * @param transactionInfo the {@link TransactionInfo}.
   * @return this factory to continue building the transaction.
   */
  public TransactionFactory<T> transactionInfo(TransactionInfo transactionInfo) {
    Validate.notNull(transactionInfo, "TransactionInfo must not be null");
    this.transactionInfo = Optional.of(transactionInfo);
    return this;
  }

  /**
   * Builder method used to change the default version. This method is generally called from the
   * rest api mapper.
   *
   * @param version a new version
   * @return this factory to continue building the transaction.
   */
  public TransactionFactory<T> version(Integer version) {
    Validate.notNull(signer, "Version must not be null");
    this.version = version;
    return this;
  }

  /**
   * Builder method used to change the default size. This method is generally called from the rest
   * api mapper.
   *
   * @param size the known size
   * @return this factory to continue building the transaction.
   */
  public TransactionFactory<T> size(long size) {
    this.size = Optional.of(size);
    return this;
  }

  /**
   * Builder method used to change the known transaction group. This method is generally called from
   * the rest api mapper.
   *
   * @param group a new group
   * @return this factory to continue building the transaction.
   */
  public TransactionFactory<T> group(TransactionGroup group) {
    Validate.notNull(signer, "Group must not be null");
    this.group = Optional.of(group);
    return this;
  }

  /** @return the transaction type. */
  public TransactionType getType() {
    return type;
  }

  /** @return the netwirj type. */
  public NetworkType getNetworkType() {
    return networkType;
  }

  /** @return the version. */
  public Integer getVersion() {
    return version;
  }

  /** @return the transaction type. */
  public Deadline getDeadline() {
    return deadline;
  }

  /** @return the transaction type. */
  public BigInteger getMaxFee() {
    return maxFee;
  }

  /** @return the transaction signaure if set. */
  public Optional<String> getSignature() {
    return signature;
  }

  /** @return the transaction info if set. */
  public Optional<TransactionInfo> getTransactionInfo() {
    return transactionInfo;
  }

  /** @return the transaction signed if set. */
  public Optional<PublicAccount> getSigner() {
    return signer;
  }

  /** @return the size from rest. */
  protected Optional<Long> getProvidedSize() {
    return this.size;
  }

  /**
   * @return the size of the transaction that's going to be created. Useful when you want to update
   *     the maxFee of the transaction depending on its size.
   */
  public long getSize() {
    return this.size.orElseGet(() -> build().getSize());
  }

  /** @return the set group. */
  public Optional<TransactionGroup> getGroup() {
    return group;
  }

  /** @return the new transaction immutable based on the configured factory. */
  public abstract T build();
}
