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


package io.nem.symbol.sdk.model.network;

/**
 * Chain related configuration properties.
 */
public class ChainProperties {

    /**
     * Set to true if block chain should calculate state hashes so that state is fully verifiable at each block.
     */
    private final Boolean enableVerifiableState;

    /**
     * Set to true if block chain should calculate receipts so that state changes are fully verifiable at each block.
     */
    private final Boolean enableVerifiableReceipts;

    /**
     * Mosaic id used as primary chain currency.
     */
    private final String currencyMosaicId;

    /**
     * Mosaic id used to provide harvesting ability.
     */
    private final String harvestingMosaicId;

    /**
     * Targeted time between blocks.
     */
    private final String blockGenerationTargetTime;

    /**
     * A higher value makes the network more biased.
     */
    private final String blockTimeSmoothingFactor;

    /**
     * Number of blocks between successive finalization attempts.
     */
    private final String blockFinalizationInterval;

    /**
     * Number of blocks that should be treated as a group for importance purposes.
     */
    private final String importanceGrouping;

    /**
     * Percentage of importance resulting from fee generation and beneficiary usage.
     */
    private final String importanceActivityPercentage;

    /**
     * Maximum number of blocks that can be rolled back.
     */
    private final String maxRollbackBlocks;

    /**
     * Maximum number of blocks to use in a difficulty calculation.
     */
    private final String maxDifficultyBlocks;

    /**
     * Default multiplier to use for dynamic fees.
     */
    private final String defaultDynamicFeeMultiplier;

    /**
     * Maximum lifetime a transaction can have before it expires.
     */
    private final String maxTransactionLifetime;

    /**
     * Maximum future time of a block that can be accepted.
     */
    private final String maxBlockFutureTime;

    /**
     * Initial currency atomic units available in the network.
     */
    private final String initialCurrencyAtomicUnits;

    /**
     * Maximum atomic units (total-supply * 10 ^ divisibility) of a mosaic allowed in the network.
     */
    private final String maxMosaicAtomicUnits;

    /**
     * Total whole importance units available in the network.
     */
    private final String totalChainImportance;

    /**
     * Minimum number of harvesting mosaic atomic units needed for an account to be eligible for harvesting.
     */
    private final String minHarvesterBalance;

    /**
     * Maximum number of harvesting mosaic atomic units needed for an account to be eligible for harvesting.
     */
    private final String maxHarvesterBalance;

    /**
     * Minimum number of harvesting mosaic atomic units needed for an account to be eligible for voting.
     */
    private final String minVoterBalance;

    /**
     * Maximum number of voting keys that can be registered at once per account.
     */
    private final String maxVotingKeysPerAccount;

    /**
     * Minimum number of finalization rounds for which voting key can be registered.
     */
    private final String minVotingKeyLifetime;

    /**
     * Maximum number of finalization rounds for which voting key can be registered.
     */
    private final String maxVotingKeyLifetime;

    /**
     * Percentage of the harvested fee that is collected by the beneficiary account.
     */
    private final String harvestBeneficiaryPercentage;

    /**
     * Percentage of the harvested fee that is collected by network.
     */
    private final String harvestNetworkPercentage;

    /**
     * The harvest network fee sink address.
     */
    private final String harvestNetworkFeeSinkAddress;
    /**
     * Number of blocks between cache pruning.
     */
    private final String blockPruneInterval;

    /**
     * Maximum number of transactions per block.
     */
    private final String maxTransactionsPerBlock;


