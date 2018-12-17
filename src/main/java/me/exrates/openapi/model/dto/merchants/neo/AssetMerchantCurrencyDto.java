package me.exrates.openapi.model.dto.merchants.neo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.exrates.openapi.model.Currency;
import me.exrates.openapi.model.Merchant;

@Getter
@AllArgsConstructor
@ToString
public class AssetMerchantCurrencyDto {
    NeoAsset asset;
    Merchant merchant;
    Currency currency;
}
