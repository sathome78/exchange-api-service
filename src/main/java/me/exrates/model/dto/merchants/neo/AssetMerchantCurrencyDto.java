package me.exrates.model.dto.merchants.neo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;

@Getter
@AllArgsConstructor
@ToString
public class AssetMerchantCurrencyDto {
    NeoAsset asset;
    Merchant merchant;
    Currency currency;
}
