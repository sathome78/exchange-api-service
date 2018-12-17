package me.exrates.openapi.model.vo;

import lombok.Data;
import me.exrates.openapi.model.enums.IntervalType;
import me.exrates.openapi.exception.model.UnsupportedIntervalFormatException;
import me.exrates.openapi.exception.model.UnsupportedIntervalTypeException;

@Data
public class BackDealInterval {

    private Integer intervalValue;
    private IntervalType intervalType;

    public BackDealInterval(Integer intervalValue, IntervalType intervalType) {
        this.intervalValue = intervalValue;
        this.intervalType = intervalType;
    }

    public BackDealInterval(){}
    public String getInterval() {
        return intervalValue + " " + intervalType;
    }

    public BackDealInterval(String intervalString) {
        try {
            this.intervalValue = Integer.valueOf(intervalString.split(" ")[0]);
            this.intervalType = IntervalType.convert(intervalString.split(" ")[1]);
        } catch (UnsupportedIntervalTypeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnsupportedIntervalFormatException(intervalString);
        }
    }
}
