package vik.telegrambots.meetupbot.utils;

public class Constants {

    public static final String BOT_INFO_MESSAGE = """
            This is a bot where you can receive notifications about events. \
            It could send messages about new events. \
            And also it could notify you about events that you have subscribed to.
            """;

    public static final String BOT_COMMANDS_MESSAGE = """
            Available commands:
            /help - Info about available commands
            /upcoming_events - Info regarding upcoming events, here you can also subscribe to any event individually
            /settings - Here you can setup notification settings
            /suggest - If you have an event, you can suggest it to be added to the bot by using:
            /suggest <your message>
            """;

    public static final String BOT_FULL_INFO_MESSAGE = BOT_INFO_MESSAGE + "\n" + BOT_COMMANDS_MESSAGE;
    public static final String YES_OF_COURSE = "Yes, of course";
    public static final String NO_UPCOMING_EVENTS = "There are no upcoming events";
    public static final String ENABLE_NEW_EVENT_NOTIFICATIONS = "ENABLE_NEW_EVENT_NOTIFICATIONS";
    public static final String DISABLE_NEW_EVENT_NOTIFICATIONS = "DISABLE_NEW_EVENT_NOTIFICATIONS";
    public static final String NAH_I_DONT_LIKE_SPAM = "Nah, I don't like spam";
    public static final String RETURN_BACK_BUTTON = "Return back";
    public static final String OPEN_UPCOMING_EVENTS = "OPEN_UPCOMING_EVENTS";
    public static final String USER_STATES_MAPDB_KEY = "userStates";
    public static final String SUBSCRIBE_TO_EVENT = "SUBSCRIBE_TO_EVENT";
    public static final String UNSUBSCRIBE_FROM_EVENT = "UNSUBSCRIBE_FROM_EVENT";
    public static final String SUBSCRIBE_TO_EVENT_FROM_UPCOMING_EVENTS = "SUBSCRIBE_TO_EVENT_FROM_UPCOMING_EVENTS";
    public static final String UNSUBSCRIBE_FROM_EVENT_FROM_UPCOMING_EVENTS = "UNSUBSCRIBE_FROM_EVENT_FROM_UPCOMING_EVENTS";
    public static final String IM_DONE_BUTTON = "IM_DONE_BUTTON";
    public static final String IM_DONE_BUTTON_FROM_SETTINGS = "IM_DONE_BUTTON_FROM_SETTINGS";
    public static final String OPEN_EVENT_INFORMATION = "OPEN_EVENT_INFORMATION";
    public static final String EVENT_NAME_EMOJI = "\uD83C\uDF97\uFE0F ";
    public static final String TIME_EMOJI = "⏰ ";
    public static final String DESCRIPTION_EMOJI = "\uD83D\uDCD6 ";
    public static final String LINK_EMOJI = "\uD83C\uDF0D ";
    public static final String CHECK_MARK_EMOJI = "✅ ";
    public static final String CROSS_MARK_EMOJI = "❌ ";
    public static final String GREEN_DOT_EMOJI = "🟢 ";
    public static final String RED_DOT_EMOJI = "🔴 ";
    public static final String BLUE_DOT_EMOJI = "🔵 ";
    public static final String EXCLAMATION_MARK_EMOJI = "❗";
    public static String NAME_STARTS_WITH = "Name: ";
    public static String TIME_STARTS_WITH = "Time: ";
    public static String DESCRIPTION_STARTS_WITH = "Description: ";
    public static String LINK_STARTS_WITH = "Link: ";
    public static final String EVENT_TEMPLATE_SIMPLE =
            NAME_STARTS_WITH + "%s\n"
                    + TIME_STARTS_WITH + "%s\n"
                    + DESCRIPTION_STARTS_WITH + "%s\n"
                    + LINK_STARTS_WITH + "%s";
    public static String NEW_EVENT_MESSAGE = "Please send event in following format:\n" + EVENT_TEMPLATE_SIMPLE.formatted("", "yyyy-MM-dd HH:MM", "", "");
    public static final String EVENT_TEMPLATE =
            EVENT_NAME_EMOJI + NAME_STARTS_WITH + "%s\n"
                    + TIME_EMOJI + TIME_STARTS_WITH + "%s\n"
                    + DESCRIPTION_EMOJI + DESCRIPTION_STARTS_WITH + "%s\n"
                    + LINK_EMOJI + LINK_STARTS_WITH + "%s";
}
