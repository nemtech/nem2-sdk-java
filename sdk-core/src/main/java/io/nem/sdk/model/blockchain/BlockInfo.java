/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.blockchain;

import io.nem.core.crypto.SignSchema;
import io.nem.sdk.model.account.PublicAccount;
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
    private final PublicAccount beneficiaryPublicAccount;

    @SuppressWarnings("squid:S00107")
    private BlockInfo(
        String hash,
        String generationHash,
        BigInteger totalFee,
        Integer numTransactions,
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
        PublicAccount beneficiaryPublicAccount) {
        this.hash = hash;
        this.generationHash = generationHash;
        this.totalFee = totalFee;
        this.numTransactions = numTransactions;
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
        this.beneficiaryPublicAccount = beneficiaryPublicAccount;
    }

    @SuppressWarnings("squid:S00107")
    public static BlockInfo create(
        SignSchema signSchema,
        String hash,
        String generationHash,
        BigInteger totalFee,
        Integer numTransactions,
        List<String> subCacheMerkleRoots,
        String signature,
        String signer,
        Integer blockVersion,
        int type,
        BigInteger height,
        BigInteger timestamp,
        BigInteger difficulty,
        Integer feeMultiplier,
        String previousBlockHash,
        String blockTransactionsHash,
        String blockReceiptsHash,
        String stateHash,
        String beneficiaryPublicKey) {
        NetworkType networkType = BlockInfo.getNetworkType(blockVersion);
        Integer transactionVersion = BlockInfo.getTransactionVersion(blockVersion);
        PublicAccount signerPublicAccount = BlockInfo
            .getPublicAccount(signer, networkType, signSchema);
        PublicAccount beneficiaryPublicAccount =
            BlockInfo.getPublicAccount(beneficiaryPublicKey, networkType, signSchema);
        return new BlockInfo(
            hash,
            generationHash,
            totalFee,
            numTransactions,
            subCacheMerkleRoots,
            signature,
            signerPublicAccount,
            networkType,
            transactionVersion,
            type,
            height,
            timestamp,
            difficulty,
            feeMultiplier,
            previousBlockHash,
            blockTransactionsHash,
            blockReceiptsHash,
            stateHash,
            beneficiaryPublicAccount);
    }

    /**
     * Get network type
     *
     * @return network type
     */
    public static NetworkType getNetworkType(Integer blockVersion) {
        return NetworkType.rawValueOf(
            Integer.parseInt(Integer.toHexString(blockVersion.intValue()).substring(0, 2), 16));
    }

    /**
     * Get transaction version
     *
     * @return transaction version
     */
    public static Integer getTransactionVersion(Integer blockVersion) {
        return Integer.parseInt(Integer.toHexString(blockVersion.intValue()).substring(2, 4), 16);
    }

    /**
     * Get public account
     *
     * @return public account
     */
    public static PublicAccount getPublicAccount(String publicKey, NetworkType networkType,
        SignSchema signSchema) {
        return new PublicAccount(publicKey, networkType, signSchema);
    }

    /**
     * Get public account
     *
     * @return public account
     */
    public static Optional<PublicAccount> getPublicAccount(
        Optional<String> publicKey, NetworkType networkType,
        SignSchema signSchema) {
        if (publicKey.isPresent() && !publicKey.get().isEmpty()) {
            return Optional.of(new PublicAccount(publicKey.get(), networkType, signSchema));
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
     * Returns total fee paid to the account harvesting the block. When generated by listeners
     * optional empty.
     *
     * @return Optional of Integer
     */
    public BigInteger getTotalFee() {
        return totalFee;
    }

    /**
     * Returns number of transactions included the block. When generated by listeners optional
     * empty.
     *
     * @return Optional of Integer
     */
    public Integer getNumTransactions() {
        return numTransactions;
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
    public int getType() {
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
}
