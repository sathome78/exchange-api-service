package me.exrates.openapi.dao;

import me.exrates.openapi.model.dto.CallBackLogDto;

public interface CallBackLogDao {

    void logCallBackData(CallBackLogDto callBackLogDto);
}
