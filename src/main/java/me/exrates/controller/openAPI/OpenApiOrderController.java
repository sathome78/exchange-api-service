package me.exrates.controller.openAPI;

import com.google.common.base.Strings;
import me.exrates.controller.model.BaseResponse;
import me.exrates.model.dto.CallbackURL;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.dto.openAPI.OrderCreationResultOpenApiDto;
import me.exrates.model.dto.openAPI.OrderParamsDto;
import me.exrates.model.enums.OrderType;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.exception.*;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;
import me.exrates.service.exception.api.OpenApiError;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.userOperation.UserOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;
import static me.exrates.service.util.RestApiUtils.retrieveParamFormBody;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/openapi/v1/orders")
public class OpenApiOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserOperationService userOperationService;

    @Autowired
    private MessageSource messageSource;

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
    @PreAuthorize("hasAuthority('TRADE')")
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrderCreationResultOpenApiDto> createOrder(@RequestBody @Valid OrderParamsDto orderParamsDto) {
        String currencyPairName = transformCurrencyPair(orderParamsDto.getCurrencyPair());
        String userEmail = userService.getUserEmailFromSecurityContext();
        int userId = userService.getIdByEmail(userEmail);
        Locale locale = new Locale(userService.getPreferedLang(userId));
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userId, UserOperationAuthority.TRADING);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, locale));
        }
        OrderCreationResultDto resultDto = orderService.prepareAndCreateOrderRest(currencyPairName, orderParamsDto.getOrderType().getOperationType(),
                orderParamsDto.getAmount(), orderParamsDto.getPrice(), userEmail);
        return new ResponseEntity<>(new OrderCreationResultOpenApiDto(resultDto), HttpStatus.CREATED);
    }

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
    @PreAuthorize("hasAuthority('TRADE')")
    @PostMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<BaseResponse<Map<String, Boolean>>> cancelOrder(@RequestBody Map<String, String> params) {
        final Integer orderId = Integer.parseInt(retrieveParamFormBody(params, "order_id", true));

        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(BaseResponse.success(Collections.singletonMap("success", true)));
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
    @PreAuthorize("hasAuthority('TRADE')")
    @RequestMapping(value = "/accept", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Boolean> acceptOrder(@RequestBody Map<String, String> params) {
        String orderIdString = retrieveParamFormBody(params, "order_id", true);
        Integer orderId = Integer.parseInt(orderIdString);
        String userEmail = userService.getUserEmailFromSecurityContext();
        int userId = userService.getIdByEmail(userEmail);
        Locale locale = new Locale(userService.getPreferedLang(userId));
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userId, UserOperationAuthority.TRADING);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, locale));
        }
        orderService.acceptOrder(userEmail, orderId);
        return Collections.singletonMap("success", true);
    }


    @PreAuthorize("hasAuthority('TRADE')")
    @PostMapping(value = "/callback/add", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> addCallback(@RequestBody CallbackURL callbackUrl) throws CallBackUrlAlreadyExistException {
        Map<String, Object> responseBody = new HashMap<>();
        String userEmail = userService.getUserEmailFromSecurityContext();
        int userId = userService.getIdByEmail(userEmail);
        if (Strings.isNullOrEmpty(callbackUrl.getCallbackURL())) {
            responseBody.put("status", "false");
            responseBody.put("error", " Callback url is null or empty");
            return responseBody;
        }
        int affectedRowCount = userService.setCallbackURL(userId, callbackUrl);
        responseBody.put("status", affectedRowCount != 0);
        return responseBody;
    }

    @PreAuthorize("hasAuthority('TRADE')")
    @PutMapping(value = "/callback/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> updateallback(@RequestBody CallbackURL callbackUrl) {
        Map<String, Object> responseBody = new HashMap<>();
        String userEmail = userService.getUserEmailFromSecurityContext();
        int userId = userService.getIdByEmail(userEmail);
        if (Strings.isNullOrEmpty(callbackUrl.getCallbackURL()) && callbackUrl.getPairId() != null) {
            responseBody.put("status", "false");
            responseBody.put("error", " Callback url is null or empty");
            return responseBody;
        }
        int affectedRowCount = userService.updateCallbackURL(userId, callbackUrl);
        responseBody.put("status", affectedRowCount != 0);
        return responseBody;
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
    @GetMapping(value = "/open/{order_type}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<OpenOrderDto> openOrders(@PathVariable("order_type") OrderType orderType,
                                         @RequestParam("currency_pair") String currencyPair) {
        String currencyPairName = transformCurrencyPair(currencyPair);
        return orderService.getOpenOrders(currencyPairName, orderType);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = AccessDeniedException.class)
    public OpenApiError accessDeniedExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.ACCESS_DENIED, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, OrderParamsWrongException.class, MethodArgumentTypeMismatchException.class})
    @ResponseBody
    public OpenApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public OpenApiError jsonMappingExceptionHandler(HttpServletRequest req, HttpMessageNotReadableException exception) {
        return new OpenApiError(ErrorCode.REQUEST_NOT_READABLE, req.getRequestURL(), "Invalid request format");
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public OpenApiError missingServletRequestParameterHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(CallBackUrlAlreadyExistException.class)
    @ResponseBody
    public OpenApiError callBackExistException(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.CALL_BACK_URL_ALREADY_EXISTS, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public OpenApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception);
    }


    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidCurrencyPairFormatException.class)
    @ResponseBody
    public OpenApiError invalidCurrencyPairFormatExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_CURRENCY_PAIR_FORMAT, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(AlreadyAcceptedOrderException.class)
    @ResponseBody
    public OpenApiError alreadyAcceptedOrderExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.ALREADY_ACCEPTED_ORDER, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseBody
    public OpenApiError orderNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.ORDER_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(UserOperationAccessException.class)
    @ResponseBody
    public OpenApiError userOperationAccessExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.BLOCED_TRADING, req.getRequestURL(), exception);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public OpenApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), "An internal error occured");
    }
}
