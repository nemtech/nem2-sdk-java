package io.nem.sdk.model.receipt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatementTest {

    private final BigInteger height = BigInteger.valueOf(1473L);
    private final BigInteger height2 = BigInteger.valueOf(1500L);
    private NetworkType networkType = NetworkType.MIJIN_TEST;

    private Account account;
    private List<TransactionStatement> transactionStatements;
    private List<AddressResolutionStatement> addressResolutionStatements;
    private List<MosaicResolutionStatement> mosaicResolutionStatements;
    private Statement statement;

    private MosaicId mosaicId1 = new MosaicId("AAAAAAAAAAAAAAA1");
    private MosaicId mosaicId2 = new MosaicId("AAAAAAAAAAAAAAA2");
    private MosaicId mosaicId3 = new MosaicId("AAAAAAAAAAAAAAA3");
    private MosaicId mosaicId4 = new MosaicId("AAAAAAAAAAAAAAA4");
    private NamespaceId mosaicNamespace1 = NamespaceId.createFromName("mosaicnamespace1");
    private NamespaceId mosaicNamespace3 = NamespaceId.createFromName("mosaicnamespace3");
    private NamespaceId mosaicNamespace4 = NamespaceId.createFromName("mosaicnamespace4");
    private Address address1 = Account.generateNewAccount(networkType).getAddress();
    private NamespaceId addressNamespace1 = NamespaceId.createFromName("addressnamespace1");


    @Test
    void shouldCreateAddressResolutionTransactionStatement() {
        Statement statement =
            new Statement(
                transactionStatements, addressResolutionStatements, mosaicResolutionStatements);
        assertEquals(statement.getAddressResolutionStatements(), addressResolutionStatements);
        assertEquals(statement.getMosaicResolutionStatement(), mosaicResolutionStatements);
        assertEquals(statement.getTransactionStatements(), transactionStatements);
    }

    @BeforeEach
    void setupStatement() {
        account = Account.createFromPrivateKey(
            "81C18245507F9C15B61BDEDAFA2C10D9DC2C4E401E573A10935D45AA2A461FD5",
            NetworkType.MIJIN_TEST);

        transactionStatements = Collections.emptyList();

        addressResolutionStatements = Collections.singletonList(
            new AddressResolutionStatement(height, addressNamespace1,
                Collections.singletonList(new ResolutionEntry<>(address1,
                    new ReceiptSource(1, 0), ReceiptType.ADDRESS_ALIAS_RESOLUTION))));

        MosaicResolutionStatement mosaicResolutionStatement1 = new MosaicResolutionStatement(
            height, mosaicNamespace1,
            Arrays.asList(new ResolutionEntry<>(mosaicId1,
                    new ReceiptSource(1, 0), ReceiptType.MOSAIC_ALIAS_RESOLUTION),
                new ResolutionEntry<>(mosaicId2,
                    new ReceiptSource(3, 5), ReceiptType.MOSAIC_ALIAS_RESOLUTION)));

        MosaicResolutionStatement mosaicResolutionStatement2 = new MosaicResolutionStatement(
            height, mosaicNamespace3,
            Collections.singletonList(new ResolutionEntry<>(mosaicId3,
                new ReceiptSource(3, 1), ReceiptType.MOSAIC_ALIAS_RESOLUTION)));

        MosaicResolutionStatement mosaicResolutionStatement3 = new MosaicResolutionStatement(
            height2, mosaicNamespace4,
            Arrays.asList(
                new ResolutionEntry<>(mosaicId1, new ReceiptSource(1, 1),
                    ReceiptType.MOSAIC_ALIAS_RESOLUTION),
                new ResolutionEntry<>(mosaicId2, new ReceiptSource(1, 4),
                    ReceiptType.MOSAIC_ALIAS_RESOLUTION),
                new ResolutionEntry<>(mosaicId3, new ReceiptSource(1, 7),
                    ReceiptType.MOSAIC_ALIAS_RESOLUTION),
                new ResolutionEntry<>(mosaicId4, new ReceiptSource(2, 4),
                    ReceiptType.MOSAIC_ALIAS_RESOLUTION)
            )
        );

        mosaicResolutionStatements = Arrays
            .asList(mosaicResolutionStatement1, mosaicResolutionStatement2,
                mosaicResolutionStatement3);

        statement = new Statement(transactionStatements, addressResolutionStatements,
            mosaicResolutionStatements);
    }

    @Test
    void shouldGetResolvedEntryWhenPrimaryIdIsGreaterThanMaxMosaicId() {
        Optional<MosaicId> resolved = statement.getResolvedMosaicId(height, mosaicNamespace1, 4, 0);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(mosaicId2, resolved.get());
    }

    @Test
    void shouldGetResolvedEntryWhenPrimaryIdIsGreaterThanMaxAddress() {
        Optional<Address> resolved = statement.getResolvedAddress(height, addressNamespace1, 4, 0);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(address1, resolved.get());
    }

    @Test
    void shouldNotResolveAddressWhenInvalidHeight() {
        Optional<Address> resolved = statement.getResolvedAddress(height2, addressNamespace1, 4, 0);
        Assertions.assertFalse(resolved.isPresent());
    }

    @Test
    void shouldNotResolveMosaicIdWhenInvalidHeight() {
        Optional<MosaicId> resolved = statement.getResolvedMosaicId(height2, addressNamespace1, 4, 0);
        Assertions.assertFalse(resolved.isPresent());
    }

    @Test
    void shouldGetResolvedEntryWhenRealMosaicId() {
        Optional<MosaicId> resolved = statement.getResolvedMosaicId(height, mosaicNamespace1, 4, 0);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(mosaicId2, resolved.get());
    }

    @Test
    void shouldGetResolvedEntryRealAddress() {
        Optional<Address> resolved = statement.getResolvedAddress(height, address1, 4, 0);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(address1, resolved.get());
    }

    @Test
    void shouldGetResolvedEntryWhenPrimaryIdIsInMiddleOf2PirmaryIds() {
        Optional<MosaicId> resolved = statement.getResolvedMosaicId(height, mosaicNamespace1, 2, 1);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(mosaicId1, resolved.get());
    }

    @Test
    void shouldGetResolvedEntryWhenPrimaryIdMatchesButNotSecondaryId() {
        Optional<MosaicId> resolved = statement.getResolvedMosaicId(height, mosaicNamespace1, 3, 6);
        Assertions.assertTrue(resolved.isPresent());
        Assertions.assertEquals(mosaicId2, resolved.get());
    }

    @Test
    void shouldGetResolvedEntryWhenPrimaryIdMatchesButSecondaryIdLessThanMinimum() {
        Optional<Address> resolved = statement.getResolvedAddress(height, mosaicNamespace1, 0, 6);
        Assertions.assertFalse(resolved.isPresent());
    }

    @Test
    void shouldReturnUndefinedAddress() {
        Optional<Address> resolved = statement.getResolvedAddress(height, addressNamespace1, 0, 6);
        Assertions.assertFalse(resolved.isPresent());
    }

    @Test
    void resolutionChangeInTheBlockMoreThanOneAggregate() {
        Optional<Address> resolved = statement.getResolvedAddress(height2, addressNamespace1, 0, 6);
        Assertions.assertFalse(resolved.isPresent());

        Assertions.assertEquals(Optional.of(mosaicId1),
            statement.getResolvedMosaicId(height2, mosaicNamespace4, 1, 1));

        Assertions.assertEquals(Optional.of(mosaicId2),
            statement.getResolvedMosaicId(height2, mosaicNamespace4, 1, 4));

        Assertions.assertEquals(Optional.of(mosaicId3),
            statement.getResolvedMosaicId(height2, mosaicNamespace4, 1, 7));

        Assertions.assertEquals(Optional.of(mosaicId3),
            statement.getResolvedMosaicId(height2, mosaicNamespace4, 2, 1));

        Assertions.assertEquals(Optional.of(mosaicId4),
            statement.getResolvedMosaicId(height2, mosaicNamespace4, 2, 4));

        Assertions.assertEquals(Optional.empty(),
            statement.getResolvedMosaicId(height2, mosaicNamespace4, 1, 0));

        Assertions.assertEquals(Optional.of(mosaicId2),
            statement.getResolvedMosaicId(height2, mosaicNamespace4, 1, 6));

        Assertions.assertEquals(Optional.of(mosaicId1),
            statement.getResolvedMosaicId(height2, mosaicNamespace4, 1, 2));


    }
}
