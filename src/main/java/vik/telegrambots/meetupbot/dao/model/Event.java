package vik.telegrambots.meetupbot.dao.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import static vik.telegrambots.meetupbot.utils.Constants.DESCRIPTION_STARTS_WITH;
import static vik.telegrambots.meetupbot.utils.Constants.EVENT_TEMPLATE;
import static vik.telegrambots.meetupbot.utils.Constants.LINK_STARTS_WITH;
import static vik.telegrambots.meetupbot.utils.Constants.NAME_STARTS_WITH;
import static vik.telegrambots.meetupbot.utils.Constants.TIME_STARTS_WITH;
import static vik.telegrambots.meetupbot.utils.Utils.parseDateTime;
import static vik.telegrambots.meetupbot.utils.Utils.writeDateTime;

@Slf4j
@Data
@Builder
@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long eventId;
    Long fromUser;
    Instant eventTime;
    String name;
    String description;
    String link;
    @Embedded
    TechnicalFields technicalFields;

    public static Event fromMessageText(String eventAsString) {
        var event = new Event();
        eventAsString.lines().forEach(line -> {
            if (line.startsWith(NAME_STARTS_WITH)) {
                event.setName(line.substring(NAME_STARTS_WITH.length()));
            }
            if (line.startsWith(TIME_STARTS_WITH)) {
                var dateTimeAsString = line.substring(TIME_STARTS_WITH.length());
                Instant dateTime = null;
                try {
                    dateTime = parseDateTime(dateTimeAsString);
                } catch (DateTimeParseException exception) {
                    log.error("Can't parse string: {}", dateTimeAsString);
                }
                event.setEventTime(dateTime);
            }
            if (line.startsWith(DESCRIPTION_STARTS_WITH)) {
                event.setDescription(line.substring(DESCRIPTION_STARTS_WITH.length()));
            }
            if (line.startsWith(LINK_STARTS_WITH)) {
                event.setLink(line.substring(LINK_STARTS_WITH.length()));
            }
        });
        return event;
    }

    public String toMessageText() {
        return EVENT_TEMPLATE.formatted(name, writeDateTime(eventTime), description, link);
    }
}
