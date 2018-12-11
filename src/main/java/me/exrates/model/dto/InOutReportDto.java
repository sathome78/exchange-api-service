package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InOutReportDto {

    private Integer orderNum;

    private int currencyId;
    private String currencyName;

    private BigDecimal rateToUSD;
    private int inputCount;
    private BigDecimal input;
    private BigDecimal inputCommission;
    private int outputCount;
    private BigDecimal output;
    private BigDecimal outputCommission;


    public static String getTitle() {
        return Stream.of("No.", "cur_id", "currency", "rateToUSD", "input", "output", "input_commission", "output_commission")
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.of(String.valueOf(orderNum), String.valueOf(currencyId), currencyName, BigDecimalProcessing.formatNoneComma(rateToUSD, false),
                BigDecimalProcessing.formatNoneComma(input, false),
                BigDecimalProcessing.formatNoneComma(output, false),
                BigDecimalProcessing.formatNoneComma(inputCommission, false),
                BigDecimalProcessing.formatNoneComma(outputCommission, false))
                .collect(Collectors.joining(";", "", "\r\n"));
    }
}
