package me.exrates.openapi.model.vo;

import lombok.AllArgsConstructor;
import me.exrates.openapi.model.enums.OrderStatus;
import me.exrates.openapi.model.enums.UserRole;

@AllArgsConstructor
public class OrderRoleInfoForDelete {
    private OrderStatus status;
    private UserRole creatorRole;
    private UserRole acceptorRole;
    private int transactionsCount;


    public boolean mayDeleteWithoutProcessingTransactions() {
        return status == OrderStatus.CLOSED && creatorRole == UserRole.BOT_TRADER && acceptorRole == UserRole.BOT_TRADER && transactionsCount == 0;
    }
}