    public ChainProperties(Boolean enableVerifiableState, Boolean enableVerifiableReceipts, String currencyMosaicId,
        String harvestingMosaicId, String blockGenerationTargetTime, String blockTimeSmoothingFactor,
        String blockFinalizationInterval, String importanceGrouping, String importanceActivityPercentage,
        String maxRollbackBlocks, String maxDifficultyBlocks, String defaultDynamicFeeMultiplier,
        String maxTransactionLifetime, String maxBlockFutureTime, String initialCurrencyAtomicUnits,
        String maxMosaicAtomicUnits, String totalChainImportance, String minHarvesterBalance,
        String maxHarvesterBalance, String minVoterBalance, String maxVotingKeysPerAccount, String minVotingKeyLifetime,
        String maxVotingKeyLifetime, String harvestBeneficiaryPercentage, String harvestNetworkPercentage,
        String harvestNetworkFeeSinkAddress, String blockPruneInterval, String maxTransactionsPerBlock) {
        this.enableVerifiableState = enableVerifiableState;
        this.enableVerifiableReceipts = enableVerifiableReceipts;
        this.currencyMosaicId = currencyMosaicId;
        this.harvestingMosaicId = harvestingMosaicId;
        this.blockGenerationTargetTime = blockGenerationTargetTime;
        this.blockTimeSmoothingFactor = blockTimeSmoothingFactor;
        this.blockFinalizationInterval = blockFinalizationInterval;
        this.importanceGrouping = importanceGrouping;
        this.importanceActivityPercentage = importanceActivityPercentage;
        this.maxRollbackBlocks = maxRollbackBlocks;
        this.maxDifficultyBlocks = maxDifficultyBlocks;
        this.defaultDynamicFeeMultiplier = defaultDynamicFeeMultiplier;
        this.maxTransactionLifetime = maxTransactionLifetime;
        this.maxBlockFutureTime = maxBlockFutureTime;
        this.initialCurrencyAtomicUnits = initialCurrencyAtomicUnits;
        this.maxMosaicAtomicUnits = maxMosaicAtomicUnits;
        this.totalChainImportance = totalChainImportance;
        this.minHarvesterBalance = minHarvesterBalance;
        this.maxHarvesterBalance = maxHarvesterBalance;
        this.minVoterBalance = minVoterBalance;
        this.maxVotingKeysPerAccount = maxVotingKeysPerAccount;
        this.minVotingKeyLifetime = minVotingKeyLifetime;
        this.maxVotingKeyLifetime = maxVotingKeyLifetime;
        this.harvestBeneficiaryPercentage = harvestBeneficiaryPercentage;
        this.harvestNetworkPercentage = harvestNetworkPercentage;
        this.harvestNetworkFeeSinkAddress = harvestNetworkFeeSinkAddress;
        this.blockPruneInterval = blockPruneInterval;
        this.maxTransactionsPerBlock = maxTransactionsPerBlock;
    }

    public Boolean getEnableVerifiableState() {
        return enableVerifiableState;
    }

    public Boolean getEnableVerifiableReceipts() {
        return enableVerifiableReceipts;
    }

    public String getCurrencyMosaicId() {
        return currencyMosaicId;
    }

    public String getHarvestingMosaicId() {
        return harvestingMosaicId;
    }

    public String getBlockGenerationTargetTime() {
        return blockGenerationTargetTime;
    }

    public String getBlockTimeSmoothingFactor() {
        return blockTimeSmoothingFactor;
    }

    public String getBlockFinalizationInterval() {
        return blockFinalizationInterval;
    }

    public String getImportanceGrouping() {
        return importanceGrouping;
    }

    public String getImportanceActivityPercentage() {
        return importanceActivityPercentage;
    }

    public String getMaxRollbackBlocks() {
        return maxRollbackBlocks;
    }

    public String getMaxDifficultyBlocks() {
        return maxDifficultyBlocks;
    }

    public String getDefaultDynamicFeeMultiplier() {
        return defaultDynamicFeeMultiplier;
    }

    public String getMaxTransactionLifetime() {
        return maxTransactionLifetime;
    }

    public String getMaxBlockFutureTime() {
        return maxBlockFutureTime;
    }

    public String getInitialCurrencyAtomicUnits() {
        return initialCurrencyAtomicUnits;
    }

    public String getMaxMosaicAtomicUnits() {
        return maxMosaicAtomicUnits;
    }

    public String getTotalChainImportance() {
        return totalChainImportance;
    }

    public String getMinHarvesterBalance() {
        return minHarvesterBalance;
    }

    public String getMaxVotingKeysPerAccount() {
        return maxVotingKeysPerAccount;
    }

    public String getMinVotingKeyLifetime() {
        return minVotingKeyLifetime;
    }

    public String getMaxVotingKeyLifetime() {
        return maxVotingKeyLifetime;
    }

    public String getMaxHarvesterBalance() {
        return maxHarvesterBalance;
    }

    public String getMinVoterBalance() {
        return minVoterBalance;
    }

    public String getHarvestBeneficiaryPercentage() {
        return harvestBeneficiaryPercentage;
    }

    public String getHarvestNetworkPercentage() {
        return harvestNetworkPercentage;
    }

    public String getHarvestNetworkFeeSinkAddress() {
        return harvestNetworkFeeSinkAddress;
    }

    public String getBlockPruneInterval() {
        return blockPruneInterval;
    }

    public String getMaxTransactionsPerBlock() {
        return maxTransactionsPerBlock;
    }
}

