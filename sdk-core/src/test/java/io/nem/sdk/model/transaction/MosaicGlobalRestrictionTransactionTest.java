package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MosaicGlobalRestrictionTransactionTest {

    static Account account;
    static String generationHash;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930",
                NetworkType.MIJIN_TEST);
        generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
    }

    @Test
    void createAMosaicGlobalRestrictionTransactionViaStaticConstructor() {
        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTx =
            MosaicGlobalRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("1")), // restrictedMosaicId
                new MosaicId(new BigInteger("2")), // referenceMosaicId
                BigInteger.valueOf(1),    // restrictionKey
                BigInteger.valueOf(9),    // previousRestrictionValue
                MosaicRestrictionType.EQ, // previousRestrictionType
                BigInteger.valueOf(8),    // newRestrictionValue
                MosaicRestrictionType.GE  // newRestrictionType
            ).build();

        assertEquals(NetworkType.MIJIN_TEST, mosaicGlobalRestrictionTx.getNetworkType());
        assertTrue(1 == mosaicGlobalRestrictionTx.getVersion());
        assertTrue(LocalDateTime.now().isBefore(mosaicGlobalRestrictionTx.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), mosaicGlobalRestrictionTx.getMaxFee());
        assertEquals(new BigInteger("1"), mosaicGlobalRestrictionTx.getMosaicId().getId());
        assertEquals(new BigInteger("2"), mosaicGlobalRestrictionTx.getReferenceMosaicId().getId());
        assertEquals(BigInteger.valueOf(1), mosaicGlobalRestrictionTx.getRestrictionKey());
        assertEquals(BigInteger.valueOf(9), mosaicGlobalRestrictionTx.getPreviousRestrictionValue());
        assertEquals(MosaicRestrictionType.EQ, mosaicGlobalRestrictionTx.getPreviousRestrictionType());
        assertEquals(BigInteger.valueOf(8), mosaicGlobalRestrictionTx.getNewRestrictionValue());
        assertEquals(MosaicRestrictionType.GE, mosaicGlobalRestrictionTx.getNewRestrictionType());
    }

    @Test
    void serializeAndSignTransaction() {
        MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTx =
            MosaicGlobalRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("1")), // restricted MosaicId
                new MosaicId(new BigInteger("2")), // referenceMosaicId
                BigInteger.valueOf(1),    // restrictionKey
                BigInteger.valueOf(9),    // previousRestrictionValue
                MosaicRestrictionType.EQ, // previousRestrictionType
                BigInteger.valueOf(8),    // newRestrictionValue
                MosaicRestrictionType.GE  // newRestrictionType
            ).build();

        SignedTransaction signedTransaction = mosaicGlobalRestrictionTx.signWith(account, generationHash);

        assertEquals("010000000000000002000000000000000100000000000000090000000000000001080000000000000006",
            signedTransaction.getPayload().substring(240));
    }
}
