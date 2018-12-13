package me.exrates.model.converter;

import com.google.gson.Gson;
import me.exrates.model.CurrencyPair;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by Valk on 13.04.16.
 */

public class CurrencyPairConverter implements Converter<String, CurrencyPair> {
    @Override
    public CurrencyPair convert(String s) {
        s = s.replaceAll("=[a-zA-Z]+\\{", "={");
        s = s.replaceAll("^[a-zA-Z]+\\{", "{");
        return new Gson().fromJson(s, CurrencyPair.class);
    }
}
