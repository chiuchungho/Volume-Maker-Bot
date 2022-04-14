package org.titanic.enums;

/**
 * @author Hanno Skowronek
 */
public enum Side {
    Buy,
    Sell;

    public static Side fromInteger(int x) {
        return switch (x) {
            case 0 -> Buy;
            case 1 -> Sell;
            default -> null;
        };

    }
}
