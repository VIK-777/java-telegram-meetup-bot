package vik.telegrambots.meetupbot.utils;

public record ButtonInfo(
    String buttonText,
    String callbackData,
    String url,
    String emoji,
    ButtonColor color
) {

  public ButtonInfo(String buttonText, String callbackData) {
    this(buttonText, callbackData, null, null, ButtonColor.EMPTY);
  }

  public ButtonInfo(String buttonText, String callbackData, ButtonColor color) {
    this(buttonText, callbackData, null, null, color);
  }

  public ButtonInfo(String buttonText, String callbackData, String emoji) {
    this(buttonText, callbackData, null, emoji, ButtonColor.EMPTY);
  }

  public ButtonInfo(String buttonText, String callbackData, CustomEmoji emoji) {
    this(buttonText, callbackData, null, emoji == null ? null : emoji.id(), ButtonColor.EMPTY);
  }

  public ButtonInfo(String buttonText, String url, boolean isUrl) {
    this(buttonText, null, url, null, ButtonColor.EMPTY);
  }
}
