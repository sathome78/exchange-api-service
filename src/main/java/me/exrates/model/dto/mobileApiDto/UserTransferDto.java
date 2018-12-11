package me.exrates.model.dto.mobileApiDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * Created by OLEG on 09.02.2017.
 */
public class UserTransferDto {
    @NotNull(message = "Missing wallet ID")
    private Integer walletId;
    @NotNull(message = "Missing nickname")
    @Pattern(regexp = "^\\D+[\\w\\d\\-_]+")
    private String nickname;
    @NotNull(message = "Missing amount")
    private BigDecimal amount;

    public Integer getWalletId() {
        return walletId;
    }

    public void setWalletId(Integer walletId) {
        this.walletId = walletId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
