package vik.telegrambots.meetupbot.utils;

import org.telegram.abilitybots.api.util.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyboardFactory {

    public static InlineKeyboardMarkup getInlineGridForPairs(List<Pair<String, String>> values, int maxValuesInRow) {
        var buttons = new ArrayList<List<InlineKeyboardButton>>();
        final var row = new ArrayList<InlineKeyboardButton>();
        final var counter = new AtomicInteger();
        values.forEach(pair -> {
            var button = new InlineKeyboardButton(pair.a());
            button.setCallbackData(pair.b());
            row.add(button);
            counter.incrementAndGet();
            if (counter.get() >= maxValuesInRow) {
                buttons.add(new ArrayList<>(row));
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
