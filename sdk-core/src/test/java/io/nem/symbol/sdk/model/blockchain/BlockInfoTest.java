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
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockInfoTest {

  @Test
  void createANewBlockInfo() {

    String proofGamma = "proofGamma";
    String proofScalar = "proofScalar";
    String proofVerificationHash = "proofVerificationHash";

    String hash = "24E92B511B54EDB48A4850F9B42485FDD1A30589D92C775632DDDD71D7D1D691";
    String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
    String signature =
        "37351C8244AC166BE6664E3FA954E99A3239AC46E51E2B32CEA1C72DD0851100A7731868E932E1A9BEF8A27D48E1"
            + "FFEE401E933EB801824373E7537E51733E0F";
    PublicAccount signer = Account.generateNewAccount(NetworkType.MIJIN_TEST).getPublicAccount();
    String previousBlockHash = "0000000000000000000000000000000000000000000000000000000000000000";
    String blockTransactionsHash =
        "702090BA31CEF9E90C62BBDECC0CCCC0F88192B6625839382850357F70DD68A0";
    String blockReceiptsHash = "702090BA31CEF9E90C62BBDECC0CCCC0F88192B6625839382850357F70DD68A0";
    String stateHash = "702090BA31CEF9E90C62BBDECC0CCCC0F88192B6625839382850357F70DD68A0";
    List<String> subCacheMerkleRoots = new ArrayList<>();
    Address beneficiaryAddress = Account.generateNewAccount(NetworkType.MIJIN_TEST).getAddress();
    String id = "abc";
    List<String> stateHashSubCacheMerkleRoots = Arrays.asList("abc");
    BlockInfo blockInfo =
        new BlockInfo(
            id,
            10L,
            hash,
            generationHash,
            BigInteger.ZERO,
            stateHashSubCacheMerkleRoots,
            25,
            20,
            35,
            subCacheMerkleRoots,
            signature,
            signer,
            NetworkType.MIJIN_TEST,
            1,
            32768,
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.valueOf(276447232L),
            1L,
            previousBlockHash,
            blockTransactionsHash,
            blockReceiptsHash,
            stateHash,
            proofGamma,
            proofScalar,
            proofVerificationHash,
            beneficiaryAddress);

    assertEquals(hash, blockInfo.getHash());
    assertEquals(generationHash, blockInfo.getGenerationHash());
    assertEquals(BigInteger.valueOf(0), blockInfo.getTotalFee());
    assertEquals(10L, blockInfo.getSize());
    assertEquals(25, blockInfo.getTransactionsCount());
    assertEquals(20, blockInfo.getTotalTransactionsCount());
    assertEquals(35, blockInfo.getStatementsCount());
    assertEquals(signature, blockInfo.getSignature());
    Assertions.assertEquals(signer, blockInfo.getSignerPublicAccount());
    assertEquals(NetworkType.MIJIN_TEST, blockInfo.getNetworkType());
    assertEquals(1, (int) blockInfo.getVersion());
    assertEquals(32768, blockInfo.getType());
    assertEquals(BigInteger.valueOf(1), blockInfo.getHeight());
    assertEquals(BigInteger.valueOf(0), blockInfo.getTimestamp());
    assertEquals(BigInteger.valueOf(276447232), blockInfo.getDifficulty());
    assertEquals(1, blockInfo.getFeeMultiplier());
    assertEquals(previousBlockHash, blockInfo.getPreviousBlockHash());
    assertEquals(blockTransactionsHash, blockInfo.getBlockTransactionsHash());
    assertEquals(blockReceiptsHash, blockInfo.getBlockReceiptsHash());
    assertEquals(stateHash, blockInfo.getStateHash());
    assertEquals(beneficiaryAddress, blockInfo.getBeneficiaryAddress());
    assertEquals(proofGamma, blockInfo.getProofGamma());
    assertEquals(proofScalar, blockInfo.getProofScalar());
    assertEquals(proofVerificationHash, blockInfo.getProofVerificationHash());
    assertEquals("abc", blockInfo.getRecordId().get());
    assertEquals(stateHashSubCacheMerkleRoots, blockInfo.getStateHashSubCacheMerkleRoots());
  }
}
