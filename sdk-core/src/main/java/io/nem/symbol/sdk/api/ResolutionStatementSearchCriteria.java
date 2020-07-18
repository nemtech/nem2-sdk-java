package io.nem.symbol.sdk.api;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Criteria used to filter resolutions.
 */
public class ResolutionStatementSearchCriteria extends SearchCriteria<ResolutionStatementSearchCriteria> {

    /**
     * Return recipients for only this block height.
     */
    private BigInteger height;

    public BigInteger getHeight() {
        return height;
    }

    public void setHeight(BigInteger height) {
        this.height = height;
    }

    /**
     * Sets the height filter builder style.
     *
     * @param height filter the receipt by height.
     * @return this builder.
     */
    public ResolutionStatementSearchCriteria height(BigInteger height) {
        this.height = height;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResolutionStatementSearchCriteria that = (ResolutionStatementSearchCriteria) o;
        return Objects.equals(height, that.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height);
    }
}
