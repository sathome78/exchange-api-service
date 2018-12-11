package me.exrates.model.vo;

import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.enums.IntervalType2;
import me.exrates.model.exceptions.UnsupportedIntervalFormatException;
import me.exrates.model.exceptions.UnsupportedIntervalTypeException;

/**
 * Created by Valk on 27.04.2016.
 * stores the interval from current DateTime
 * based on string like "5 DAY"
 */
public class BackDealInterval2 {
    public Integer intervalValue;
    public IntervalType2 intervalType;

    public BackDealInterval2(Integer intervalValue, IntervalType2 intervalType) {
        this.intervalValue = intervalValue;
        this.intervalType = intervalType;
    }

    /*constructor*/
    public BackDealInterval2(String intervalString) {
        try {
            this.intervalValue = Integer.valueOf(intervalString.split(" ")[0]);
            this.intervalType = IntervalType2.valueOf(intervalString.split(" ")[1]);
        } catch (UnsupportedIntervalTypeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedIntervalFormatException(intervalString);
        }
    }

    public BackDealInterval2(ChartTimeFrame timeFrame) {
        this.intervalValue = timeFrame.getTimeValue();
        this.intervalType = timeFrame.getTimeUnit();
    }
    /**/
    public String getInterval(){
        return intervalValue+" "+intervalType;
    }
    /*getters setters*/

    @Override
    public String toString() {
        return "BackDealInterval{" +
                "intervalValue=" + intervalValue +
                ", intervalType=" + intervalType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BackDealInterval2 that = (BackDealInterval2) o;

        if (intervalValue != null ? !intervalValue.equals(that.intervalValue) : that.intervalValue != null)
            return false;
        return intervalType == that.intervalType;

    }

    @Override
    public int hashCode() {
        int result = intervalValue != null ? intervalValue.hashCode() : 0;
        result = 31 * result + (intervalType != null ? intervalType.hashCode() : 0);
        return result;
    }
}
