package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {

    private int id;

    @JsonProperty("file_name")
    private String fileName;

    private byte[] content;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
