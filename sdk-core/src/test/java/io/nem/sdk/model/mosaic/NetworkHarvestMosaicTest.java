package io.nem.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class NetworkHarvestMosaicTest {

    @Test
    void shouldCreateNetworkHarvestMosaicViaConstructor() {
        NetworkHarvestMosaic currency = new NetworkHarvestMosaic(BigInteger.valueOf(0));
        assertEquals(BigInteger.valueOf(0), currency.getAmount());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID, currency.getId());
        assertEquals("941299B2B7E1291C", currency.getIdAsHex());
    }

    @Test
    void shouldCreateRelativeNetworkHarvestMosaic() {
        NetworkHarvestMosaic currency = NetworkHarvestMosaic
            .createRelative(BigInteger.valueOf(1000));
        assertEquals(BigInteger.valueOf(1000 * 1000), currency.getAmount());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID, currency.getId());
        assertEquals("941299B2B7E1291C", currency.getIdAsHex());
    }

    @Test
    void shouldCreateAbsoluteNetworkHarvestMosaic() {
        NetworkHarvestMosaic currency = NetworkHarvestMosaic.createAbsolute(BigInteger.valueOf(1));
        assertEquals(BigInteger.valueOf(1), currency.getAmount());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID, currency.getId());
        assertEquals("941299B2B7E1291C", currency.getIdAsHex());
    }

    @Test
    void shouldCompareNamespaceIdsForEquality() {
        NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.valueOf(-7776984613647210212L));
        assertEquals(-7776984613647210212L, namespaceId.getIdAsLong());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID.getIdAsLong(), namespaceId.getIdAsLong());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID.getIdAsHex(), namespaceId.getIdAsHex());
    }

    @Test
    @SuppressWarnings("squid:S3415")
    void shouldHaveValidStatics() {
        assertEquals(3, NetworkHarvestMosaic.DIVISIBILITY);
        assertEquals(BigInteger.valueOf(15000000L), NetworkHarvestMosaic.INITIALSUPPLY);
        assertTrue(NetworkHarvestMosaic.TRANSFERABLE);
        assertTrue(NetworkHarvestMosaic.SUPPLYMUTABLE);
    }
}
