package io.nem.symbol.sdk.api;


import io.nem.symbol.sdk.model.account.Address;
import java.util.Objects;

/**
 * Criteria used to search addresses.
 */
public class HashLockSearchCriteria extends SearchCriteria<HashLockSearchCriteria> {

    /**
     * Account address.
     */
    private final Address address;

    public HashLockSearchCriteria(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
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
        HashLockSearchCriteria that = (HashLockSearchCriteria) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }
}
