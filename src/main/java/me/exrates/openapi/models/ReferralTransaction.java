package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exrates.openapi.models.enums.ReferralTransactionStatus;

@Data
@ToString(exclude = {"order", "referralLevel", "transaction"})
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class ReferralTransaction {

    private int id;
    private int userId;
    private int initiatorId;
    private ExOrder order;
    private ReferralLevel referralLevel;
    private Transaction transaction;
    private String initiatorEmail;
    private ReferralTransactionStatus status;
}
