package me.exrates.openapi.model.exceptions;

public class UnsupportedChartTypeException extends RuntimeException {

    public UnsupportedChartTypeException(String chartType) {
        super("No such chart type " + chartType);
    }
}