package me.exrates.model.dto;

import lombok.Data;

/**
 * Created by Maks on 27.02.2018.
 */

@Data
public class MosaicIdDto {

    private String namespaceId;
    private String name;

    public MosaicIdDto(String namespaceId, String name) {
        this.namespaceId = namespaceId;
        this.name = name;
    }
}
