package me.exrates.openapi.service.merchantStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class MerchantServiceAbstract {

  String generateFullUrl(String url, Properties properties) {
    return url.concat("?").concat(
        properties.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"))
    );
  }

  Map<String, String> generateFullUrlMap(String url, String method, Properties properties) {
    Map<String, String> result = new HashMap<String, String>() {{
      put("$__redirectionUrl", url);
      put("$__method", method);
    }};
    properties.entrySet().forEach(e -> result.put(e.getKey().toString(), e.getValue().toString()));
    return result;
  }

  Map<String, String> generateFullUrlMap(String url, String method, Properties properties, String sign) {
    Map<String, String> result = generateFullUrlMap(url, method, properties);
    result.put("$__sign", sign);
    return result;
  }

  String getMainAddress() {
    return "qwqwqqqw";
  }
}
