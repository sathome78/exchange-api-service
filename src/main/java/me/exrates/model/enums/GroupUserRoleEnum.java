package me.exrates.model.enums;

import lombok.NoArgsConstructor;
import me.exrates.model.exceptions.UnsupportedGroupUserRoleNameException;

import java.util.Arrays;

/**
 * Created by ValkSam
 */
@NoArgsConstructor
public enum GroupUserRoleEnum {
  ADMINS,
  USERS,
  BOT;

  public static GroupUserRoleEnum convert(String name) {
    return Arrays.stream(GroupUserRoleEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedGroupUserRoleNameException(name));
  }
}
