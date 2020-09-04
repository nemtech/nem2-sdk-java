package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests of HashLockInfo
 */
public class HashLockInfoTest {

    @Test
    void constructor() {

        Optional<String> recordId = Optional.of("abc");

        Address ownerAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
        MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), ownerAddress);
        BigInteger amount = BigInteger.ONE;
        BigInteger endHeight = BigInteger.TEN;
        Integer status = 3;
        String hash = "ABC";

        HashLockInfo info = new HashLockInfo(recordId, ownerAddress, mosaicId, amount, endHeight, status, hash);

        Assertions.assertEquals(recordId, info.getRecordId());
        Assertions.assertEquals(ownerAddress, info.getOwnerAddress());
        Assertions.assertEquals(mosaicId, info.getMosaicId());
        Assertions.assertEquals(amount, info.getAmount());
        Assertions.assertEquals(endHeight, info.getEndHeight());
        Assertions.assertEquals(status, info.getStatus());
        Assertions.assertEquals(hash, info.getHash());
    }

}
