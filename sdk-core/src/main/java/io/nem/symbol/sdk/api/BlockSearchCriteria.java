package io.nem.symbol.sdk.api;

import io.nem.symbol.core.crypto.PublicKey;

/**
 * Criteria used to search blocks
 */
public class BlockSearchCriteria extends SearchCriteria{

    /**
     * Search block by signer.
     */
    private PublicKey signerPublicKey;

    /**
     * Search block by beneficiary.
     */
    private PublicKey beneficiaryPublicKey;

    /**
     * Entry id at which to start pagination. If the ordering parameter is set to DESC, the elements returned precede
     * the identifier. Otherwise, newer elements with respect to the id are returned.  (optional)
     */
    private String offset;

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

    public PublicKey getBeneficiaryPublicKey() {
        return beneficiaryPublicKey;
    }

    public void setBeneficiaryPublicKey(PublicKey beneficiaryPublicKey) {
        this.beneficiaryPublicKey = beneficiaryPublicKey;
    }

    public BlockOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(BlockOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
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
     * Sets the beneficiaryPublicKey builder style.
     *
     * @param beneficiaryPublicKey the beneficiaryPublicKey.
     * @return this object.
     */
    public BlockSearchCriteria beneficiaryPublicKey(PublicKey beneficiaryPublicKey) {
        this.beneficiaryPublicKey = beneficiaryPublicKey;
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

    /**
     * Sets the offset builder style.
     *
     * @param offset the offset.
     * @return this object.
     */
    public BlockSearchCriteria offset(String offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Sets the page size builder style.
     *
     * @param pageSize the page size.
     * @return this object.
     */
    public BlockSearchCriteria pageSize(Integer pageSize) {
        return (BlockSearchCriteria) super.pageSize(pageSize);
    }

    /**
     * Sets the order builder style.
     *
     * @param order the order.
     * @return this object.
     */
    @Override
    public BlockSearchCriteria order(OrderBy order) {
        return (BlockSearchCriteria) super.order(order);
    }

    /**
     * Sets the page number builder style.
     *
     * @param pageNumber the page number.
     * @return this objects.
     */
    @Override
    public BlockSearchCriteria pageNumber(Integer pageNumber) {
        return (BlockSearchCriteria) super.pageNumber(pageNumber);
    }

}
