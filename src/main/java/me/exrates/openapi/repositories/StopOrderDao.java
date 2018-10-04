package me.exrates.openapi.repositories;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.models.StopOrder;
import me.exrates.openapi.models.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Slf4j
@Repository
public class StopOrderDao {

    private static final String CREATE_STOP_ORDER_SQL = "INSERT INTO STOP_ORDERS" +
            "  (user_id, currency_pair_id, operation_type_id, stop_rate,  limit_rate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id)" +
            "  VALUES (:user_id, :currency_pair_id, :operation_type_id, :stop_rate, :limit_rate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount, :status_id)";

    private static final String UPDATE_STOP_ORDER_SQL = "UPDATE STOP_ORDERS so SET so.status_id=:status_id WHERE so.id = :id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public StopOrderDao(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //+
    public Integer create(StopOrder order) {
        log.debug(CREATE_STOP_ORDER_SQL);
        log.debug("{}", order);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = jdbcTemplate.update(
                CREATE_STOP_ORDER_SQL,
                new MapSqlParameterSource(
                        Map.of(
                                "user_id", order.getUserId(),
                                "currency_pair_id", order.getCurrencyPairId(),
                                "operation_type_id", order.getOperationType().getType(),
                                "stop_rate", order.getStop(),
                                "limit_rate", order.getLimit(),
                                "amount_base", order.getAmountBase(),
                                "amount_convert", order.getAmountConvert(),
                                "commission_id", order.getComissionId(),
                                "commission_fixed_amount", order.getCommissionFixedAmount(),
                                "status_id", OrderStatus.INPROCESS.getStatus())),
                keyHolder);

        return result <= 0 ? 0 : keyHolder.getKey().intValue();
    }

    //+
    public boolean setStatus(int orderId, OrderStatus status) {
        int result = jdbcTemplate.update(
                UPDATE_STOP_ORDER_SQL,
                Map.of(
                        "status_id", status.getStatus(),
                        "id", orderId
                ));
        return result > 0;
    }
}
