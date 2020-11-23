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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.List;

/** Block with importance information. */
public class ImportanceBlockInfo extends BlockInfo {

  /** Number of voting eligible accounts. */
  private final long votingEligibleAccountsCount;

  /** Number of harvesting eligible accounts. */
  private final BigInteger harvestingEligibleAccountsCount;
  /** Total balance eligible for voting. */
  private final BigInteger totalVotingBalance;

  /** Previous importance block hash. */
  private final String previousImportanceBlockHash;

  public ImportanceBlockInfo(
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
      Address beneficiaryAddress,
      long votingEligibleAccountsCount,
      BigInteger harvestingEligibleAccountsCount,
      BigInteger totalVotingBalance,
      String previousImportanceBlockHash) {
    super(
        recordId,
        size,
        hash,
        generationHash,
        totalFee,
        stateHashSubCacheMerkleRoots,
        transactionsCount,
        totalTransactionsCount,
        statementsCount,
        subCacheMerkleRoots,
        signature,
        signerPublicAccount,
        networkType,
        version,
        type,
        height,
        timestamp,
        difficulty,
        feeMultiplier,
        previousBlockHash,
        blockTransactionsHash,
        blockReceiptsHash,
        stateHash,
        proofGamma,
        proofScalar,
        proofVerificationHash,
        beneficiaryAddress);
    this.votingEligibleAccountsCount = votingEligibleAccountsCount;
    this.harvestingEligibleAccountsCount = harvestingEligibleAccountsCount;
    this.totalVotingBalance = totalVotingBalance;
    this.previousImportanceBlockHash = previousImportanceBlockHash;
  }

  public long getVotingEligibleAccountsCount() {
    return votingEligibleAccountsCount;
  }

  public BigInteger getHarvestingEligibleAccountsCount() {
    return harvestingEligibleAccountsCount;
  }

  public BigInteger getTotalVotingBalance() {
    return totalVotingBalance;
  }

  public String getPreviousImportanceBlockHash() {
    return previousImportanceBlockHash;
  }
}
