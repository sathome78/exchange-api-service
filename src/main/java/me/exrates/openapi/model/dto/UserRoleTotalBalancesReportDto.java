package me.exrates.openapi.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.RealCheckableRole;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter @Setter
public class UserRoleTotalBalancesReportDto<T extends Enum & RealCheckableRole> {

    private String currency;
    //wolper 19.04.2018
    //currency id added
    private int curId;
    private Map<String, BigDecimal> balances;
    private Class<T> enumClass;
    //wolper 24.04.18
    private BigDecimal rateToUSD;

    private String totalReal = BigDecimalProcessing.formatNoneComma(BigDecimal.ZERO, false);

    //wolper 19.04.18
    public UserRoleTotalBalancesReportDto(String currency, int id, Map<String, BigDecimal> balances, Class<T> enumClass) {
        this(currency, balances, enumClass);
        this.curId=id;
    }

    public UserRoleTotalBalancesReportDto(String currency, Map<String, BigDecimal> balances, Class<T> enumClass) {
        this.currency = currency;
        this.balances = balances;
        this.enumClass = enumClass;
        if (RealCheckableRole.class.isAssignableFrom(enumClass)) {
            BigDecimal totalRealValue = Arrays.stream(enumClass.getEnumConstants()).map(dto -> (RealCheckableRole) dto).filter(RealCheckableRole::isReal)
                    .map(dto -> balances.getOrDefault(dto.getName(), BigDecimal.ZERO)).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

            totalReal = BigDecimalProcessing.formatNoneComma(totalRealValue, false);
        }
    }

    public static <T extends Enum> String getTitle(Class<T> enumClass) {
        return Stream.concat(Stream.of("cur_id", "currency", "rateToUSD", "totalReal"), Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).sorted())
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.concat(Stream.of(String.valueOf(curId), currency,  BigDecimalProcessing.formatNoneComma(rateToUSD, false), totalReal), Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).sorted()
                .map(item -> BigDecimalProcessing.formatNoneComma(balances.getOrDefault(item, BigDecimal.ZERO), false)))
                .collect(Collectors.joining(";", "", "\r\n"));
    }


}
