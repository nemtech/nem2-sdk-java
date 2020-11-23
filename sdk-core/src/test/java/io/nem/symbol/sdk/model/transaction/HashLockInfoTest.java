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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests of HashLockInfo */
public class HashLockInfoTest {

  @Test
  void constructor() {

    Optional<String> recordId = Optional.of("abc");

    Address ownerAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
    MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), ownerAddress);
    BigInteger amount = BigInteger.ONE;
    BigInteger endHeight = BigInteger.TEN;
    LockStatus status = LockStatus.USED;
    String hash = "ABC";

    HashLockInfo info =
        new HashLockInfo(recordId.get(), ownerAddress, mosaicId, amount, endHeight, status, hash);

    Assertions.assertEquals(recordId, info.getRecordId());
    Assertions.assertEquals(ownerAddress, info.getOwnerAddress());
    Assertions.assertEquals(mosaicId, info.getMosaicId());
    Assertions.assertEquals(amount, info.getAmount());
    Assertions.assertEquals(endHeight, info.getEndHeight());
    Assertions.assertEquals(status, info.getStatus());
    Assertions.assertEquals(hash, info.getHash());
  }
}
