package io.nem.sdk.model.blockchain;

import java.util.List;

public class MerkelProofInfo {

    private final List<MerkelPathItem> merklePath;

    /**
     * Constructor
     */
    public MerkelProofInfo(List<MerkelPathItem> merklePath) {
        this.merklePath = merklePath;
    }


    /**
     * Return Payload
     *
     * @return {@link List} of MerkelPathItem
     */
    public List<MerkelPathItem> getMerklePath() {
        return this.merklePath;
    }
}
