package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Created by OLEG on 24.03.2017.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BtcTransactionHistoryDto {
    private String txId;
    private String address;
    private String category;
    private String amount;
    private String blockhash;
    private String fee;
    private Integer confirmations;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime time;

    public BtcTransactionHistoryDto(String txId, String address, String category, String amount, Integer confirmations, LocalDateTime time) {
        this.txId = txId;
        this.address = address;
        this.category = category;
        this.amount = amount;
        this.confirmations = confirmations;
        this.time = time;
    }

    public BtcTransactionHistoryDto(String txId) {
        this.txId = txId;
    }
}