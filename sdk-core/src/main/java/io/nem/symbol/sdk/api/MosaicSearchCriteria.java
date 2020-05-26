package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.Address;
import java.util.Objects;

/**
 * Criteria used to search mosaics via rest.
 */
public class MosaicSearchCriteria extends SearchCriteria {

    /**
     * Filter by owner address (optional).
     */
    private Address ownerAddress;

    /**
     * Entry id at which to start pagination. If the ordering parameter is set to DESC, the elements returned precede
     * the identifier. Otherwise, newer elements with respect to the id are returned.  (optional)
     */
    private String offset;

    public Address getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(Address ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    /**
     * Sets the address owner builder style.
     *
     * @param ownerAddress the filter by owner address
     * @return this criteria.
     */
    public MosaicSearchCriteria ownerAddress(Address ownerAddress) {
        this.ownerAddress = ownerAddress;
        return this;
    }

    /**
     * Sets the page size builder style.
     *
     * @param pageSize the page size.
     * @return this object.
     */
    public MosaicSearchCriteria pageSize(Integer pageSize) {
        return (MosaicSearchCriteria) super.pageSize(pageSize);
    }

    /**
     * Sets the order builder style.
     *
     * @param order the order.
     * @return this object.
     */
    @Override
    public MosaicSearchCriteria order(OrderBy order) {
        return (MosaicSearchCriteria) super.order(order);
    }

    /**
     * Sets the page number builder style.
     *
     * @param pageNumber the page number.
     * @return this object.
     */
    @Override
    public MosaicSearchCriteria pageNumber(Integer pageNumber) {
        return (MosaicSearchCriteria) super.pageNumber(pageNumber);
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    /**
     * Sets the offset builder style.
     *
     * @param offset the new offset
     * @return this criteria.
     */
    public MosaicSearchCriteria offset(String offset) {
        this.offset = offset;
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
        if (!super.equals(o)) {
            return false;
        }
        MosaicSearchCriteria that = (MosaicSearchCriteria) o;
        return Objects.equals(ownerAddress, that.ownerAddress) &&
            Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ownerAddress, offset);
    }
}
