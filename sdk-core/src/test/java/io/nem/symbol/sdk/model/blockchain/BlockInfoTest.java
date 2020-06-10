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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockInfoTest {

    private BlockInfo blockInfo;
    private BlockInfo blockInfo2;
    private BlockInfo blockInfo3;
    private String hash;
    private String generationHash;
    private String signature;
    private PublicAccount signer;
    private String previousBlockHash;
    private String blockTransactionsHash;
    private String blockReceiptsHash;
    private String stateHash;
    private Address beneficiaryAddress;
    private List<String> subCacheMerkleRoots;
    private final String proofGamma = "proofGamma";
    private final String proofScalar = "proofScalar";
    private final String proofVerificationHash = "proofVerificationHash";
    private String id;

    @BeforeAll
    void setup() {
        hash = "24E92B511B54EDB48A4850F9B42485FDD1A30589D92C775632DDDD71D7D1D691";
        generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
        signature =
            "37351C8244AC166BE6664E3FA954E99A3239AC46E51E2B32CEA1C72DD0851100A7731868E932E1A9BEF8A27D48E1"
                + "FFEE401E933EB801824373E7537E51733E0F";
        signer = Account.generateNewAccount(NetworkType.MIJIN_TEST).getPublicAccount();
        previousBlockHash = "0000000000000000000000000000000000000000000000000000000000000000";
        blockTransactionsHash = "702090BA31CEF9E90C62BBDECC0CCCC0F88192B6625839382850357F70DD68A0";
        blockReceiptsHash = "702090BA31CEF9E90C62BBDECC0CCCC0F88192B6625839382850357F70DD68A0";
        stateHash = "702090BA31CEF9E90C62BBDECC0CCCC0F88192B6625839382850357F70DD68A0";
        subCacheMerkleRoots = new ArrayList<>();
        beneficiaryAddress = Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress();
        id = "abc";
        blockInfo =
            new BlockInfo(
                id,
                hash,
                generationHash,
                BigInteger.ZERO,
                25,
                Optional.of(35),
                subCacheMerkleRoots,
                signature,
                signer,
                NetworkType.MIJIN_TEST,
                1,
                32768,
                BigInteger.ONE,
                BigInteger.ZERO,
                BigInteger.valueOf(276447232L),
                1,
                previousBlockHash,
                blockTransactionsHash,
                blockReceiptsHash,
                stateHash,
                proofGamma,
                proofScalar,
                proofVerificationHash,
                beneficiaryAddress);
    }

    @Test
    void createANewBlockInfo() {
        assertEquals(hash, blockInfo.getHash());
        assertEquals(generationHash, blockInfo.getGenerationHash());
        assertEquals(BigInteger.valueOf(0), blockInfo.getTotalFee());
        assertEquals(25, blockInfo.getNumTransactions());
        assertEquals(35, blockInfo.getNumStatements().get());
        assertEquals(signature, blockInfo.getSignature());
        Assertions.assertEquals(signer, blockInfo.getSignerPublicAccount());
        assertEquals(NetworkType.MIJIN_TEST, blockInfo.getNetworkType());
        assertEquals(1, (int) blockInfo.getVersion());
        assertEquals(32768, blockInfo.getType());
        assertEquals(BigInteger.valueOf(1), blockInfo.getHeight());
        assertEquals(BigInteger.valueOf(0), blockInfo.getTimestamp());
        assertEquals(BigInteger.valueOf(276447232), blockInfo.getDifficulty());
        assertEquals(1, (int) blockInfo.getFeeMultiplier());
        assertEquals(previousBlockHash, blockInfo.getPreviousBlockHash());
        assertEquals(blockTransactionsHash, blockInfo.getBlockTransactionsHash());
        assertEquals(blockReceiptsHash, blockInfo.getBlockReceiptsHash());
        assertEquals(stateHash, blockInfo.getStateHash());
        assertEquals(beneficiaryAddress, blockInfo.getBeneficiaryAddress());
        assertEquals(proofGamma, blockInfo.getProofGamma());
        assertEquals(proofScalar, blockInfo.getProofScalar());
        assertEquals(proofVerificationHash, blockInfo.getProofVerificationHash());
        assertEquals("abc", blockInfo.getRecordId().get());
    }
}
