package me.exrates.dao.impl;

import me.exrates.dao.CallBackLogDao;
import me.exrates.model.dto.CallBackLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CallBackLogDaoImpl implements CallBackLogDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void logCallBackData(CallBackLogDto callBackLogDto) {
        String sql = "INSERT INTO CALLBACK_LOGS VALUES(:user_id, :request_date, :response_date, :request_json, :response_json, :response_code)";
        final Map<String, Object> params = new HashMap<>();
        params.put("user_id", callBackLogDto.getUserId());
        params.put("request_date", callBackLogDto.getRequestDate());
        params.put("response_date", callBackLogDto.getResponseDate());
        params.put("request_json", callBackLogDto.getRequestJson());
        params.put("response_json", callBackLogDto.getResponseJson());
        params.put("response_code", callBackLogDto.getResponseCode());
        namedParameterJdbcTemplate.update(sql, params);

    }
}
