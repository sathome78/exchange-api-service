package me.exrates.dao;

import me.exrates.model.dto.onlineTableDto.NewsDto;

import java.util.List;
import java.util.Locale;

/**
 * Created by Valk on 27.05.2016.
 */

public interface NewsDao {
    List<NewsDto> getNewsBriefList(Integer offset, Integer limit, Locale locale);

}
