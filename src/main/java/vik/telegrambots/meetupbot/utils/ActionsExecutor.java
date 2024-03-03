package vik.telegrambots.meetupbot.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@RequiredArgsConstructor
public class ActionsExecutor {

    private final MessageSender sender;

    @SneakyThrows
    public User getBotInfo() {
        return sender.execute(new GetMe());
    }

    public void sendMessage(Long chatId, String messageText) {
        sendMessage(chatId, messageText, KeyboardFactory.emptyKeyboard());
    }

    public void sendMessage(List<Long> chatIds, String messageText) {
        chatIds.forEach(chatId -> sendMessage(chatId, messageText));
    }

    public void sendMessage(List<Long> chatIds, String messageText, ReplyKeyboard replyKeyboard) {
        chatIds.forEach(chatId -> sendMessage(chatId, messageText, replyKeyboard));
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String messageText, ReplyKeyboard replyKeyboard) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(replyKeyboard)
                .build();
        sender.execute(message);
    }

    public void sendMessage(Long chatId, Integer replyToMessageId, String messageText) {
        sendMessage(chatId, replyToMessageId, messageText, KeyboardFactory.emptyKeyboard());
    }

    @SneakyThrows
    public void sendMessage(Long chatId, Integer replyToMessageId, String messageText, ReplyKeyboard replyKeyboard) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyMarkup(replyKeyboard)
                .replyToMessageId(replyToMessageId)
                .build();
        sender.execute(message);
    }

    @SneakyThrows
    public void updateMessageText(Update upd, String text) {
        var message = (Message) upd.getCallbackQuery().getMessage();
        sender.execute(EditMessageText.builder()
                .chatId(getChatId(upd))
                .messageId(message.getMessageId())
                .text(text)
                .replyMarkup(null)
                .build());
    }

    @SneakyThrows
    public void updateMessageText(Update upd, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        var message = (Message) upd.getCallbackQuery().getMessage();
        sender.execute(EditMessageText.builder()
                .chatId(getChatId(upd))
                .messageId(message.getMessageId())
                .text(text)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }

    @SneakyThrows
    public void updateMessageText(Update upd, InlineKeyboardMarkup inlineKeyboardMarkup) {
        var message = (Message) upd.getCallbackQuery().getMessage();
        sender.execute(EditMessageText.builder()
                .chatId(getChatId(upd))
                .messageId(message.getMessageId())
                .text(message.getText())
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }
}
