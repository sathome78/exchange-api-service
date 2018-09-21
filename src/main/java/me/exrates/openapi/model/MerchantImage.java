package me.exrates.openapi.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode
public class MerchantImage {
    private int Id;
    private int merchantId;
    private int currencyId;
    private String image_name;
    private String image_path;

    public MerchantImage(int id) {
        Id = id;
    }
}