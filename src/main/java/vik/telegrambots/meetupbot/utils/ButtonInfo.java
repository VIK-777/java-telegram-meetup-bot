package vik.telegrambots.meetupbot.utils;

public record ButtonInfo(
    String buttonText,
    String callbackData,
    String emoji,
    ButtonColor color
) {

  public ButtonInfo(String buttonText, String callbackData) {
    this(buttonText, callbackData, null, ButtonColor.EMPTY);
  }

  public ButtonInfo(String buttonText, String callbackData, ButtonColor color) {
    this(buttonText, callbackData, null, color);
  }

  public ButtonInfo(String buttonText, String callbackData, String emoji) {
    this(buttonText, callbackData, emoji, ButtonColor.EMPTY);
  }

  public ButtonInfo(String buttonText, String callbackData, CustomEmoji emoji) {
    this(buttonText, callbackData, emoji == null ? null : emoji.id(), ButtonColor.EMPTY);
  }
}
