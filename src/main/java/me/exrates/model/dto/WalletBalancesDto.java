package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class WalletBalancesDto {

    private String currencyName;
    private ExternalWalletBalancesDto external;
    private List<InternalWalletBalancesDto> internals;
}
