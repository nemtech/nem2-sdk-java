package io.nem.symbol.sdk.api;


import io.nem.symbol.sdk.model.account.Address;
import java.util.Objects;

/**
 * Criteria used to search secret lock entities.
 */
public class SecretLockSearchCriteria extends SearchCriteria<SecretLockSearchCriteria> {

    /**
     * Account address.
     */
    private final Address address;

    public SecretLockSearchCriteria(Address address) {
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
        SecretLockSearchCriteria that = (SecretLockSearchCriteria) o;
        return Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }
}
