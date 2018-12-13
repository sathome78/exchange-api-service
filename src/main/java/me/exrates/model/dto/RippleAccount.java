package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * Created by maks on 10.05.2017.
 */
@Builder(toBuilder = true)
@Data
public class RippleAccount {

    private String name;
    private String secret;

    @Tolerate
    public RippleAccount() {
    }
}
