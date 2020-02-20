package io.nem.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class NetworkCurrencyCatCurrencyTest {

    private NetworkCurrency networkCurrency = NetworkCurrency.CAT_CURRENCY;

    @Test
    void shouldCreateRelativeMosaic() {
        Mosaic currency = networkCurrency.createRelative(BigInteger.valueOf(1000));
        assertEquals(BigInteger.valueOf(1000 * 1000000), currency.getAmount());
        assertEquals(networkCurrency.getNamespaceId().get(), currency.getId());
        assertEquals("85BBEA6CC462B244", currency.getIdAsHex());
    }

    @Test
    void shouldCreateRelativeMosaicUsingBigDecimal() {
        Mosaic currency = networkCurrency.createRelative(BigDecimal.valueOf(0.000001));
        assertEquals(BigInteger.valueOf((long) (0.000001 * 1000000)), currency.getAmount());
        assertEquals(networkCurrency.getNamespaceId().get(), currency.getId());
        assertEquals("85BBEA6CC462B244", currency.getIdAsHex());
    }

    @Test
    void shouldCreateAbsoluteMosaic() {
        Mosaic currency = networkCurrency.createAbsolute(BigInteger.valueOf(1));
        assertEquals(BigInteger.valueOf(1), currency.getAmount());
        assertEquals(networkCurrency.getNamespaceId().get(), currency.getId());
        assertEquals("85BBEA6CC462B244", currency.getIdAsHex());
    }

    @Test
    void shouldCompareNamespaceIdsForEquality() {
        NamespaceId namespaceId = NamespaceId
            .createFromId(BigInteger.valueOf(-8810190493148073404L));
        assertEquals(-8810190493148073404L, namespaceId.getIdAsLong());
        assertEquals(networkCurrency.getNamespaceId().get().getIdAsLong(),
            namespaceId.getIdAsLong());
        assertEquals(networkCurrency.getNamespaceId().get().getIdAsHex(), namespaceId.getIdAsHex());

        // Note:
        // BigInteger decimal generated from namespace path vs generated using Lower and Higher integers
        // Using namespace path:     9636553580561478212 (Decimal number)
        // Using Lower and Higher:  -8810190493148073404 (Decimal from signed 2's complement)
        // In Hexadecimal:              85bbea6cc462b244 (same for both)
    }

    @Test
    @SuppressWarnings("squid:S3415")
    void shouldHaveValidStatics() {
        assertEquals(networkCurrency.getUnresolvedMosaicId(),
            networkCurrency.getNamespaceId().get());
        assertEquals("cat.currency", networkCurrency.getNamespaceId().get().getFullName().get());
        assertEquals(6, networkCurrency.getDivisibility());
        assertTrue(networkCurrency.isTransferable());
        assertFalse(networkCurrency.isSupplyMutable());
    }
}
