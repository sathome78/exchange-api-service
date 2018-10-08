package me.exrates.openapi.controllers;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.aspect.AccessCheck;
import me.exrates.openapi.aspect.RateLimitCheck;
import me.exrates.openapi.exceptions.ValidationException;
import me.exrates.openapi.models.dto.OrderCreationResultDto;
import me.exrates.openapi.models.dto.openAPI.OpenOrderDto;
import me.exrates.openapi.models.dto.openAPI.OrderCreationResultOpenApiDto;
import me.exrates.openapi.models.dto.openAPI.OrderParametersDto;
import me.exrates.openapi.models.enums.OrderType;
import me.exrates.openapi.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static java.util.Objects.nonNull;
import static me.exrates.openapi.utils.ValidationUtil.validateCurrencyPair;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @AccessCheck
    @RateLimitCheck
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderCreationResultOpenApiDto> createOrder(@Valid @RequestBody OrderParametersDto orderParametersDto,
                                                                     Errors result) {
        if (result.hasErrors()) {
            throw new ValidationException(result.getAllErrors());
        }

        final String pair = orderParametersDto.getPair();

        validateCurrencyPair(pair);

        OrderCreationResultDto resultDto = orderService.prepareAndCreateOrder(
                pair,
                orderParametersDto.getOrderType().getOperationType(),
                orderParametersDto.getAmount(),
                orderParametersDto.getPrice());

        return ResponseEntity.ok(new OrderCreationResultOpenApiDto(resultDto));
    }

    @AccessCheck
    @RateLimitCheck
    @PostMapping(value = "/cancel/{order_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> cancelOrder(@PathVariable(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @AccessCheck
    @RateLimitCheck
    @PostMapping(value = "/cancel/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> cancelOrdersByCurrencyPair(@RequestParam(value = "currency_pair", required = false) String pair) {

        if (nonNull(pair)) {
            validateCurrencyPair(pair);

            return ResponseEntity.ok(orderService.cancelOpenOrdersByCurrencyPair(pair));
        }
        return ResponseEntity.ok(orderService.cancelAllOpenOrders());
    }

    @AccessCheck
    @RateLimitCheck
    @RequestMapping(value = "/accept/{order_id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> acceptOrder(@PathVariable(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(orderService.acceptOrder(orderId));
    }

    @AccessCheck
    @RateLimitCheck
    @GetMapping(value = "/opened", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OpenOrderDto>> openOrders(@RequestParam(value = "currency_pair") String pair,
                                                         @RequestParam("order_type") OrderType orderType) {
        validateCurrencyPair(pair);

        return ResponseEntity.ok(orderService.getOpenOrders(pair, orderType));
    }
}
