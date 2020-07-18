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

package io.nem.symbol.sdk.api;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * Defines the params used to search transactions. With this criteria, you can sort and filter
 * transactions queries using rest.
 */
public class TransactionSearchCriteria extends SearchCriteria<TransactionSearchCriteria> {

    /**
     * Transaction identifier up to which transactions are returned. (optional)
     */
    private String id;

    /**
     * Filter by address involved in the transaction.
     *
     * An account's address is consider to be involved in the transaction when the account is the
     * sender, recipient, or it is required to cosign the transaction.
     *
     * This filter cannot be combined with ''recipientAddress'' and ''signerPublicKey'' query
     * params.  (optional)
     */
    private Address address;

    /**
     * Address of an account receiving the transaction. (optional)
     */
    private Address recipientAddress;

    /**
     * Public key of the account signing the entity. (optional)
     */
    private PublicKey signerPublicKey;

    /**
     * Filter by block height. (optional, default to null)
     */
    private BigInteger height;

    /**
     * The group of transaction (optional, default is confirmed)
     */
    private final TransactionGroup group;

    /**
     * Filter by transaction type. To filter by multiple transaction type.  (optional, default to
     * new empty array)
     */
    private List<TransactionType> transactionTypes;

    /**
     * When true, the endpoint also returns all the embedded aggregate transactions. When
     * false, only top-level transactions used to calculate the block transactionsHash are
     * returned.  (optional, default to false)
     */
    private Boolean embedded;

    /**
     * @param group the group you are searching for.
     */
    public TransactionSearchCriteria(TransactionGroup group) {
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    public Address getRecipientAddress() {
        return recipientAddress;
    }

    public PublicKey getSignerPublicKey() {
        return signerPublicKey;
    }

    public BigInteger getHeight() {
        return height;
    }

    public TransactionGroup getGroup() {
        return group;
    }

    public List<TransactionType> getTransactionTypes() {
        return transactionTypes;
    }

    public Boolean getEmbedded() {
        return embedded;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setRecipientAddress(Address recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public void setSignerPublicKey(PublicKey signerPublicKey) {
        this.signerPublicKey = signerPublicKey;
    }

    public void setHeight(BigInteger height) {
        this.height = height;
    }

    public void setTransactionTypes(
        List<TransactionType> transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public void setEmbedded(Boolean embedded) {
        this.embedded = embedded;
    }

    public TransactionSearchCriteria id(String id) {
        this.id = id;
        return this;
    }

    public TransactionSearchCriteria transactionTypes(List<TransactionType> transactionTypes) {
        this.transactionTypes = transactionTypes;
        return this;
    }

    public TransactionSearchCriteria address(Address address) {
        this.address = address;
        return this;
    }

    public TransactionSearchCriteria recipientAddress(Address recipientAddress) {
        this.recipientAddress = recipientAddress;
        return this;
    }

    public TransactionSearchCriteria signerPublicKey(PublicKey signerPublicKey) {
        this.signerPublicKey = signerPublicKey;
        return this;
    }

    public TransactionSearchCriteria height(BigInteger height) {
        this.height = height;
        return this;
    }

    public TransactionSearchCriteria embedded(Boolean embedded) {
        this.embedded = embedded;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TransactionSearchCriteria that = (TransactionSearchCriteria) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(address, that.address) &&
            Objects.equals(recipientAddress, that.recipientAddress) &&
            Objects.equals(signerPublicKey, that.signerPublicKey) &&
            Objects.equals(height, that.height) &&
            group == that.group &&
            Objects.equals(transactionTypes, that.transactionTypes) &&
            Objects.equals(embedded, that.embedded);
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(super.hashCode(), id, address, recipientAddress, signerPublicKey, height,
                group, transactionTypes, embedded);
    }
}
