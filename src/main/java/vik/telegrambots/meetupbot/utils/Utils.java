package vik.telegrambots.meetupbot.utils;

import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.addTag;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class Utils {

    public static final ZoneId berlinTimezone = TimeZone.getTimeZone("Europe/Berlin").toZoneId();

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(berlinTimezone);

    public static final DateTimeFormatter dateTimeFormatterWithWeekDay = DateTimeFormatter.ofPattern("EEE, yyyy-MM-dd HH:mm").withZone(berlinTimezone);

    public static final DateTimeFormatter dateTimeFormatterWithWeekDayHumanReadable = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm").withZone(berlinTimezone);

    public static final DateTimeFormatter inlineQuerySearchStringFormatter = DateTimeFormatter.ofPattern("EEEE MMMM").withZone(berlinTimezone);

    public static String writeDateTimeForInlineQuerySearch(Instant dateTime) {
        return inlineQuerySearchStringFormatter.format(dateTime).toLowerCase();
    }

    public static Instant parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, dateTimeFormatter).atZone(berlinTimezone).toInstant();
    }

    public static String writeDateTime(Instant dateTime) {
        return dateTimeFormatterWithWeekDay.format(dateTime);
    }

    public static String writeDateTimeHumanReadable(Instant dateTime) {
        return dateTimeFormatterWithWeekDayHumanReadable.format(dateTime);
    }

    public static String addTagOrNull(String userName) {
        return userName != null && !userName.startsWith("@") ? addTag(userName) : userName;
    }
}
