package vik.telegrambots.meetupbot.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ButtonColor {
  BLUE("primary"),
  RED("danger"),
  GREEN("success"),
  EMPTY(null);

  @Getter
  private final String telegramStyle;
}
