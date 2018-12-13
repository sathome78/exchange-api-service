package me.exrates.dao;

import me.exrates.model.dto.CallBackLogDto;

public interface CallBackLogDao {

    void logCallBackData(CallBackLogDto callBackLogDto);
}
