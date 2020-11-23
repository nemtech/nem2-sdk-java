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
package io.nem.symbol.sdk.model.blockchain;

import io.nem.symbol.sdk.model.Stored;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/** The block info structure describes basic information of a block. */
public class BlockInfo implements Stored {

  private final String recordId;
  private final Long size;
  private final String hash;
  private final String generationHash;
  private final BigInteger totalFee;
  private final List<String> stateHashSubCacheMerkleRoots;
  private final Integer transactionsCount;
  private final Integer totalTransactionsCount;
  private final Integer statementsCount;
  private final List<String> subCacheMerkleRoots;
  private final String signature;
  private final PublicAccount signerPublicAccount;
  private final NetworkType networkType;
  private final Integer version;
  private final BlockType type;
  private final BigInteger height;
  private final BigInteger timestamp;
  private final BigInteger difficulty;
  private final Long feeMultiplier;
  private final String previousBlockHash;
  private final String blockTransactionsHash;
  private final String blockReceiptsHash;
  private final String stateHash;
  private final String proofGamma;
  private final String proofScalar;
  private final String proofVerificationHash;
  private final Address beneficiaryAddress;

  @SuppressWarnings("squid:S00107")
  public BlockInfo(
      String recordId,
      Long size,
      String hash,
      String generationHash,
      BigInteger totalFee,
      List<String> stateHashSubCacheMerkleRoots,
      Integer transactionsCount,
      Integer totalTransactionsCount,
      Integer statementsCount,
      List<String> subCacheMerkleRoots,
      String signature,
      PublicAccount signerPublicAccount,
      NetworkType networkType,
      Integer version,
      BlockType type,
      BigInteger height,
      BigInteger timestamp,
      BigInteger difficulty,
      Long feeMultiplier,
      String previousBlockHash,
      String blockTransactionsHash,
      String blockReceiptsHash,
      String stateHash,
      String proofGamma,
      String proofScalar,
      String proofVerificationHash,
      Address beneficiaryAddress) {
    this.recordId = recordId;
    this.size = size;
    this.hash = hash;
    this.generationHash = generationHash;
    this.totalFee = totalFee;
    this.stateHashSubCacheMerkleRoots = stateHashSubCacheMerkleRoots;
    this.transactionsCount = transactionsCount;
    this.totalTransactionsCount = totalTransactionsCount;
    this.statementsCount = statementsCount;
    this.subCacheMerkleRoots = subCacheMerkleRoots;
    this.signature = signature;
    this.signerPublicAccount = signerPublicAccount;
    this.networkType = networkType;
    this.version = version;
    this.type = type;
    this.height = height;
    this.timestamp = timestamp;
    this.difficulty = difficulty;
    this.feeMultiplier = feeMultiplier;
    this.previousBlockHash = previousBlockHash;
    this.blockTransactionsHash = blockTransactionsHash;
    this.blockReceiptsHash = blockReceiptsHash;
    this.stateHash = stateHash;
    this.proofGamma = proofGamma;
    this.proofScalar = proofScalar;
    this.proofVerificationHash = proofVerificationHash;
    this.beneficiaryAddress = beneficiaryAddress;
  }

  /**
   * Returns the size of the block
   *
   * @return the size
   */
  public Long getSize() {
    return size;
  }

  /**
   * Returns block hash.
   *
   * @return String
   */
  public String getHash() {
    return hash;
  }

  /**
   * Returns block generation hash.
   *
   * @return String
   */
  public String getGenerationHash() {
    return generationHash;
  }

  /**
   * Returns total fee paid to the account harvesting the block.
   *
   * @return Integer
   */
  public BigInteger getTotalFee() {
    return totalFee;
  }

  /**
   * Number of transactions confirmed in this block. This does not count *embedded* transactions
   *
   * @return Integer
   */
  public Integer getTransactionsCount() {
    return transactionsCount;
  }

  /**
   * Returns number of statements included the block.
   *
   * @return Integer
   */
  public Integer getStatementsCount() {
    return statementsCount;
  }

  /** @return list of SubCache Merkle Root. */
  public List<String> getSubCacheMerkleRoots() {
    return subCacheMerkleRoots;
  }

  /**
   * The signature was generated by the signerPublicAccount and can be used to validate that the
   * blockchain data was not modified by a node.
   *
   * @return Block signature.
   */
  public String getSignature() {
    return signature;
  }

  /**
   * Returns public account of block harvester.
   *
   * @return {@link PublicAccount}
   */
  public PublicAccount getSignerPublicAccount() {
    return signerPublicAccount;
  }

  /**
   * Returns network type.
   *
   * @return {@link NetworkType}
   */
  public NetworkType getNetworkType() {
    return networkType;
  }

  /**
   * Returns block transaction version.
   *
   * @return Integer
   */
  public Integer getVersion() {
    return version;
  }

  /**
   * Returns block transaction type.
   *
   * @return int
   */
  public BlockType getType() {
    return type;
  }

  /**
   * Returns height of which the block was confirmed. Each block has a unique height. Subsequent
   * blocks differ in height by 1.
   *
   * @return BigInteger
   */
  public BigInteger getHeight() {
    return height;
  }

  /**
   * Returns the number of seconds elapsed since the creation of the nemesis blockchain.
   *
   * @return BigInteger
   */
  public BigInteger getTimestamp() {
    return timestamp;
  }

  /**
   * Returns POI difficulty to harvest a block.
   *
   * @return BigInteger
   */
  public BigInteger getDifficulty() {
    return difficulty;
  }

  /**
   * Returns the feeMultiplier defined by the harvester.
   *
   * @return Long
   */
  public Long getFeeMultiplier() {
    return feeMultiplier;
  }

  /**
   * Total number of transactions confirmed in this block, including *embedded* transactions (i.e.
   * transactions contained within aggregate transactions).
   *
   * @return Integer
   */
  public Integer getTotalTransactionsCount() {
    return totalTransactionsCount;
  }

  /**
   * Returns the last block hash.
   *
   * @return String
   */
  public String getPreviousBlockHash() {
    return previousBlockHash;
  }

  /**
   * Returns the block transaction hash.
   *
   * @return String
   */
  public String getBlockTransactionsHash() {
    return blockTransactionsHash;
  }

  /**
   * Returns the block receipts hash.
   *
   * @return String
   */
  public String getBlockReceiptsHash() {
    return blockReceiptsHash;
  }

  /**
   * Returns the block state hash.
   *
   * @return String
   */
  public String getStateHash() {
    return stateHash;
  }

  /**
   * Returns the beneficiary address.
   *
   * @return PublicAccount
   */
  public Address getBeneficiaryAddress() {
    return beneficiaryAddress;
  }

  /**
   * Returns The proof gamma.
   *
   * @return The proof gamma.
   */
  public String getProofGamma() {
    return proofGamma;
  }

  /**
   * Returns the proof scalar.
   *
   * @return The proof scalar.
   */
  public String getProofScalar() {
    return proofScalar;
  }

  /**
   * Returns the proof verification hash.
   *
   * @return The proof verification hash.
   */
  public String getProofVerificationHash() {
    return proofVerificationHash;
  }

  /**
   * Returns database id of the block
   *
   * @return The database id of the block.
   */
  public Optional<String> getRecordId() {
    return Optional.ofNullable(recordId);
  }

  /** @return state Hash Sub Cache Merkle Roots ahses */
  public List<String> getStateHashSubCacheMerkleRoots() {
    return stateHashSubCacheMerkleRoots;
  }
}
