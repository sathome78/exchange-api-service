package me.exrates.openapi.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.openapi.exceptions.model.UnsupportedIntervalFormatException;
import me.exrates.openapi.exceptions.model.UnsupportedIntervalTypeException;
import me.exrates.openapi.model.enums.IntervalType;

@Data
@NoArgsConstructor
public class BackDealInterval {

    private Integer intervalValue;
    private IntervalType intervalType;

    public BackDealInterval(Integer intervalValue, IntervalType intervalType) {
        this.intervalValue = intervalValue;
        this.intervalType = intervalType;
    }

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
