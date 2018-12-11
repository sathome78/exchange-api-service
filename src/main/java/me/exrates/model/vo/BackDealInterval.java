package me.exrates.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.exceptions.UnsupportedIntervalFormatException;
import me.exrates.model.exceptions.UnsupportedIntervalTypeException;

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
