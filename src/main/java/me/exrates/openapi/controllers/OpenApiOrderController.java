package me.exrates.openapi.controllers;

import me.exrates.openapi.exceptions.ValidationException;
import me.exrates.openapi.models.dto.OrderCreationResultDto;
import me.exrates.openapi.models.dto.openAPI.OrderCreationResultOpenApiDto;
import me.exrates.openapi.models.dto.openAPI.OrderParamsDto;
import me.exrates.openapi.services.OrderService;
import me.exrates.openapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

import static me.exrates.openapi.converters.CurrencyPairConverter.convert;
import static me.exrates.openapi.utils.RestApiUtils.retrieveParamFormBody;

@RestController
@RequestMapping("/orders")
public class OpenApiOrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OpenApiOrderController(OrderService orderService,
                                  UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * @api {post} /openapi/v1/orders/create Create order
     * @apiName Creates order
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Creates Order
     * @apiParam {String} currency_pair Name of currency pair (e.g. btc_usd)
     * @apiParam {String} order_type Type of order (BUY or SELL)
     * @apiParam {Number} amount Amount in base currency
     * @apiParam {Number} price Exchange rate
     * @apiParamExample Request Example:
     * /openapi/v1/orders/create
     * RequestBody:{currency_pair, order_type, amount, price}
     * @apiSuccess {Object} orderCreationResult Order creation result information
     * @apiSuccess {Integer} orderCreationResult.created_order_id Id of created order (not shown in case of partial accept)
     * @apiSuccess {Integer} orderCreationResult.auto_accepted_quantity Number of orders accepted automatically (not shown if no orders were auto-accepted)
     * @apiSuccess {Number} orderCreationResult.partially_accepted_amount Amount that was accepted partially (shown only in case of partial accept)
     */
//    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<OrderCreationResultOpenApiDto> createOrder(@RequestBody @Valid OrderParamsDto orderParamsDto,
//                                                                     Errors result) {
//        if (result.hasErrors()) {
//            throw new ValidationException(result.getAllErrors());
//        }
//        String currencyPairName = convert(orderParamsDto.getCurrencyPair());
//        String userEmail = userService.getUserEmailFromSecurityContext();
//        OrderCreationResultDto resultDto = orderService.prepareAndCreateOrderRest(currencyPairName, orderParamsDto.getOrderType().getOperationType(),
//                orderParamsDto.getAmount(), orderParamsDto.getPrice(), userEmail);
//        return new ResponseEntity<>(new OrderCreationResultOpenApiDto(resultDto), HttpStatus.CREATED);
//    }

    /**
     * @api {post} /openapi/v1/orders/cancel Cancel order by order id
     * @apiName Cancel order by order id
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Cancel order by order id
     * @apiParam {String} order_id Id of order to be cancelled
     * @apiParamExample Request Example:
     * /openapi/v1/orders/cancel
     * RequestBody: Map{order_id=123}
     * @apiSuccess {Map} success Cancellation result
     */
    @PostMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, Boolean>> cancelOrder(@RequestBody Map<String, String> params) {
        final Integer orderId = Integer.parseInt(retrieveParamFormBody(params, "order_id", true));

        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    /**
     * @api {post} /openapi/v1/orders/cancel/{currency_pair}/all Cancel open orders by currency pair
     * @apiName Cancel open orders by currency pair
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Cancel open orders by currency pair
     * @apiParamExample Request Example:
     * /openapi/v1/orders/cancel/btc_usd/all
     * @apiSuccess {Map} success Cancellation result
     */
//    @PostMapping(value = "/cancel/{currency_pair}/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<BaseResponse<Map<String, Boolean>>> cancelOrdersByCurrencyPair(@PathVariable("currency_pair") String currencyPair) {
//        final String transformedCurrencyPair = convert(currencyPair);
//
//        orderService.cancelOpenOrdersByCurrencyPair(transformedCurrencyPair);
//        return ResponseEntity.ok(BaseResponse.success(Collections.singletonMap("success", true)));
//    }

    /**
     * @api {post} /openapi/v1/orders/cancel/all Cancel all open orders
     * @apiName Cancel all open orders
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Cancel all open orders
     * @apiParamExample Request Example:
     * /openapi/v1/orders/cancel/all
     * @apiSuccess {Map} success Cancellation result
     */
    @PostMapping(value = "/cancel/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, Boolean>> cancelAllOrders() {
        orderService.cancelAllOpenOrders();
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    /**
     * @api {get} /openapi/v1/orders/accept Accept order
     * @apiName Accept order
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Accepts order
     * @apiParam {Integer} order_id Id of order to be accepted
     * @apiParamExample Request Example:
     * /openapi/v1/orders/accept
     * RequestBody: Map{order_id=123}
     * @apiSuccess {Map} success=true Acceptance result
     */
    @RequestMapping(value = "/accept", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Boolean> acceptOrder(@RequestBody Map<String, String> params) {
        String orderIdString = retrieveParamFormBody(params, "order_id", true);
        Integer orderId = Integer.parseInt(orderIdString);
        String userEmail = userService.getUserEmailFromSecurityContext();
        orderService.acceptOrder(userEmail, orderId);
        return Collections.singletonMap("success", true);
    }

    /**
     * @api {get} /openapi/v1/orders/open/{order_type}?currency_pair Open orders
     * @apiName Open orders
     * @apiGroup Order API
     * @apiUse APIHeaders
     * @apiPermission NonPublicAuth
     * @apiDescription Buy or sell open orders ordered by price (SELL ascending, BUY descending)
     * @apiParam {String} order_type Type of order (BUY or SELL)
     * @apiParam {String} currency_pair Name of currency pair
     * @apiParamExample Request Example:
     * /openapi/v1/orders/open/SELL?btc_usd
     * @apiSuccess {Array} openOrder Open Order Result
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id Order id
     * @apiSuccess {String} data.order_type type of order (BUY or SELL)
     * @apiSuccess {Number} data.amount Amount in base currency
     * @apiSuccess {Number} data.price Exchange rate
     */
//    @GetMapping(value = "/open/{order_type}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public List<OpenOrderDto> openOrders(@PathVariable("order_type") OrderType orderType,
//                                         @RequestParam("currency_pair") String currencyPair) {
//        String currencyPairName = convert(currencyPair);
//        return orderService.getOpenOrders(currencyPairName, orderType);
//    }
}
