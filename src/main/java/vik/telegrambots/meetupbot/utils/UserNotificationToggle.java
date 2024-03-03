package vik.telegrambots.meetupbot.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserNotificationToggle {

    TOGGLE_NEW_EVENTS_NOTIFICATION("New event notifications", true),
    TOGGLE_1_DAY_NOTIFICATION("One day before event", false),
    TOGGLE_12_HOURS_NOTIFICATION("12 hours before event", false),
    TOGGLE_6_HOURS_NOTIFICATION("6 hours before event", false),
    TOGGLE_1_HOUR_NOTIFICATION("1 hour before event", false),
    TOGGLE_NEW_EVENTS_NOTIFICATION_FROM_SETTINGS("New event notifications", true),
    TOGGLE_1_DAY_NOTIFICATION_FROM_SETTINGS("One day before event", true),
    TOGGLE_12_HOURS_NOTIFICATION_FROM_SETTINGS("12 hours before event", true),
    TOGGLE_6_HOURS_NOTIFICATION_FROM_SETTINGS("6 hours before event", true),
    TOGGLE_1_HOUR_NOTIFICATION_FROM_SETTINGS("1 hour before event", true);

    private final String buttonText;
    private final boolean crossMarkEmoji;
}
