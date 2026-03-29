package vik.telegrambots.meetupbot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

public class KeyboardFactory {

    public static InlineKeyboardMarkup getInlineGrid(List<ButtonInfo> values, int maxValuesInRow) {
        var buttons = new ArrayList<InlineKeyboardRow>();
        final var row = new InlineKeyboardRow();
        final var counter = new AtomicInteger();
        values.forEach(buttonInfo -> {
            var button = new InlineKeyboardButton(buttonInfo.buttonText());
            button.setCallbackData(buttonInfo.callbackData());
            button.setIconCustomEmojiId(buttonInfo.emoji());
            button.setStyle(buttonInfo.color().getTelegramStyle());
            row.add(button);
            counter.incrementAndGet();
            if (counter.get() >= maxValuesInRow) {
                buttons.add(new InlineKeyboardRow(row));
                row.clear();
                counter.set(0);
            }
        });
        if (!row.isEmpty()) {
            buttons.add(row);
        }
        return new InlineKeyboardMarkup(buttons);
    }

    public static ReplyKeyboard emptyKeyboard() {
        return new ReplyKeyboardRemove(true);
    }
}
