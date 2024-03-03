package vik.telegrambots.meetupbot.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class Utils {

    public static final ZoneId berlinTimezone = TimeZone.getTimeZone("Europe/Berlin").toZoneId();

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(berlinTimezone);

    public static DateTimeFormatter dateTimeFormatterWithWeekDay = DateTimeFormatter.ofPattern("EEE, yyyy-MM-dd HH:mm").withZone(berlinTimezone);

    public static Instant parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, dateTimeFormatter).atZone(berlinTimezone).toInstant();
    }

    public static String writeDateTime(Instant dateTime) {
        return dateTimeFormatterWithWeekDay.format(dateTime);
    }
}
