package vik.telegrambots.meetupbot.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;
import static vik.telegrambots.meetupbot.utils.KeyboardFactory.emptyKeyboard;

@Slf4j
@RequiredArgsConstructor
public class ActionsExecutor {

    private final MessageSender sender;

    @SneakyThrows
    public User getBotInfo() {
        return sender.execute(new GetMe());
    }

    public void sendMessage(Long chatId, String messageText) {
        sendMessage(chatId, messageText, emptyKeyboard());
    }

    public void sendMessage(List<Long> chatIds, String messageText) {
        chatIds.forEach(chatId -> sendMessage(chatId, messageText));
    }

    public void sendMessage(List<Long> chatIds, String messageText, ReplyKeyboard replyKeyboard) {
        chatIds.forEach(chatId -> sendMessage(chatId, messageText, replyKeyboard));
    }

    public Message sendMessage(Long chatId, String messageText, ReplyKeyboard replyKeyboard, LinkPreviewOptions linkPreviewOptions) {
        return sendMessage(chatId, messageText, null, replyKeyboard, linkPreviewOptions);
    }

    public Message sendMessage(Long chatId, String messageText, ReplyKeyboard replyKeyboard) {
        return sendMessage(chatId, messageText, replyKeyboard, null);
    }

    public void sendMessage(Long chatId, Integer replyToMessageId, String messageText) {
        sendMessage(chatId, replyToMessageId, messageText, emptyKeyboard());
    }

    public void sendMessage(Long chatId, Integer replyToMessageId, String messageText, ReplyKeyboard replyKeyboard) {
        sendMessage(chatId, messageText, replyToMessageId, replyKeyboard, null);
    }

    @SneakyThrows
    public void updateMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup inlineKeyboardMarkup, LinkPreviewOptions linkPreviewOptions) {
        sender.execute(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .replyMarkup(inlineKeyboardMarkup)
                .linkPreviewOptions(linkPreviewOptions)
                .build());
    }

    public void updateMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        updateMessageText(chatId, messageId, text, inlineKeyboardMarkup, null);
    }

    public void updateMessageText(Long chatId, Integer messageId, String text) {
        updateMessageText(chatId, messageId, text, null);
    }

    public void updateMessageText(Update upd, String text) {
        var message = (Message) upd.getCallbackQuery().getMessage();
        updateMessageText(getChatId(upd), message.getMessageId(), text);
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

    public Message sendMessage(Long chatId, String messageText, Integer replyToMessageId, ReplyKeyboard replyKeyboard, LinkPreviewOptions linkPreviewOptions) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyToMessageId(replyToMessageId)
                .replyMarkup(replyKeyboard)
                .linkPreviewOptions(linkPreviewOptions)
                .build();
        try {
            return sender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Can't send message to chat {}, text: {}", chatId, messageText, e);
            return null;
        }
    }

    public void sendDocument(Long chatId, InputFile inputFile) {
        var sendDocument = SendDocument.builder()
                .document(inputFile)
                .chatId(chatId)
                .replyMarkup(emptyKeyboard())
                .build();
        try {
            sender.sendDocument(sendDocument);
        } catch (TelegramApiException e) {
            log.error("Can't send file {} to chat {}", inputFile.getMediaName(), chatId, e);
        }
    }
}
