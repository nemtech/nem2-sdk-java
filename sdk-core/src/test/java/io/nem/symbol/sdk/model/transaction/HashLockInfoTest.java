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

import io.nem.symbol.catapult.builders.HashLockInfoBuilder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import java.math.BigInteger;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests of HashLockInfo */
public class HashLockInfoTest {

  @Test
  void constructor() {

    Optional<String> recordId = Optional.of("abc");

    Address ownerAddress = Address.createFromRawAddress("SDY3NFHBQAPO7ZBII3USHG2UZHJYD7G7FICKIII");
    MosaicId mosaicId = MosaicId.createFromNonce(new MosaicNonce(1), ownerAddress);
    BigInteger amount = BigInteger.ONE;
    BigInteger endHeight = BigInteger.TEN;
    LockStatus status = LockStatus.USED;
    String hash = "DD9EC2AC9AB11FC7E942E5FA39AF8811180F236E29BCD40DB812392295512F55";

    HashLockInfo info =
        new HashLockInfo(
            recordId.orElse(null), 1, ownerAddress, mosaicId, amount, endHeight, status, hash);

    Assertions.assertEquals(recordId, info.getRecordId());
    Assertions.assertEquals(ownerAddress, info.getOwnerAddress());
    Assertions.assertEquals(mosaicId, info.getMosaicId());
    Assertions.assertEquals(amount, info.getAmount());
    Assertions.assertEquals(endHeight, info.getEndHeight());
    Assertions.assertEquals(status, info.getStatus());
    Assertions.assertEquals(hash, info.getHash());

    byte[] serializedState = info.serialize();
    String expectedHex =
        "010090F1B694E1801EEFE42846E9239B54C9D381FCDF2A04A421E9F49B9E5199FC7601000000000000000A0000000000000001DD9EC2AC9AB11FC7E942E5FA39AF8811180F236E29BCD40DB812392295512F55";
    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(serializedState));
    HashLockInfoBuilder builder =
        HashLockInfoBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(builder.serialize()));
  }
}
