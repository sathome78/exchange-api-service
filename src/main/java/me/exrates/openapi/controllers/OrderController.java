package me.exrates.openapi.controllers;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.util.Objects.nonNull;
import static me.exrates.openapi.converters.CurrencyPairConverter.convert;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * @api {post} /openapi/v1/orders/create Create order
     * @apiName Creates order
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Creates Order
     * @apiParam {String} currency_1 Name of base currency (e.g. btc)
     * @apiParam {String} currency_2 Name of convert currency (e.g. usd)
     * @apiParam {String} order_type Type of order (BUY or SELL)
     * @apiParam {Number} amount Amount in base currency
     * @apiParam {Number} price Exchange rate
     * @apiParamExample Request Example:
     * /openapi/v1/orders/create
     * RequestBody:{currency_1, currency_2, order_type, amount, price}
     * @apiSuccess {Object}     orderCreationResult                             Order creation result information
     * @apiSuccess {Integer}    orderCreationResult.created_order_id            Id of created order (not shown in case of partial accept)
     * @apiSuccess {Integer}    orderCreationResult.auto_accepted_quantity      Number of orders accepted automatically (not shown if no orders were auto-accepted)
     * @apiSuccess {Number}     orderCreationResult.partially_accepted_amount   Amount that was accepted partially (shown only in case of partial accept)
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderCreationResultOpenApiDto> createOrder(@Valid @RequestBody OrderParametersDto orderParametersDto,
                                                                     Errors result) {
        if (result.hasErrors()) {
            throw new ValidationException(result.getAllErrors());
        }
        String pairName = convert(orderParametersDto.getCurrency1(), orderParametersDto.getCurrency2());

        OrderCreationResultDto resultDto = orderService.prepareAndCreateOrder(
                pairName,
                orderParametersDto.getOrderType().getOperationType(),
                orderParametersDto.getAmount(),
                orderParametersDto.getPrice());

        return ResponseEntity.ok(new OrderCreationResultOpenApiDto(resultDto));
    }

    /**
     * @api {post} /openapi/v1/orders/cancel?order_id Cancel order by order id
     * @apiName Cancel order by order id
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Cancel order by order id
     * @apiParam {Integer} order_id Id of order to be cancelled
     * @apiParamExample Request Example:
     * /openapi/v1/orders/cancel?order_id=1
     * @apiSuccess {Boolean} success=true Cancellation result
     */
    @PostMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> cancelOrder(@RequestParam(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    /**
     * @api {post} /openapi/v1/orders/cancel/all[?currency_1&currency_2] Cancel all open orders by currency pair (if currency_1 and currency_2 present) otherwise cancel all open orders
     * @apiName Cancel all open orders
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Cancel all open orders
     * @apiParamExample Request Example:
     * /openapi/v1/orders/cancel/all?currency_1=btc&currency_2=usd or /openapi/v1/orders/cancel/all
     * @apiSuccess {Boolean} success=true Cancellation result
     */
    @PostMapping(value = "/cancel/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> cancelOrdersByCurrencyPair(@RequestParam(value = "currency_1", required = false) String currency1,
                                                              @RequestParam(value = "currency_2", required = false) String currency2) {
        String pairName = convert(currency1, currency2);
        return ResponseEntity.ok(nonNull(currency1) && nonNull(currency2)
                ? orderService.cancelOpenOrdersByCurrencyPair(pairName)
                : orderService.cancelAllOpenOrders());
    }

    /**
     * @api {get} /openapi/v1/orders/accept?order_id Accept order by order id
     * @apiName Accept order by order id
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Accept order by order id
     * @apiParam {Integer} order_id Id of order to be accepted
     * @apiParamExample Request Example:
     * /openapi/v1/orders/accept?order_id=1
     * @apiSuccess {Boolean} success=true Acceptance result
     */
    @RequestMapping(value = "/accept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> acceptOrder(@RequestParam(value = "order_id") Integer orderId) {
        return ResponseEntity.ok(orderService.acceptOrder(orderId));
    }

    /**
     * @api {get} /openapi/v1/orders/open?currency_1&currency_2&order_type Open orders
     * @apiName Open orders
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Buy or sell open orders ordered by price (SELL ascending, BUY descending)
     * @apiParam {String} currency_1 Name of base currency
     * @apiParam {String} currency_2 Name of convert currency
     * @apiParam {String} order_type Type of order (BUY or SELL)
     * @apiParamExample Request Example:
     * /openapi/v1/orders/open?currency_1=btc&currency_2=usd&order_type=SELL
     * @apiSuccess {Array}      openOrder           Open Order Result
     * @apiSuccess {Object}     data                Container object
     * @apiSuccess {Integer}    data.id             Order id
     * @apiSuccess {String}     data.order_type     Type of order (BUY or SELL)
     * @apiSuccess {Number}     data.amount         Amount in base currency
     * @apiSuccess {Number}     data.price          Exchange rate
     */
    @GetMapping(value = "/open", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<OpenOrderDto>> openOrders(@RequestParam("currency_1") String currency1,
                                                         @RequestParam("currency_2") String currency2,
                                                         @RequestParam("order_type") OrderType orderType) {
        String pairName = convert(currency1, currency2);

        return ResponseEntity.ok(orderService.getOpenOrders(pairName, orderType));
    }
}
