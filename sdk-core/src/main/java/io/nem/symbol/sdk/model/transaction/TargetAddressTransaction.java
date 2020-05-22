package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;

/**
 * Transactions with targeted to an address.
 */
public interface TargetAddressTransaction {

    /**
     * Returns the target address.
     *
     * @return {@link Address }
     */
    UnresolvedAddress getTargetAddress();
}
