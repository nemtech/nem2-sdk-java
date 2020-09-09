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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/** Criteria used to search TransactionStatement. */
public class TransactionStatementSearchCriteria
    extends SearchCriteria<TransactionStatementSearchCriteria> {

  /** Filter the transaction receipts by block height. */
  private BigInteger height;

  /** Filter the transaction receipts by type */
  private List<ReceiptType> receiptTypes;

  /** Filter the transaction receipts by receipt address. */
  private Address recipientAddress;

  /** Filter the transaction receipts by sender address */
  private Address senderAddress;

  /** Filter the transaction receipts by target address */
  private Address targetAddress;

  /** Filter the transaction receipts by artifact id (mosaic id hex or namespace id hex) */
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

  public List<ReceiptType> getReceiptTypes() {
    return receiptTypes;
  }

  public void setReceiptTypes(List<ReceiptType> receiptTypes) {
    this.receiptTypes = receiptTypes;
  }

  public TransactionStatementSearchCriteria receiptTypes(List<ReceiptType> receiptType) {
    this.receiptTypes = receiptType;
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
    if (!super.equals(o)) {
      return false;
    }
    TransactionStatementSearchCriteria that = (TransactionStatementSearchCriteria) o;
    return Objects.equals(height, that.height)
        && Objects.equals(receiptTypes, that.receiptTypes)
        && Objects.equals(recipientAddress, that.recipientAddress)
        && Objects.equals(senderAddress, that.senderAddress)
        && Objects.equals(targetAddress, that.targetAddress)
        && Objects.equals(artifactId, that.artifactId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        height,
        receiptTypes,
        recipientAddress,
        senderAddress,
        targetAddress,
        artifactId);
  }
}
