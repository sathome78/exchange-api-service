package me.exrates.openapi.model.dto;

import lombok.Builder;
import lombok.Data;
import me.exrates.openapi.model.Commission;
import me.exrates.openapi.model.Wallet;

import java.math.BigDecimal;

/**
 * Created by maks on 22.06.2017.
 */
@Builder(toBuilder = true)
@Data
public class TransferDto {

    private Wallet walletUserFrom;
    private Wallet walletUserTo;
    private String userToNickName;
    private int currencyId;
    private int userFromId;
    private int userToId;
    private Commission commission;
    private String notyAmount;
    private BigDecimal initialAmount;
    private BigDecimal comissionAmount;
}