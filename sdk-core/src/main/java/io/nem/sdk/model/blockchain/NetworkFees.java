

package io.nem.sdk.model.blockchain;

/**
 * Information about the average, median, highest and lower fee multiplier over the last
 * "numBlocksTransactionFeeStats".
 */
public class NetworkFees {

    /**
     * Average fee multiplier over the last "numBlocksTransactionFeeStats".
     */
    private final Double averageFeeMultiplier;

    /**
     * Median fee multiplier over the last "numBlocksTransactionFeeStats".
     **/
    private final Double medianFeeMultiplier;


    /**
     * Lowest fee multiplier over the last "numBlocksTransactionFeeStats".
     **/
    private final Integer lowestFeeMultiplier;

    /**
     * Highest fee multiplier over the last "numBlocksTransactionFeeStats".
     **/
    private final Integer highestFeeMultiplier;

    public NetworkFees(Double averageFeeMultiplier, Double medianFeeMultiplier,
        Integer lowestFeeMultiplier, Integer highestFeeMultiplier) {
        this.averageFeeMultiplier = averageFeeMultiplier;
        this.medianFeeMultiplier = medianFeeMultiplier;
        this.lowestFeeMultiplier = lowestFeeMultiplier;
        this.highestFeeMultiplier = highestFeeMultiplier;
    }

    public Double getAverageFeeMultiplier() {
        return averageFeeMultiplier;
    }

    public Double getMedianFeeMultiplier() {
        return medianFeeMultiplier;
    }

    public Integer getLowestFeeMultiplier() {
        return lowestFeeMultiplier;
    }

    public Integer getHighestFeeMultiplier() {
        return highestFeeMultiplier;
    }
}

