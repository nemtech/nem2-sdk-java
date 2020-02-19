package io.nem.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class NetworkHarvestCatHarvestTest {

    private NetworkCurrency networkCurrency = NetworkCurrency.CAT_HARVEST;

    @Test
    void shouldCreateRelativeNetworkHarvestMosaic() {
        Mosaic currency = networkCurrency.createRelative(BigInteger.valueOf(1000));
        assertEquals(BigInteger.valueOf(1000 * 1000), currency.getAmount());
        assertEquals(networkCurrency.getNamespaceId().get(), currency.getId());
        assertEquals("941299b2b7e1291c", currency.getIdAsHex());
    }

    @Test
    void shouldCreateAbsoluteNetworkHarvestMosaic() {
        Mosaic currency = networkCurrency.createAbsolute(BigInteger.valueOf(1));
        assertEquals(BigInteger.valueOf(1), currency.getAmount());
        assertEquals(networkCurrency.getNamespaceId().get(), currency.getId());
        assertEquals("941299b2b7e1291c", currency.getIdAsHex());
    }

    @Test
    void shouldCompareNamespaceIdsForEquality() {
        NamespaceId namespaceId = NamespaceId
            .createFromId(BigInteger.valueOf(-7776984613647210212L));
        assertEquals(-7776984613647210212L, namespaceId.getIdAsLong());
        assertEquals(networkCurrency.getNamespaceId().get().getIdAsLong(),
            namespaceId.getIdAsLong());
        assertEquals(networkCurrency.getNamespaceId().get().getIdAsHex(), namespaceId.getIdAsHex());
    }

    @Test
    @SuppressWarnings("squid:S3415")
    void shouldHaveValidStatics() {
        assertEquals(networkCurrency.getUnresolvedMosaicId(),
            networkCurrency.getNamespaceId().get());
        assertEquals("cat.harvest", networkCurrency.getNamespaceId().get().getFullName().get());
        assertEquals(3, networkCurrency.getDivisibility());
        assertTrue(networkCurrency.isTransferable());
        assertTrue(networkCurrency.isSupplyMutable());
    }
}
