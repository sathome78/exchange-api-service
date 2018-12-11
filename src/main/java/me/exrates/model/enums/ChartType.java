package me.exrates.model.enums;

import me.exrates.model.exceptions.UnsupportedChartTypeException;

public enum ChartType {

    AREA("AREA"),
    CANDLE("CANDLE"),
    STOCK("STOCK");

    private final String chartType;

    ChartType(String chartType) {
        this.chartType = chartType;
    }

    public static ChartType convert(String typeName) {
        switch (typeName) {
            case "AREA":
                return AREA;
            case "CANDLE":
                return CANDLE;
            case "STOCK":
                return STOCK;
            default:
                throw new UnsupportedChartTypeException(typeName);
        }
    }

    public String getTypeName() {
        return chartType;
    }
}
