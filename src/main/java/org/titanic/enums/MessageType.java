package org.titanic.enums;

/**
 * @author Hanno Skowronek
 */
public enum MessageType {
    REQUEST(0),
    REPLY(1),
    SUBSCRIBE(2),
    EVENT(3),
    UNSUBSCRIBE(4),
    ERROR(5);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
