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
package io.nem.symbol.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MosaicDefinitionTransactionTest extends AbstractTransactionTester {

  @Test
  void createAMosaicCreationTransactionViaStaticConstructor() {
    Account owner = Account.generateNewAccount(NetworkType.MIJIN_TEST);
    int nonceNumber = 12345;
    MosaicNonce nonce = MosaicNonce.createFromInteger(nonceNumber);
    MosaicDefinitionTransaction mosaicCreationTx =
        MosaicDefinitionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                nonce,
                MosaicId.createFromNonce(nonce, owner.getPublicAccount()),
                MosaicFlags.create(true, true, true),
                4,
                new BlockDuration(222222))
            .deadline(new FakeDeadline())
            .build();

    System.out.println(
        ConvertUtils.toHex(BinarySerializationImpl.INSTANCE.serialize(mosaicCreationTx)));
    assertEquals(NetworkType.MIJIN_TEST, mosaicCreationTx.getNetworkType());
    assertEquals(1, (int) mosaicCreationTx.getVersion());
    assertTrue(LocalDateTime.now().isBefore(mosaicCreationTx.getDeadline().getLocalDateTime()));
    assertEquals(BigInteger.valueOf(0), mosaicCreationTx.getMaxFee());
    //    assertEquals(new BigInteger("0"), mosaicCreationTx.getMosaicId().getId());
    assertTrue(mosaicCreationTx.getMosaicFlags().isSupplyMutable());
    assertTrue(mosaicCreationTx.getMosaicFlags().isTransferable());
    assertTrue(mosaicCreationTx.getMosaicFlags().isRestrictable());
    assertEquals(nonceNumber, mosaicCreationTx.getMosaicNonce().getNonceAsInt());
    assertEquals(4, mosaicCreationTx.getDivisibility());
    assertEquals(new BlockDuration(222222), mosaicCreationTx.getBlockDuration());
  }

  @Test
  @DisplayName("Serialization")
  void serialization() {
    String expected =
        "96000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001904d410000000000000000010000000000000000000000000000001027000000000000000000000504";
    MosaicDefinitionTransaction transaction =
        MosaicDefinitionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                MosaicNonce.createFromBigInteger(new BigInteger("0")),
                new MosaicId(new BigInteger("0")),
                MosaicFlags.create(true, false, true),
                4,
                new BlockDuration(10000))
            .deadline(new FakeDeadline())
            .build();
    assertSerialization(expected, transaction);
  }

  @Test
  @DisplayName("SerializationEmbeddedBytes")
  void shouldGenerateEmbeddedBytes() {
    String expected =
        "460000000000000068b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b7630000000001904d4100000000000000000a00000000000000000000000203";

    NetworkType networkType = NetworkType.MIJIN_TEST;
    MosaicId mosaicId = new MosaicId(new BigInteger("0"));
    BigInteger fee = BigInteger.ONE;
    MosaicNonce mosaicNonce = MosaicNonce.createFromBigInteger(new BigInteger("0"));
    MosaicFlags mosaicFlags = MosaicFlags.create(false, true, false);

    PublicAccount signature =
        PublicAccount.createFromPublicKey(
            "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763",
            NetworkType.MIJIN_TEST);
    TransactionInfo transactionInfo =
        TransactionInfo.createAggregate(
            new BigInteger("121855"),
            1,
            "5A3D23889CD1E800015929A9",
            "3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006",
            "5A0069D83F17CF0001777E55");

    MosaicDefinitionTransaction transaction =
        MosaicDefinitionTransactionFactory.create(
                networkType, mosaicNonce, mosaicId, mosaicFlags, 3, new BlockDuration(10))
            .maxFee(fee)
            .signature("theSigner")
            .signer(signature)
            .transactionInfo(transactionInfo)
            .build();

    assertEmbeddedSerialization(expected, transaction);
  }
}
