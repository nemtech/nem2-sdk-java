package io.nem.symbol.sdk.api;

import io.nem.symbol.sdk.model.account.Address;
import java.util.Objects;

/**
 * Criteria used to search mosaics via rest.
 */
public class MosaicSearchCriteria extends SearchCriteria<MosaicSearchCriteria> {

    /**
     * Filter by owner address (optional).
     */
    private Address ownerAddress;

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
        return Objects.equals(ownerAddress, that.ownerAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ownerAddress);
    }
}
