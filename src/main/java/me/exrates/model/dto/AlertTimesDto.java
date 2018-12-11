package me.exrates.model.dto;

import lombok.Data;

/**
 * Created by Maks on 21.12.2017.
 */
@Data
public class AlertTimesDto {

    private String start;
    private String end;
    private String now;

    public AlertTimesDto(String start, String end, String now) {
        this.start = start;
        this.end = end;
        this.now = now;
    }
}
