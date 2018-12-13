package me.exrates.model.enums;

public enum PriceGrowthDirection {
    UP, DOWN;

    public static PriceGrowthDirection getOpposite(PriceGrowthDirection direction) {
        switch (direction) {
            case UP: return DOWN;
            case DOWN: return UP;
            default: return direction;
        }
    }
}
