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
 * The benefits of using a factory instead of a constructor:
 *
 * <ul>
 *   <li>Transactions can be immutable</li>
 *   <li>Better support for default values like deadline and max fee</li>
 *   <li>Better extensibility, fields can be added to {@link Transaction} without affecting the subclasses. It's not necessary to change subclasses constructors.</li>
 *   <li>Massive constructors are not necessary in the {@link Transaction} subclasses</li>
 *   <li>Mappers are easier to implement as top level mappers can set the top level transaction attributes via the factories
 *   without affecting the mapper subclasses</li>
 * </ul>
 *
 * When a new transaction type is added, a new extension of this factory should be added too.
 *
 * @param <T> the transaction class an instance of this factory builds.
 */
public abstract class TransactionFactory<T extends Transaction> {

    /**
     * The transaction type of the new transaction.
     */
    private final TransactionType type;

    /**
     * The network type of the new transaction.
     */
    private final NetworkType networkType;

    /**
     * The version of the new transaction, by default the {@link TransactionType} default version.
     */
    private Integer version;

    /**
     * The deadline of the new transaction. 2 hours by default.
     */
    private Deadline deadline = Deadline.create();

    /**
     * The max fee of the new transaction. Zero by default.
     */
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


    /**
     * The constructor that sets the required and default attributes.
     *
     * @param type the transaction type, this field is generally defined in the sub classes.
     * @param networkType the network type of this transaction.
     */
    public TransactionFactory(TransactionType type, NetworkType networkType) {
        Validate.notNull(type, "Type must not be null");
        Validate.notNull(networkType, "NetworkType must not be null");
        this.type = type;
        this.networkType = networkType;
        this.version = type.getCurrentVersion();
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
     * Builder method used to set the signer.  This method is generally called from the rest api
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
     * Builder method used to set the {@link TransactionInfo}.  This method is generally called from
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
     * @return the transaction type.
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * @return the netwirj type.
     */
    public NetworkType getNetworkType() {
        return networkType;
    }

    /**
     * @return the version.
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @return the transaction type.
     */
    public Deadline getDeadline() {
        return deadline;
    }

    /**
     * @return the transaction type.
     */
    public BigInteger getMaxFee() {
        return maxFee;
    }

    /**
     * @return the transaction signaure if set.
     */
    public Optional<String> getSignature() {
        return signature;
    }

    /**
     * @return the transaction info if set.
     */
    public Optional<TransactionInfo> getTransactionInfo() {
        return transactionInfo;
    }

    /**
     * @return the transaction signed if set.
     */
    public Optional<PublicAccount> getSigner() {
        return signer;
    }


    /**
     * @return the size of the transaction that's going to be created. Useful when you want to
     * update the maxFee of the transaction depending on its size.
     */
    public int getSize() {
        return build().getSize();
    }

    /**
     * @return the new transaction immutable based on the configured factory.
     */
    public abstract T build();

}
