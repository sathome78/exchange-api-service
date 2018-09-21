package me.exrates.openapi.service.merchantStrategy;

import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.util.CharUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IMerchantService {

  default void checkWithdrawAddressName(String withdrawName) {
    if (CharUtils.isCyrillic(withdrawName)) {
      throw new InvalidAccountException();
    }
  }

  default String generateFullUrl(String url, Properties properties) {
    return url.concat("?").concat(
        properties.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"))
    );
  }

  default Map<String, String> generateFullUrlMap(String url, String method, Properties properties) {
    Map<String, String> result = new HashMap<String, String>() {{
      put("$__redirectionUrl", url);
      put("$__method", method);
    }};
    properties.entrySet().forEach(e -> result.put(e.getKey().toString(), e.getValue().toString()));
    return result;
  }

  default Map<String, String> generateFullUrlMap(String url, String method, Properties properties, String sign) {
    Map<String, String> result = generateFullUrlMap(url, method, properties);
    result.put("$__sign", sign);
    return result;
  }

  default String getMainAddress() {
    return "no address!!!";
  }
  //TODO remove after changes in mobile api
  default String getPaymentMessage(String additionalTag, Locale locale) {
    return additionalTag;
  }
}
