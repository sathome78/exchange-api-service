package me.exrates.openapi.model.dto.newsDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import me.exrates.openapi.model.enums.NewsTypeEnum;
import me.exrates.openapi.model.newsEntity.News;
import me.exrates.openapi.model.newsEntity.NewsVariant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ValkSam
 */
@Getter
@Setter
@PropertySource(value = {"classpath:/news.properties"})
public class NewsSyncDataDto {
  @Value(value = "${news.tagSeparator}")
  @JsonIgnore
  private String tagSeparator;
  @JsonIgnore
  private String resources;

  private Integer id;
  private NewsTypeEnum newsType;
  private String date;
  private String titleImgName;
  private String ytVideoUrl;
  private List<NewsVariantSyncData> variantList;

  public NewsSyncDataDto(News news) {
    this.id = news.getId();
    this.newsType = NewsTypeEnum.convert(news.getNewsType().getId());
    this.date = news.getDate().toString();
    this.resources = news.getResource();
    if (newsType == NewsTypeEnum.VIDEO) {
      this.ytVideoUrl = news.getResource();
    }
    this.variantList = new ArrayList<>();
    for (NewsVariant newsVariant : news.getNewsVariant()) {
      if (newsVariant.getSyncToWalletDate() == null
          || newsVariant.getSyncToWalletDate().isBefore(newsVariant.getUpdatedDate())) {
        variantList.add(new NewsVariantSyncData(newsVariant));
      }
    }
  }

  @Getter
  @Setter
  public class NewsVariantSyncData {
    private String language;
    private Boolean active;
    private Integer newsVariantId;
    private String date;
    private String updatedDate;
    private String lastSyncDate;
    private String title;
    private String brief;
    private String content;
    private List<String> tagList;
    private Integer showsCount;

    public NewsVariantSyncData(NewsVariant newsVariant) {
      this.language = newsVariant.getLanguage();
      this.newsVariantId = newsVariant.getId();
      this.date = newsVariant.getAddedDate() == null ? null : newsVariant.getAddedDate().toString();
      this.updatedDate = newsVariant.getUpdatedDate() == null ? null : newsVariant.getUpdatedDate().toString();
      this.lastSyncDate = newsVariant.getSyncToWalletDate() == null ? null : newsVariant.getSyncToWalletDate().toString();
      this.active = newsVariant.getActive();
      if (this.active) {
        this.title = newsVariant.getTitle();
        this.brief = newsVariant.getBrief();
        this.content = newsVariant.getContent();
        if (StringUtils.isEmpty(newsVariant.getTags())) {
          this.tagList = new ArrayList<>();
        } else {
          String tagString = newsVariant.getTags();
          tagString = tagString.replaceAll("\\s\\s", " ");
          if (StringUtils.isEmpty(tagSeparator)) {
            tagSeparator = "\\s";
          }
          this.tagList = Arrays.asList(tagString.split(tagSeparator));
        }
        this.showsCount = newsVariant.getVisitCount();
      }
    }
  }
}


