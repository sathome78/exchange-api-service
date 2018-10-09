package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class PublicTokenDto {

    private Long id;
    private String alias;
    private Integer userId;
    private String publicKey;
    private Boolean allowTrade;
    private Boolean allowWithdraw;
    private LocalDateTime generationDate;
}
