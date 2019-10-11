package io.nem.sdk.model.message;

import java.util.Arrays;

public enum MessageType {

    PLAIN_MESSAGE(0x00),

    ENCRYPTED_MESSAGE(0x01),

    PERSISTENT_HARVESTING_DELEGATION_MESSAGE(0xFE);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    /**
     * Static constructor converting metadata type raw value to enum instance.
     *
     * @return {@link MessageType}
     */
    public static MessageType rawValueOf(int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

    /**
     * Returns enum value.
     *
     * @return enum value
     */
    public int getValue() {
        return this.value;
    }

}
