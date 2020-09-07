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
 * Tests of SecretLockInfo
 */
public class SecretLockInfoTest {

    @Test
    void constructor() {

        Optional<String> recordId = Optional.of("abc");

        Address ownerAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
        Address recipientAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
        MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), ownerAddress);
        BigInteger amount = BigInteger.ONE;
        BigInteger endHeight = BigInteger.TEN;
        Integer status = 3;
        String hash = "ABC";

        SecretHashAlgorithm hashAlgorithm = SecretHashAlgorithm.HASH_256;
        String secret = "SomeSecret";

        SecretLockInfo info = new SecretLockInfo(recordId, ownerAddress, mosaicId, amount, endHeight, status,
            hashAlgorithm, secret, recipientAddress, hash);

        Assertions.assertEquals(recordId, info.getRecordId());
        Assertions.assertEquals(ownerAddress, info.getOwnerAddress());
        Assertions.assertEquals(mosaicId, info.getMosaicId());
        Assertions.assertEquals(amount, info.getAmount());
        Assertions.assertEquals(endHeight, info.getEndHeight());
        Assertions.assertEquals(status, info.getStatus());
        Assertions.assertEquals(hash, info.getCompositeHash());
        Assertions.assertEquals(hashAlgorithm, info.getHashAlgorithm());
        Assertions.assertEquals(recipientAddress, info.getRecipientAddress());
    }

}
