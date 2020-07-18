package io.nem.symbol.sdk.api;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.account.Address;
import java.util.Objects;

/**
 * Criteria used to search blocks
 */
public class BlockSearchCriteria extends SearchCriteria<BlockSearchCriteria> {

    /**
     * Search block by signer.
     */
    private PublicKey signerPublicKey;

    /**
     * Search block by beneficiary.
     */
    private Address beneficiaryAddress;

    /**
     * The atrribute used to sort the
     */
    private BlockOrderBy orderBy;

    public PublicKey getSignerPublicKey() {
        return signerPublicKey;
    }

    public void setSignerPublicKey(PublicKey signerPublicKey) {
        this.signerPublicKey = signerPublicKey;
    }

    public Address getBeneficiaryAddress() {
        return beneficiaryAddress;
    }

    public void setBeneficiaryAddress(Address beneficiaryAddress) {
        this.beneficiaryAddress = beneficiaryAddress;
    }

    public BlockOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(BlockOrderBy orderBy) {
        this.orderBy = orderBy;
    }


    /**
     * Sets the signerPublicKey builder style.
     *
     * @param signerPublicKey the signerPublicKey.
     * @return this object.
     */
    public BlockSearchCriteria signerPublicKey(PublicKey signerPublicKey) {
        this.signerPublicKey = signerPublicKey;
        return this;
    }

    /**
     * Sets the beneficiaryAddress builder style.
     *
     * @param beneficiaryAddress the beneficiaryAddress.
     * @return this object.
     */
    public BlockSearchCriteria beneficiaryAddress(Address beneficiaryAddress) {
        this.beneficiaryAddress = beneficiaryAddress;
        return this;
    }

    /**
     * Sets the orderBy builder style.
     *
     * @param orderBy the orderBy.
     * @return this object.
     */
    public BlockSearchCriteria orderBy(BlockOrderBy orderBy) {
        this.orderBy = orderBy;
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
        BlockSearchCriteria criteria = (BlockSearchCriteria) o;
        return Objects.equals(signerPublicKey, criteria.signerPublicKey) && Objects
            .equals(beneficiaryAddress, criteria.beneficiaryAddress) && orderBy == criteria.orderBy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), signerPublicKey, beneficiaryAddress, orderBy);
    }
}
