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

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * The block info structure describes basic information of a block.
 */
public class BlockInfo {

    private final String hash;
    private final String generationHash;
    private final BigInteger totalFee;
    private final Integer numTransactions;
    private final Optional<Integer> numStatements;
    private final List<String> subCacheMerkleRoots;
    private final String signature;
    private final PublicAccount signerPublicAccount;
    private final NetworkType networkType;
    private final Integer version;
    private final int type;
    private final BigInteger height;
    private final BigInteger timestamp;
    private final BigInteger difficulty;
    private final Integer feeMultiplier;
    private final String previousBlockHash;
    private final String blockTransactionsHash;
    private final String blockReceiptsHash;
    private final String stateHash;
    private final String proofGamma;
    private final String proofScalar;
    private final String proofVerificationHash;
    private final PublicAccount beneficiaryPublicAccount;

    @SuppressWarnings("squid:S00107")
    private BlockInfo(
        String hash,
        String generationHash,
        BigInteger totalFee,
        Integer numTransactions,
        Optional<Integer> numStatements,
        List<String> subCacheMerkleRoots,
        String signature,
        PublicAccount signerPublicAccount,
        NetworkType networkType,
        Integer version,
        int type,
        BigInteger height,
        BigInteger timestamp,
        BigInteger difficulty,
        Integer feeMultiplier,
        String previousBlockHash,
        String blockTransactionsHash,
        String blockReceiptsHash,
        String stateHash,
        String proofGamma,
        String proofScalar,
        String proofVerificationHash,
        PublicAccount beneficiaryPublicAccount) {
        this.hash = hash;
        this.generationHash = generationHash;
        this.totalFee = totalFee;
        this.numTransactions = numTransactions;
        this.numStatements = numStatements;
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
        this.beneficiaryPublicAccount = beneficiaryPublicAccount;
    }

    @SuppressWarnings("squid:S00107")
    public static BlockInfo create(
        String hash,
        String generationHash,
        BigInteger totalFee,
        Integer numTransactions,
        Optional<Integer> numStatements,
        List<String> subCacheMerkleRoots,
        String signature,
        String signer,
        NetworkType networkType,
        Integer version,
        int type,
        BigInteger height,
        BigInteger timestamp,
        BigInteger difficulty,
        Integer feeMultiplier,
        String previousBlockHash,
        String blockTransactionsHash,
        String blockReceiptsHash,
        String stateHash,
        String proofGamma,
        String proofScalar,
        String proofVerificationHash,
        String beneficiaryPublicKey) {
        PublicAccount signerPublicAccount = BlockInfo.getPublicAccount(signer, networkType);
        PublicAccount beneficiaryPublicAccount = beneficiaryPublicKey == null ? null :
            BlockInfo.getPublicAccount(beneficiaryPublicKey, networkType);
        return new BlockInfo(
            hash,
            generationHash,
            totalFee,
            numTransactions,
            numStatements,
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
            beneficiaryPublicAccount);
    }

    /**
     * Get public account
     *
     * @param publicKey the public key
     * @param networkType the {@link NetworkType}
     * @return public account
     */
    public static PublicAccount getPublicAccount(String publicKey, NetworkType networkType) {
        return new PublicAccount(publicKey, networkType);
    }

    /**
     * Get public account if possible
     *
     * @param publicKey the public key
     * @param networkType the {@link NetworkType}
     * @return public account or empty if no public key is provided.
     */
    public static Optional<PublicAccount> getPublicAccount(
        Optional<String> publicKey, NetworkType networkType) {
        if (publicKey.isPresent() && !publicKey.get().isEmpty()) {
            return Optional.of(new PublicAccount(publicKey.get(), networkType));
        } else {
            return Optional.empty();
        }
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
     * Returns number of transactions included the block.
     *
     * @return Integer
     */
    public Integer getNumTransactions() {
        return numTransactions;
    }

    /**
     * Returns number of statements included the block.
     *
     * @return optional of Integer
     */
    public Optional<Integer> getNumStatements() {
        return numStatements;
    }

    /**
     * Gets a list of transactions.
     *
     * @return List of transactions.
     */
    public List<String> getSubCacheMerkleRoots() {
        return subCacheMerkleRoots;
    }

    /**
     * The signature was generated by the signerPublicAccount and can be used to validate that the blockchain data was
     * not modified by a node.
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
    public int getType() {
        return type;
    }

    /**
     * Returns height of which the block was confirmed. Each block has a unique height. Subsequent blocks differ in
     * height by 1.
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
     * @return Integer
     */
    public Integer getFeeMultiplier() {
        return feeMultiplier;
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
     * Returns the beneficiary public account.
     *
     * @return PublicAccount
     */
    public PublicAccount getBeneficiaryPublicAccount() {
        return beneficiaryPublicAccount;
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
}
