package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.UnresolvedAddress;

/**
 * A transaction linked to a recipient.
 */
public interface RecipientTransaction {

    /**
     * Returns address of the recipient.
     *
     * @return recipient address
     */
    UnresolvedAddress getRecipient();
}
