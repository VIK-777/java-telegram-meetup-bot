package vik.telegrambots.meetupbot.utils;

import java.util.List;

public record MessageInfo(
    String messageText,
    List<ButtonInfo> buttons
) {

}
