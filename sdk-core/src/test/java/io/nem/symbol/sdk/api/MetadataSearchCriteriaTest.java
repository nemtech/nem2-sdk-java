package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MetadataSearchCriteria}
 */
public class MetadataSearchCriteriaTest {

    @Test
    void shouldCreate() {
        MetadataSearchCriteria criteria = new MetadataSearchCriteria();
        Assertions.assertNull(criteria.getOrder());
        Assertions.assertNull(criteria.getPageSize());
        Assertions.assertNull(criteria.getPageNumber());
        Assertions.assertNull(criteria.getOffset());

        Assertions.assertNull(criteria.getMetadataType());
        Assertions.assertNull(criteria.getScopedMetadataKey());
        Assertions.assertNull(criteria.getSourceAddress());
        Assertions.assertNull(criteria.getTargetAddress());
        Assertions.assertNull(criteria.getTargetId());
    }

    @Test
    void shouldSetValues() {

        Address sourceAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
        Address targetAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
        MetadataSearchCriteria criteria = new MetadataSearchCriteria();
        criteria.setOrder(OrderBy.DESC);
        criteria.setPageSize(10);
        criteria.setPageNumber(5);
        criteria.setOffset("abc");

        criteria.setMetadataType(MetadataType.MOSAIC);
        criteria.setScopedMetadataKey(BigInteger.ONE);
        criteria.setSourceAddress(sourceAddress);
        criteria.setTargetAddress(targetAddress);
        criteria.setTargetId("123");

        Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
        Assertions.assertEquals(10, criteria.getPageSize());
        Assertions.assertEquals(5, criteria.getPageNumber());
        Assertions.assertEquals("abc", criteria.getOffset());

        Assertions.assertEquals(sourceAddress, criteria.getSourceAddress());
        Assertions.assertEquals(targetAddress, criteria.getTargetAddress());
        Assertions.assertEquals("123", criteria.getTargetId());
        Assertions.assertEquals(MetadataType.MOSAIC, criteria.getMetadataType());
        Assertions.assertEquals(BigInteger.ONE, criteria.getScopedMetadataKey());
    }


    @Test
    void shouldUseBuilderMethods() {

        Address sourceAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
        Address targetAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
        MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), sourceAddress);
        MetadataSearchCriteria criteria = new MetadataSearchCriteria().order(OrderBy.DESC).pageSize(10).pageNumber(5)
            .offset("abc").metadataType(MetadataType.MOSAIC).scopedMetadataKey(BigInteger.ONE)
            .sourceAddress(sourceAddress).targetAddress(targetAddress).targetId(mosaicId);

        Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
        Assertions.assertEquals(10, criteria.getPageSize());
        Assertions.assertEquals(5, criteria.getPageNumber());
        Assertions.assertEquals("abc", criteria.getOffset());

        Assertions.assertEquals(sourceAddress, criteria.getSourceAddress());
        Assertions.assertEquals(targetAddress, criteria.getTargetAddress());
        Assertions.assertEquals(mosaicId.getIdAsHex(), criteria.getTargetId());
        Assertions.assertEquals(MetadataType.MOSAIC, criteria.getMetadataType());
        Assertions.assertEquals(BigInteger.ONE, criteria.getScopedMetadataKey());
    }

    @Test
    void shouldBeEquals() {

        Address sourceAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
        Address targetAddress = Address.generateRandom(NetworkType.MIJIN_TEST);
        MosaicId mosaicId = MosaicId.createFromNonce(MosaicNonce.createRandom(), sourceAddress);

        MetadataSearchCriteria criteria1 = new MetadataSearchCriteria().order(OrderBy.DESC).pageSize(10).pageNumber(5)
            .offset("abc").metadataType(MetadataType.MOSAIC).scopedMetadataKey(BigInteger.ONE)
            .sourceAddress(sourceAddress).targetAddress(targetAddress).targetId(mosaicId);

        MetadataSearchCriteria criteria2 = new MetadataSearchCriteria().order(OrderBy.DESC).pageSize(10).pageNumber(5)
            .offset("abc").metadataType(MetadataType.MOSAIC).scopedMetadataKey(BigInteger.ONE)
            .sourceAddress(sourceAddress).targetAddress(targetAddress).targetId(mosaicId);

        Assertions.assertEquals(criteria1, criteria1);
        Assertions.assertEquals(criteria1, criteria2);
        Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());

        criteria2.targetId(NamespaceId.createFromName("somealias"));
        Assertions.assertEquals(criteria1, criteria1);
        Assertions.assertNotEquals(criteria1, criteria2);
        Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());
    }
}
