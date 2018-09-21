package me.exrates.openapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
public class UserSummaryTotalInOutDto {
  private String currency;
  private BigDecimal totalIn;
  private BigDecimal totalOut;

  public static String getTitle() {
    return "Currency" + ";" +
        "In" + ";" +
        "Out" +
        "\r\n";
  }

  @Override
  public String toString() {
    return currency + ";" +
        BigDecimalProcessing.formatNoneComma(totalIn, false) + ";" +
        BigDecimalProcessing.formatNoneComma(totalOut, false) +
        "\r\n";
  }

}
