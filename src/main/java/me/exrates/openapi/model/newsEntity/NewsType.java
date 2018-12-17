package me.exrates.openapi.model.newsEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.openapi.model.enums.NewsTypeEnum;


/**
 * Created by ValkSam
 */
@Getter
@Setter
@NoArgsConstructor
public class NewsType {
  private Integer id;

  private String name;

  public NewsType(NewsTypeEnum newsTypeEnum) {
    this.id = newsTypeEnum.getCode();
    this.name = newsTypeEnum.name();
  }

}
