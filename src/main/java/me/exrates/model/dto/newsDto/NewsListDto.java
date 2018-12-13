package me.exrates.model.dto.newsDto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.model.newsEntity.NewsVariant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ValkSam
 */
@Getter
@Setter
@PropertySource(value = {"classpath:/news.properties"})
public class NewsListDto extends NewsDto {
  @Value(value = "${news.tagSeparator}")
  private String tagSeparator;

  public NewsListDto(NewsVariant news) {
    this.id = news.getNews().getId();
    this.resource = news.getNews().getResource();
    this.newsType = NewsTypeEnum.convert(news.getNews().getNewsType().getId());
    this.date = news.getNews().getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    this.title = news.getTitle();
    this.brief = news.getBrief();
    this.language = news.getLanguage();
    this.newsVariantId = news.getId();
    this.showsCount = news.getVisitCount();
    this.calendarDate = news.getNews().getCalendarDate() == null ? null : news.getNews().getCalendarDate().toString();
    this.noTitleImg = news.getNews().getNoTitleImg();
    if (StringUtils.isEmpty(news.getTags())) {
      this.tagList = new ArrayList<>();
    } else {
      String tagString = news.getTags();
      tagString = tagString.replaceAll("\\s\\s", " ");
      if (StringUtils.isEmpty(tagSeparator)) {
        tagSeparator = "\\s";
      }
      this.tagList = Arrays.asList(tagString.split(tagSeparator));
    }
  }
}
