package me.exrates.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class CallBackLogDto {
    private int userId;
    private int responseCode;
    private LocalDateTime requestDate;
    private LocalDateTime responseDate;
    private String requestJson;
    private String responseJson;

}
