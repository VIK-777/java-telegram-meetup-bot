package vik.telegrambots.meetupbot.utils;

public record CustomEmoji(
    String emoji,
    String id
) {

  public String toHtmlString() {
    return "<tg-emoji emoji-id=\"%s\">%s</tg-emoji>".formatted(id, emoji);
  }
}
