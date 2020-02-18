package io.nem.sdk.model.blockchain;

public class MerklePathItem {

    private final Position position;
    private final String hash;

    /**
     * Constructor
     *
     * @param position the position in the path.
     * @param hash the hash.
     */
    public MerklePathItem(Position position, String hash) {
        this.position = position;
        this.hash = hash;
    }

    /**
     * Return position
     *
     * @return Integer
     */
    public Position getPosition() {
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
