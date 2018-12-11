package me.exrates.model;

import lombok.Getter;
import lombok.ToString;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.PriceGrowthDirection;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static me.exrates.model.enums.ActionType.ADD;
import static me.exrates.model.enums.ActionType.SUBTRACT;
import static me.exrates.model.enums.PriceGrowthDirection.DOWN;
import static me.exrates.model.enums.PriceGrowthDirection.UP;

@Getter
@ToString
public class BotTradingCalculator {
    private final boolean isUserOrderPriceConsidered;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final BigDecimal maxPrice;
    private final BigDecimal minPrice;
    private final BigDecimal maxUserPrice;
    private final BigDecimal minUserPrice;
    private final BigDecimal lowerPriceBound;
    private final BigDecimal upperPriceBound;
    private final BigDecimal priceStep;
    private final BigDecimal minDeviationPercent;
    private final BigDecimal maxDeviationPercent;
    private final boolean isPriceStepRandom;
    private final BigDecimal priceStepDeviationPercent;
    private PriceGrowthDirection direction;

    public BotTradingCalculator(BotTradingSettings botTradingSettings) {
        isUserOrderPriceConsidered = botTradingSettings.getBotLaunchSettings().isUserOrderPriceConsidered();
        minAmount = botTradingSettings.getMinAmount();
        maxAmount = botTradingSettings.getMaxAmount();
        priceStep = botTradingSettings.getPriceStep();
        direction = botTradingSettings.getDirection();
        maxPrice = botTradingSettings.getMaxPrice();
        minPrice = botTradingSettings.getMinPrice();
        maxUserPrice = botTradingSettings.getMaxUserPrice();
        minUserPrice = botTradingSettings.getMinUserPrice();
        this.minDeviationPercent = new BigDecimal(botTradingSettings.getMinDeviationPercent());
        this.maxDeviationPercent = new BigDecimal(botTradingSettings.getMaxDeviationPercent());
        this.isPriceStepRandom = botTradingSettings.isPriceStepRandom();
        this.priceStepDeviationPercent = new BigDecimal(botTradingSettings.getPriceStepDeviationPercent());

        if (isUserOrderPriceConsidered) {
            if (minUserPrice == null || !checkPriceWithinRange(minUserPrice, minPrice, maxPrice)) {
                lowerPriceBound = minPrice;
            } else {
                lowerPriceBound = minUserPrice;
            }

            BigDecimal lowerWithStep = lowerPriceBound.add(priceStep);

            if (maxUserPrice == null || !checkPriceWithinRange(maxUserPrice, minPrice, maxPrice)) {
                upperPriceBound = maxPrice;
            } else if (maxUserPrice.compareTo(lowerWithStep) < 0 && checkPriceWithinRange(lowerWithStep, minPrice, maxPrice)) {
                upperPriceBound = lowerPriceBound.add(priceStep);
            } else {
                upperPriceBound = maxUserPrice;
            }
        } else {
            lowerPriceBound = minPrice;
            upperPriceBound = maxPrice;
        }
    }

    private boolean checkPriceWithinRange(BigDecimal price, BigDecimal min, BigDecimal max) {
        return price.compareTo(min) >= 0 && price.compareTo(max) <= 0;
    }

    public BigDecimal getRandomizedAmount() {
        return getRandomNumberForRange(minAmount, maxAmount, 5);
    }

    private BigDecimal getRandomNumberForRange(BigDecimal min, BigDecimal max, int scale) {
        return new BigDecimal(new RandomDataGenerator().nextUniform(min.doubleValue(), max.doubleValue())).setScale(scale, RoundingMode.DOWN);
    }


    public BigDecimal nextPrice(final BigDecimal previousPrice) {
        if (previousPrice.equals(lowerPriceBound)) {
            direction = UP;
        } else if (previousPrice.equals(upperPriceBound)) {
            direction = DOWN;
        }
        BigDecimal result = calculateNextPrice(previousPrice);
        if (result.compareTo(lowerPriceBound) < 0) {
            result = BigDecimalProcessing.doAction(lowerPriceBound, calculatePriceDeviationByPercent(priceStep, minDeviationPercent), ActionType.ADD);
            direction = UP;
        } else if(result.compareTo(upperPriceBound) > 0) {
            result = BigDecimalProcessing.doAction(upperPriceBound, calculatePriceDeviationByPercent(priceStep, maxDeviationPercent), ActionType.SUBTRACT);;
            direction = DOWN;
        }
        return result;
    }

    private BigDecimal calculateNextPrice(BigDecimal previousPrice) {
        ActionType actionType = direction == UP ? ADD : SUBTRACT;
        BigDecimal actualPriceStep;
        if (isPriceStepRandom) {
            actualPriceStep = BigDecimalProcessing.doAction(priceStep, calculatePriceDeviationByPercent(priceStep, priceStepDeviationPercent), ActionType.SUBTRACT);
        } else {
            actualPriceStep = priceStep;
        }


        return BigDecimalProcessing.doAction(previousPrice, actualPriceStep, actionType);
    }

    private BigDecimal calculatePriceDeviationByPercent(BigDecimal deviationBase, BigDecimal deviationPercent) {
        BigDecimal maxDeviation = BigDecimalProcessing.doAction(deviationBase, deviationPercent, ActionType.MULTIPLY_PERCENT);
        if (BigDecimal.ZERO.equals(maxDeviation)) {
            return BigDecimal.ZERO;
        }
        return getRandomNumberForRange(BigDecimal.ZERO, maxDeviation, 5);
    }

    public void setDirection(PriceGrowthDirection direction) {
        this.direction = direction;
    }
}
