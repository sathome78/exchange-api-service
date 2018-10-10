package me.exrates.openapi.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.exrates.openapi.models.dto.CoinmarketApiDto;

import java.util.List;

@Getter
@NoArgsConstructor
public class CoinmarketData {

    private List<CoinmarketApiDto> list;

    public CoinmarketData(List<CoinmarketApiDto> list) {
        this.list = list;
    }
}
