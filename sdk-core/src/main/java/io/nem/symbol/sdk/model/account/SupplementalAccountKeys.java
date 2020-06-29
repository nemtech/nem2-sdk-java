package io.nem.symbol.sdk.model.account;

import java.util.List;
import java.util.Optional;

/**
 * Supplemental Public Keys
 */
public class SupplementalAccountKeys {

    /**
     * Linked public key if any
     */
    private final Optional<String> linked;

    /**
     * Node public key if any
     */
    private final Optional<String> node;

    /**
     * VRF public key if any
     */
    private final Optional<String> vrf;

    /**
     * Veys public keys if any
     */
    private final List<AccountLinkVotingKey> voting;

    public SupplementalAccountKeys(Optional<String> linked, Optional<String> node, Optional<String> vrf,
        List<AccountLinkVotingKey> voting) {
        this.linked = linked;
        this.node = node;
        this.vrf = vrf;
        this.voting = voting;
    }

    public Optional<String> getLinked() {
        return linked;
    }

    public Optional<String> getNode() {
        return node;
    }

    public Optional<String> getVrf() {
        return vrf;
    }

    public List<AccountLinkVotingKey> getVoting() {
        return voting;
    }
}
