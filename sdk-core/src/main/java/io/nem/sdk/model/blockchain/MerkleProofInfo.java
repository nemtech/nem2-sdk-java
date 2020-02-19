package io.nem.sdk.model.blockchain;

import java.util.List;

public class MerkleProofInfo {

    private final List<MerklePathItem> merklePath;

    /**
     * Constructor
     *
     * @param merklePath the path.
     */
    public MerkleProofInfo(List<MerklePathItem> merklePath) {
        this.merklePath = merklePath;
    }


    /**
     * Return Payload
     *
     * @return {@link List} of MerklePathItem
     */
    public List<MerklePathItem> getMerklePath() {
        return this.merklePath;
    }
}
