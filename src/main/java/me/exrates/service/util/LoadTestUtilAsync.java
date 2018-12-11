package me.exrates.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.dto.mobileApiDto.OrderCreationParamsDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import me.exrates.model.enums.OperationType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LoadTestUtilAsync {

    private static final String BASE_URL = "http://dev.exrates.tech";
    private static final String URL_DASHBOARD = BASE_URL + "/dashboard";
    private static final String URL_AUTHENTICATE = BASE_URL + "/rest/user/authenticate";
    private static final String URL_SUBMIT_ORDER = BASE_URL + "/api/orders/submitOrderForCreation";
    private static final String URL_CONFIRM_ORDER = BASE_URL + "/api/orders/createOrder";
    private static final String URL_ACCEPT_ORDERS = BASE_URL + "/api/orders/acceptOrders";
    private static AsyncRestTemplate restTemplate;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final UserAuthenticationDto AUTHENTICATION;
    private static final OrderCreationParamsDto ORDER_CREATION_PARAMS_DTO;
    private static final int POOL_SIZE = 5000;

    static {
        restTemplate = new AsyncRestTemplate(requestFactory());

        AUTHENTICATION = new UserAuthenticationDto();
        AUTHENTICATION.setEmail("golvazin@gmail.com");
        AUTHENTICATION.setPassword("EFMDCF5aA1oVWQ==");
        AUTHENTICATION.setAppKey("111");
        ORDER_CREATION_PARAMS_DTO = new OrderCreationParamsDto();
        ORDER_CREATION_PARAMS_DTO.setAmount(new BigDecimal(0.001));
        ORDER_CREATION_PARAMS_DTO.setCurrencyPairId(1);
        ORDER_CREATION_PARAMS_DTO.setOrderType(OperationType.BUY);
        ORDER_CREATION_PARAMS_DTO.setRate(new BigDecimal(9700));

    }

    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(POOL_SIZE);
        pool.scheduleAtFixedRate(LoadTestUtilAsync::runOrderCreationSequence, 2L, 100L, TimeUnit.MILLISECONDS);
    }




    private static void runOrderCreationSequence() {
     //   System.out.println("Active threads " + Thread.activeCount());
        sendGetRequest(URL_DASHBOARD, Collections.emptyMap()).addCallback(result -> {
            System.out.println("Got dashboard");
            retrieveToken()
                    .addCallback((tokenResult) -> {
                        try {
                            String token = objectMapper.readTree(tokenResult.getBody()).get("token").asText();
                            System.out.println("TOKEN: " + token);
                            submitOrder(token, submitResult -> {
                                try {
                                    String orderKey = objectMapper.readTree(submitResult).get("key").asText();
                                    System.out.println("ORDER KEY: " + orderKey);

                                    createOrder(token, orderKey, createResult -> {
                                        try {
                                            Integer orderId = objectMapper.readTree(createResult).get("createdOrderId").asInt();
                                            System.out.println("ORDER ID: " + orderId);

                                            acceptOrder(token, orderId, acceptResult -> {
                                                System.out.println("ACCEPTED: " + orderId);
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, Throwable::printStackTrace);
        }, Throwable::printStackTrace);





    }






    private static ListenableFuture<ResponseEntity<String>> retrieveToken() {
        return sendPostRequest(URL_AUTHENTICATE, AUTHENTICATION, MediaType.APPLICATION_JSON_UTF8_VALUE);

    }

    private static ListenableFuture<ResponseEntity<String>> submitOrder(String token, Consumer<String> successCallback) {
        return sendPostWithToken(URL_SUBMIT_ORDER, ORDER_CREATION_PARAMS_DTO, token, MediaType.APPLICATION_JSON_UTF8_VALUE, successCallback);

    }

    private static ListenableFuture<ResponseEntity<String>> createOrder(String token, String key, Consumer<String> successCallback) {
        return sendPostWithToken(URL_CONFIRM_ORDER, new OrderKeyDto(key), token, MediaType.APPLICATION_JSON_UTF8_VALUE, successCallback);

    }

    private static ListenableFuture<ResponseEntity<String>> acceptOrder(String token, Integer orderId, Consumer<String> successCallback) {
        return sendPostWithToken(URL_ACCEPT_ORDERS, new OrdersAcceptionDto(Collections.singletonList(orderId)),
                token, MediaType.APPLICATION_JSON_UTF8_VALUE, successCallback);
    }



    private static ListenableFuture<ResponseEntity<String>> sendGetRequest(String url, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        params.forEach(builder::queryParam);
        return restTemplate.getForEntity(builder.toUriString(), String.class);
    }

    private static ListenableFuture<ResponseEntity<String>> sendPostRequest(String url, Object bodyObject, String mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", mediaType);
        HttpEntity<?> httpEntity = new HttpEntity<>(bodyObject, headers);
        return restTemplate.postForEntity(url, httpEntity, String.class);
    }

    private static ListenableFuture<ResponseEntity<String>> sendPostWithToken(String url, Object bodyObject, String token, String mediaType,
                                                                              Consumer<String> successCallback) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Exrates-Rest-Token", token);
        headers.set("Accept", mediaType);
        HttpEntity<?> httpEntity = new HttpEntity<>(bodyObject, headers);
        ListenableFuture<ResponseEntity<String>> future = restTemplate.postForEntity(url, httpEntity, String.class);
        future.addCallback(new PostWithTokenCallback(newToken -> sendPostWithToken(url, bodyObject, newToken, mediaType, successCallback), successCallback));
        return future;

    }


    private static class OrderKeyDto {
        private String orderKey;

        public OrderKeyDto(String orderKey) {
            this.orderKey = orderKey;
        }

    }

    private static class OrdersAcceptionDto {
        private List<Integer> orderIdsList;

        public OrdersAcceptionDto(List<Integer> orderIdsList) {
            this.orderIdsList = orderIdsList;
        }

    }



    private static class PostWithTokenCallback implements ListenableFutureCallback<ResponseEntity<String>> {

        private Consumer<String> tokenCallback;
        private Consumer<String> successCallback;

        public PostWithTokenCallback(Consumer<String> tokenCallback, Consumer<String> successCallback) {
            this.tokenCallback = tokenCallback;
            this.successCallback = successCallback;
        }

        @Override
        public void onFailure(Throwable ex) {
            if (ex instanceof HttpClientErrorException) {
                HttpClientErrorException clientErrorException = (HttpClientErrorException) ex;
                String response = clientErrorException.getResponseBodyAsString();
                try {
                    String errorCode = objectMapper.readTree(response).get("errorCode").asText();
                    if("EXPIRED_AUTHENTICATION_TOKEN".equals(errorCode)) {
                        retrieveToken().addCallback(result -> {
                            try {
                                String newToken = objectMapper.readTree(result.getBody()).get("token").asText();
                                System.out.println("TOKEN: " + newToken);
                                tokenCallback.accept(newToken);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }, Throwable::printStackTrace);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSuccess(ResponseEntity<String> result) {
            String body = result.getBody();
            successCallback.accept(body);
        }
    }

    private static HttpComponentsAsyncClientHttpRequestFactory requestFactory() {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        HttpComponentsAsyncClientHttpRequestFactory asyncRequestFactory = new HttpComponentsAsyncClientHttpRequestFactory();
        asyncRequestFactory.setHttpAsyncClient(httpclient);
        return asyncRequestFactory;
    }





}
