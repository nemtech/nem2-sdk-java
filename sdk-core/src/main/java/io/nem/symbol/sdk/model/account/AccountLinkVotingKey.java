package io.nem.symbol.sdk.model.account;

import java.math.BigInteger;

/**
 * Account link voting key
 */
public class AccountLinkVotingKey {

    /**
     * Public Key.
     */
    private final String publicKey;

    /**
     * Start point.
     */
    private final BigInteger startPoint;

    /**
     * End point.
     */
    private final BigInteger endPoint;

    public AccountLinkVotingKey(String publicKey, BigInteger startPoint, BigInteger endPoint) {
        this.publicKey = publicKey;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public BigInteger getStartPoint() {
        return startPoint;
    }

    public BigInteger getEndPoint() {
        return endPoint;
    }
}
