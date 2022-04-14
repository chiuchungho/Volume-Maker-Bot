package org.titanic.enums;

/**
 * @author Hanno Skowronek
 */
public enum Instrument {
    MAMIUSDT(33);

    private int value;

    Instrument(int x) {
        this.value = x;
    }

    public static Instrument fromInteger(int x) {
        return switch (x) {
            case 33 -> MAMIUSDT;
            default -> null;
        };
    }
    public int toInteger() {
        return this.value;
    }
}
