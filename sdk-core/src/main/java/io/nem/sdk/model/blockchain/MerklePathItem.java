package io.nem.sdk.model.blockchain;

public class MerklePathItem {

    private final Integer position;
    private final String hash;

    /**
     * Constructor
     *
     * @param position the position in the path.
     * @param hash the hash.
     */
    public MerklePathItem(Integer position, String hash) {
        this.position = position;
        this.hash = hash;
    }

    /**
     * Return position
     *
     * @return Integer
     */
    public Integer getPosition() {
        return this.position;
    }

    /**
     * Return hash
     *
     * @return String
     */
    public String getHash() {
        return this.hash;
    }
}
