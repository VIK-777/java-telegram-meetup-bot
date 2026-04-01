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
            /suggest - If you have an event, you can suggest it to be added to the bot. Also could be using as:
            /suggest <your message>
            """;

    public static final String BOT_FULL_INFO_MESSAGE = BOT_INFO_MESSAGE + "\n" + BOT_COMMANDS_MESSAGE;
    public static final String YES_OF_COURSE = "Yes, of course";
    public static final String NO_UPCOMING_EVENTS = "There are no upcoming events";
    public static final String ENABLE_NEW_EVENT_NOTIFICATIONS = "ENABLE_NEW_EVENT_NOTIFICATIONS";
    public static final String DISABLE_NEW_EVENT_NOTIFICATIONS = "DISABLE_NEW_EVENT_NOTIFICATIONS";
    public static final String NAH_I_DONT_LIKE_SPAM = "Nah, I don't like spam";
    public static final CustomEmoji RETURN_BACK_EMOJI = new CustomEmoji("\uD83D\uDD19", "5352759161945867747");
    public static final String RETURN_BACK_BUTTON = "Return";
    public static final String OPEN_UPCOMING_EVENTS = "OPEN_UPCOMING_EVENTS";
    public static final String USER_STATES_MAPDB_KEY = "userStates";
    public static final String USER_STATES_PARAMS_MAPDB_KEY = "userStatesParams";
    public static final String SUBSCRIBE_TO_EVENT = "SUBSCRIBE_TO_EVENT";
    public static final String UNSUBSCRIBE_FROM_EVENT = "UNSUBSCRIBE_FROM_EVENT";
    public static final String SUBSCRIBE_TO_EVENT_FROM_UPCOMING_EVENTS = "SUBSCRIBE_TO_EVENT_FROM_UPCOMING_EVENTS";
    public static final String UNSUBSCRIBE_FROM_EVENT_FROM_UPCOMING_EVENTS = "UNSUBSCRIBE_FROM_EVENT_FROM_UPCOMING_EVENTS";
    public static final String IM_DONE_BUTTON = "IM_DONE_BUTTON";
    public static final String IM_DONE_BUTTON_FROM_SETTINGS = "IM_DONE_BUTTON_FROM_SETTINGS";
    public static final String OPEN_EVENT_INFORMATION = "OPEN_EVENT_INFORMATION";
    public static final CustomEmoji EVENT_NAME_EMOJI = new CustomEmoji("\uD83C\uDF97", "5454172415070315962");
    public static final String EVENT_NAME_EMOJI_HTML_STRING = EVENT_NAME_EMOJI.toHtmlString() + " ";
    public static final CustomEmoji TIME_EMOJI = new CustomEmoji("⏰", "5413704112220949842");
    public static final String TIME_EMOJI_HTML_STRING = TIME_EMOJI.toHtmlString() + " ";
    public static final CustomEmoji DESCRIPTION_EMOJI = new CustomEmoji("\uD83D\uDCD6", "5226512880362332956");
    public static final String DESCRIPTION_EMOJI_HTML_STRING = DESCRIPTION_EMOJI.toHtmlString() + " ";
    public static final CustomEmoji LINK_EMOJI = new CustomEmoji("\uD83D\uDD17", "5375129357373165375");
    public static final String LINK_EMOJI_HTML_STRING = LINK_EMOJI.toHtmlString() + " ";
    public static final CustomEmoji CHECK_MARK_EMOJI = new CustomEmoji("✅", "5427009714745517609");
    public static final String CHECK_MARK_EMOJI_HTML_STRING = CHECK_MARK_EMOJI.toHtmlString() + " ";
    public static final CustomEmoji CROSS_MARK_EMOJI = new CustomEmoji("❌", "5465665476971471368");
    public static final String CROSS_MARK_EMOJI_HTML_STRING = CROSS_MARK_EMOJI.toHtmlString() + " ";
    public static final String GREEN_DOT_EMOJI = "🟢 ";
    public static final String RED_DOT_EMOJI = "🔴 ";
    public static final String BLUE_DOT_EMOJI = "🔵 ";
    public static final CustomEmoji EXCLAMATION_MARK_EMOJI = new CustomEmoji("❗", "5467928559664242360");
    public static final String EXCLAMATION_MARK_EMOJI_HTML_STRING = EXCLAMATION_MARK_EMOJI.toHtmlString() + " ";
    public static final String EXCLAMATION_MARK_EMOJI_TEXT = EXCLAMATION_MARK_EMOJI.emoji();
    public static final CustomEmoji LOCATION_EMOJI = new CustomEmoji("\uD83D\uDCCD", "5321275372333979355");
    public static final String LOCATION_EMOJI_HTML_STRING = LOCATION_EMOJI.toHtmlString() + " ";
    public static final String FLOPPY_DISK_EMOJI = "\uD83D\uDCBE ";
    public static final String NEW_EMOJI = "\uD83C\uDD95 ";
    public static final String SMILE_WITH_TEAR_EMOJI = "\uD83D\uDE22 ";
    public static String NAME_STARTS_WITH = "Name: ";
    public static String TIME_STARTS_WITH = "Time: ";
    public static String DESCRIPTION_STARTS_WITH = "Description: ";
    public static String LINK_STARTS_WITH = "Link: ";
    public static String LOCATION_STARTS_WITH = "Location: ";
    public static final String EVENT_TEMPLATE_SIMPLE =
            NAME_STARTS_WITH + "%s\n"
                    + TIME_STARTS_WITH + "%s\n"
                    + DESCRIPTION_STARTS_WITH + "%s\n"
                    + LOCATION_STARTS_WITH + "%s\n"
                    + LINK_STARTS_WITH + "%s";
    public static String NEW_EVENT_MESSAGE = "Please send event in following format:\n<blockquote>" + EVENT_TEMPLATE_SIMPLE.formatted("", "yyyy-MM-dd HH:MM", "", "", "") + "</blockquote>";
    public static final String EVENT_TEMPLATE = """
        %s<b>%s</b>
        %s
        
        %s%s
        
        %s<a href="%s">Open event page</a>""".formatted(EVENT_NAME_EMOJI_HTML_STRING, "%s", "%s", TIME_EMOJI_HTML_STRING, "%s", LINK_EMOJI_HTML_STRING, "%s");
    public static final String IM_DONE_BUTTON_TEXT = FLOPPY_DISK_EMOJI + "I'm done";
    public static final String NOTIFICATIONS_SETTINGS_TEXT = "Tick which notifications you want to receive and then click \"" + IM_DONE_BUTTON_TEXT + "\"";
    public static final String FEATURE_INLINE_QUERY_MESSAGE = NEW_EMOJI + """
            Now <b>sharing meetups</b> became much more easier!!!
            You can write following in <b>any</b> chat:
            
            @meetup_calendar_bot <i>your search</i>
            
            And then just clicking on the meetup, it will be sent there!
            
            Try it out and share something interesting with your friends! 😉
            
            <i>P.S.</i> If you don't want to receive notifications about new features, you can disable it in /settings <span class="tg-spoiler">but I will feel very sad 😢</span>
            """;
}
