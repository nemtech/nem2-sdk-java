package io.nem.symbol.sdk.api;

/**
 * Possible columns accounts can be sorted.
 */
public enum AccountOrderBy {
    ID("id"),

    BALANCE("balance");

    private final String value;

    AccountOrderBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
