package vik.telegrambots.meetupbot.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
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
public class ActionsExecutor {

    private final AbilityBot abilityBot;
    private final MessageSender sender;

    public ActionsExecutor(AbilityBot abilityBot) {
        this.abilityBot = abilityBot;
        this.sender = abilityBot.sender();
    }

    @SneakyThrows
    public User getBotInfo() {
        return sender.execute(new GetMe());
    }

    public Message sendMessage(Long chatId, String messageText) {
        return sendMessage(chatId, messageText, null, emptyKeyboard(), null, ParseMode.NULL);
    }

    public void sendMessage(Long chatId, String messageText, ParseMode parseMode) {
        sendMessage(chatId, messageText, null, emptyKeyboard(), null, parseMode);
    }

    public Message sendMessage(Long chatId, String messageText, ReplyKeyboard replyKeyboard) {
        return sendMessage(chatId, messageText, null, replyKeyboard, null, ParseMode.NULL);
    }

    public Message sendMessage(Long chatId, String messageText, ReplyKeyboard replyKeyboard, ParseMode parseMode) {
        return sendMessage(chatId, messageText, null, replyKeyboard, null, parseMode);
    }

    public void sendMessage(List<Long> chatIds, String messageText) {
        chatIds.forEach(chatId -> sendMessage(chatId, messageText));
    }

    public void sendMessage(List<Long> chatIds, String messageText, ReplyKeyboard replyKeyboard) {
        chatIds.forEach(chatId -> sendMessage(chatId, messageText, replyKeyboard));
    }

    public void updateMessageText(Update upd, String text) {
        var message = (Message) upd.getCallbackQuery().getMessage();
        updateMessageText(getChatId(upd), message.getMessageId(), text, null, null, ParseMode.NULL);
    }

    public void updateMessageText(Update upd, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        var message = (Message) upd.getCallbackQuery().getMessage();
        updateMessageText(getChatId(upd), message.getMessageId(), text, inlineKeyboardMarkup, null, ParseMode.NULL);
    }

    public void updateMessageText(Update upd, String text, InlineKeyboardMarkup inlineKeyboardMarkup, ParseMode parseMode) {
        var message = (Message) upd.getCallbackQuery().getMessage();
        updateMessageText(getChatId(upd), message.getMessageId(), text, inlineKeyboardMarkup, null, parseMode);
    }

    public void updateMessageText(Update upd, InlineKeyboardMarkup inlineKeyboardMarkup) {
        var message = (Message) upd.getCallbackQuery().getMessage();
        updateMessageText(getChatId(upd), message.getMessageId(), message.getText(), inlineKeyboardMarkup, null, ParseMode.NULL);
    }

    public void updateMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup inlineKeyboardMarkup, LinkPreviewOptions linkPreviewOptions, ParseMode parseMode) {
        try {
            sender.execute(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(text)
                    .replyMarkup(inlineKeyboardMarkup)
                    .linkPreviewOptions(linkPreviewOptions)
                    .parseMode(parseMode.getAsString())
                    .build());
        } catch (TelegramApiException e) {
            log.error("Can't update message text in chat {}", chatId, e);
        }
    }

    public Message sendMessage(Long chatId, String messageText, Integer replyToMessageId, ReplyKeyboard replyKeyboard, LinkPreviewOptions linkPreviewOptions, ParseMode parseMode) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .replyToMessageId(replyToMessageId)
                .replyMarkup(replyKeyboard)
                .linkPreviewOptions(linkPreviewOptions)
                .parseMode(parseMode.getAsString())
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

    public void sendAnimation(Long chatId, InputFile inputFile, String caption) {
        var sendAnimation = SendAnimation.builder()
                .animation(inputFile)
                .chatId(chatId)
                .caption(caption)
                .parseMode(ParseMode.HTML.getAsString())
                .build();
        try {
            abilityBot.execute(sendAnimation);
        } catch (TelegramApiException e) {
            log.error("Can't send video {} to chat {}", inputFile.getMediaName(), chatId, e);
        }
    }

    public void sendInlineQueryResult(AnswerInlineQuery inlineQueryResult) {
        try {
            sender.execute(inlineQueryResult);
        } catch (TelegramApiException e) {
            log.error("Can't send inlineQueryResult: {}", inlineQueryResult, e);
        }
    }

    public enum ParseMode {
        MARKDOWN,
        HTML,
        NULL;

        public String getAsString() {
            return this == ParseMode.NULL ? null : this.name().toLowerCase();
        }
    }
}
