package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Criteria used to search TransactionStatement.
 */
public class TransactionStatementSearchCriteria extends SearchCriteria<TransactionStatementSearchCriteria> {

    /**
     * Filter the transaction receipts by block height.
     */
    private BigInteger height;

    /**
     * Filter the transaction receipts by type height.
     */
    private ReceiptType receiptType;

    /**
     * Filter the transaction receipts by receipt address.
     */
    private Address recipientAddress;

    /**
     * Filter the transaction receipts by sender address
     */
    private Address senderAddress;

    /**
     * Filter the transaction receipts by target address
     */
    private Address targetAddress;

    /**
     * Filter the transaction receipts by artifact id (mosaic id hex or namespace id hex)
     */
    private String artifactId;


    public BigInteger getHeight() {
        return height;
    }

    public void setHeight(BigInteger height) {
        this.height = height;
    }

    public TransactionStatementSearchCriteria height(BigInteger height) {
        this.height = height;
        return this;
    }

    public ReceiptType getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(ReceiptType receiptType) {
        this.receiptType = receiptType;
    }


    public TransactionStatementSearchCriteria receiptType(ReceiptType receiptType) {
        this.receiptType = receiptType;
        return this;
    }

    public Address getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(Address recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public TransactionStatementSearchCriteria recipientAddress(Address recipientAddress) {
        this.recipientAddress = recipientAddress;
        return this;
    }

    public Address getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(Address senderAddress) {
        this.senderAddress = senderAddress;
    }

    public TransactionStatementSearchCriteria senderAddress(Address senderAddress) {
        this.senderAddress = senderAddress;
        return this;
    }

    public Address getTargetAddress() {
        return targetAddress;
    }

    public TransactionStatementSearchCriteria targetAddress(Address targetAddress) {
        this.targetAddress = targetAddress;
        return this;
    }

    public void setTargetAddress(Address targetAddress) {
        this.targetAddress = targetAddress;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public TransactionStatementSearchCriteria artifactId(String artifactId) {
        this.artifactId = artifactId;
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
        TransactionStatementSearchCriteria that = (TransactionStatementSearchCriteria) o;
        return Objects.equals(height, that.height) && receiptType == that.receiptType && Objects
            .equals(recipientAddress, that.recipientAddress) && Objects.equals(senderAddress, that.senderAddress)
            && Objects.equals(targetAddress, that.targetAddress) && Objects.equals(artifactId, that.artifactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, receiptType, recipientAddress, senderAddress, targetAddress, artifactId);
    }
}
