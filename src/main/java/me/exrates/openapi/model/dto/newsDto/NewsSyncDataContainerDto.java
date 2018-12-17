package me.exrates.openapi.model.dto.newsDto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ValkSam on 07.11.2016.
 */
@Getter
@Setter
public class NewsSyncDataContainerDto {
  private List<NewsSyncDataDto> updatedNews = new ArrayList<>();
}


