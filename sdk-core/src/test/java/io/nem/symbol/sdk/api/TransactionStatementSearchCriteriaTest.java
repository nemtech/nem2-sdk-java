package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionStatementSearchCriteriaTest {

    @Test
    void shouldCreate() {
        TransactionStatementSearchCriteria criteria = new TransactionStatementSearchCriteria();
        Assertions.assertNull(criteria.getOrder());
        Assertions.assertNull(criteria.getPageSize());
        Assertions.assertNull(criteria.getPageNumber());
        Assertions.assertNull(criteria.getOffset());
        Assertions.assertNull(criteria.getHeight());
        Assertions.assertNull(criteria.getReceiptType());
        Assertions.assertNull(criteria.getArtifactId());
        Assertions.assertNull(criteria.getTargetAddress());
        Assertions.assertNull(criteria.getRecipientAddress());
    }

    @Test
    void shouldSetValues() {

        Address address = Address.generateRandom(NetworkType.MIJIN_TEST);

        TransactionStatementSearchCriteria criteria = new TransactionStatementSearchCriteria();

        criteria.setOrder(OrderBy.DESC);
        criteria.setPageSize(10);
        criteria.setPageNumber(5);
        criteria.setOffset("abc");
        criteria.setHeight(BigInteger.ONE);

        Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
        Assertions.assertEquals(10, criteria.getPageSize());
        Assertions.assertEquals(5, criteria.getPageNumber());
        Assertions.assertEquals(BigInteger.ONE, criteria.getHeight());
        Assertions.assertEquals("abc", criteria.getOffset());
    }

    @Test
    void shouldUseBuilderMethods() {

        TransactionStatementSearchCriteria criteria = new TransactionStatementSearchCriteria().height(BigInteger.ONE);
        criteria.order(OrderBy.ASC).pageSize(10).pageNumber(5);

        criteria.offset("abc");
        Assertions.assertEquals(OrderBy.ASC, criteria.getOrder());
        Assertions.assertEquals(10, criteria.getPageSize());
        Assertions.assertEquals(5, criteria.getPageNumber());
        Assertions.assertEquals(BigInteger.ONE, criteria.getHeight());
        Assertions.assertEquals("abc", criteria.getOffset());
    }

    @Test
    void shouldBeEquals() {

        TransactionStatementSearchCriteria criteria1 = new TransactionStatementSearchCriteria().order(OrderBy.ASC)
            .pageSize(10).pageNumber(5).height(BigInteger.ONE);
        criteria1.offset("abc");

        TransactionStatementSearchCriteria criteria2 = new TransactionStatementSearchCriteria().order(OrderBy.ASC)
            .pageSize(10).pageNumber(5).height(BigInteger.ONE);
        criteria2.offset("abc");

        Assertions.assertEquals(new TransactionStatementSearchCriteria(), new TransactionStatementSearchCriteria());
        Assertions.assertEquals(criteria1, criteria2);
        Assertions.assertEquals(criteria1, criteria1);
        Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());

        criteria1.setHeight(BigInteger.TEN);
        Assertions.assertNotEquals(criteria1, criteria2);
        Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

        Assertions.assertNotEquals("ABC", criteria2);
    }
}
