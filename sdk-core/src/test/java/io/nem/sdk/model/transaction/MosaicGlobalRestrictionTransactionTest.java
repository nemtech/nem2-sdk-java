package io.nem.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MosaicGlobalRestrictionTransactionTest extends AbstractTransactionTester {

    static Account account =
        new Account(
            "26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930",
            NetworkType.MIJIN_TEST);
    static String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

    @Test
    void createAMosaicGlobalRestrictionTransactionViaStaticConstructor() {
        MosaicGlobalRestrictionTransaction transaction =
            MosaicGlobalRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("1")), // restrictedMosaicId
                BigInteger.valueOf(1),    // restrictionKey
                BigInteger.valueOf(8),    // newRestrictionValue
                MosaicRestrictionType.GE  // newRestrictionType
            ).referenceMosaicId(new MosaicId(new BigInteger("2")))
                .previousRestrictionValue(BigInteger.valueOf(9))
                .previousRestrictionType(MosaicRestrictionType.EQ).build();

        assertEquals(NetworkType.MIJIN_TEST, transaction.getNetworkType());
        assertTrue(1 == transaction.getVersion());
        assertTrue(LocalDateTime.now()
            .isBefore(transaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), transaction.getMaxFee());
        assertEquals(new BigInteger("1"), transaction.getMosaicId().getId());
        assertEquals(new BigInteger("2"), transaction.getReferenceMosaicId().getId());
        assertEquals(BigInteger.valueOf(1), transaction.getRestrictionKey());
        assertEquals(BigInteger.valueOf(9),
            transaction.getPreviousRestrictionValue());
        assertEquals(MosaicRestrictionType.EQ,
            transaction.getPreviousRestrictionType());
        assertEquals(BigInteger.valueOf(8), transaction.getNewRestrictionValue());
        assertEquals(MosaicRestrictionType.GE, transaction.getNewRestrictionType());
    }

    @Test
    void serializeAndSignTransaction() {
        MosaicGlobalRestrictionTransaction transaction =
            MosaicGlobalRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("1")), // restricted MosaicId
                BigInteger.valueOf(1),    // restrictionKey
                BigInteger.valueOf(8),    // newRestrictionValue
                MosaicRestrictionType.GE  // newRestrictionType
            ).referenceMosaicId(new MosaicId(new BigInteger("2")))
                .previousRestrictionValue(BigInteger.valueOf(9))
                .previousRestrictionType(MosaicRestrictionType.EQ).build();

        SignedTransaction signedTransaction = transaction
            .signWith(account, generationHash);

        assertEquals(
            "1A000000010000000000000002000000000000000100000000000000090000000000000008000000000000000106",
            signedTransaction.getPayload().substring(248));
    }

    @Test
    void serialize() {
        MosaicGlobalRestrictionTransaction transaction =
            MosaicGlobalRestrictionTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                new MosaicId(new BigInteger("3456")), // restricted MosaicId
                BigInteger.valueOf(1),    // restrictionKey
                BigInteger.valueOf(8),    // newRestrictionValue
                MosaicRestrictionType.GE  // newRestrictionType
            ).referenceMosaicId(new MosaicId(new BigInteger("2")))
                .previousRestrictionValue(BigInteger.valueOf(9))
                .previousRestrictionType(MosaicRestrictionType.EQ)
                .deadline(new FakeDeadline())
                .signer(account.getPublicAccount()).build();

        String expected = "aa0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000c2f93346e27ce6ad1a9f8f5e3066f8326593a406bdf357acb041e2f9ab402efe000000000190514100000000000000000100000000000000800d00000000000002000000000000000100000000000000090000000000000008000000000000000106";
        assertSerialization(expected, transaction);

        String expectedEmbeddedHash = "5a00000000000000c2f93346e27ce6ad1a9f8f5e3066f8326593a406bdf357acb041e2f9ab402efe0000000001905141800d00000000000002000000000000000100000000000000090000000000000008000000000000000106";
        assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
    }
}
