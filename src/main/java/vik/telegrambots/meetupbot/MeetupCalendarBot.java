package vik.telegrambots.meetupbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.toggle.AbilityToggle;
import org.telegram.abilitybots.api.toggle.CustomToggle;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.abilitybots.api.util.Pair;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import vik.telegrambots.meetupbot.dao.jparepository.EventSubscriptionsJpaRepository;
import vik.telegrambots.meetupbot.dao.jparepository.EventsJpaRepository;
import vik.telegrambots.meetupbot.dao.jparepository.UpdatesJpaRepository;
import vik.telegrambots.meetupbot.dao.jparepository.UsersJpaRepository;
import vik.telegrambots.meetupbot.dao.model.Event;
import vik.telegrambots.meetupbot.dao.model.EventSubscription;
import vik.telegrambots.meetupbot.dao.model.User;
import vik.telegrambots.meetupbot.utils.ActionsExecutor;
import vik.telegrambots.meetupbot.utils.Constants;
import vik.telegrambots.meetupbot.utils.UserNotificationToggle;
import vik.telegrambots.meetupbot.utils.UserState;
import vik.telegrambots.meetupbot.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.CREATOR;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.addTag;
import static org.telegram.abilitybots.api.util.AbilityUtils.fullName;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;
import static org.telegram.abilitybots.api.util.AbilityUtils.getUser;
import static vik.telegrambots.meetupbot.utils.Constants.BLUE_DOT_EMOJI;
import static vik.telegrambots.meetupbot.utils.Constants.BOT_COMMANDS_MESSAGE;
import static vik.telegrambots.meetupbot.utils.Constants.BOT_FULL_INFO_MESSAGE;
import static vik.telegrambots.meetupbot.utils.Constants.BOT_INFO_MESSAGE;
import static vik.telegrambots.meetupbot.utils.Constants.CHECK_MARK_EMOJI;
import static vik.telegrambots.meetupbot.utils.Constants.CROSS_MARK_EMOJI;
import static vik.telegrambots.meetupbot.utils.Constants.DISABLE_NEW_EVENT_NOTIFICATIONS;
import static vik.telegrambots.meetupbot.utils.Constants.ENABLE_NEW_EVENT_NOTIFICATIONS;
import static vik.telegrambots.meetupbot.utils.Constants.EXCLAMATION_MARK_EMOJI;
import static vik.telegrambots.meetupbot.utils.Constants.FEATURE_INLINE_QUERY_MESSAGE;
import static vik.telegrambots.meetupbot.utils.Constants.GREEN_DOT_EMOJI;
import static vik.telegrambots.meetupbot.utils.Constants.IM_DONE_BUTTON;
import static vik.telegrambots.meetupbot.utils.Constants.IM_DONE_BUTTON_FROM_SETTINGS;
import static vik.telegrambots.meetupbot.utils.Constants.IM_DONE_BUTTON_TEXT;
import static vik.telegrambots.meetupbot.utils.Constants.NAH_I_DONT_LIKE_SPAM;
import static vik.telegrambots.meetupbot.utils.Constants.NEW_EVENT_MESSAGE;
import static vik.telegrambots.meetupbot.utils.Constants.NOTIFICATIONS_SETTINGS_TEXT;
import static vik.telegrambots.meetupbot.utils.Constants.NO_UPCOMING_EVENTS;
import static vik.telegrambots.meetupbot.utils.Constants.OPEN_EVENT_INFORMATION;
import static vik.telegrambots.meetupbot.utils.Constants.OPEN_UPCOMING_EVENTS;
import static vik.telegrambots.meetupbot.utils.Constants.RETURN_BACK_BUTTON;
import static vik.telegrambots.meetupbot.utils.Constants.SUBSCRIBE_TO_EVENT;
import static vik.telegrambots.meetupbot.utils.Constants.SUBSCRIBE_TO_EVENT_FROM_UPCOMING_EVENTS;
import static vik.telegrambots.meetupbot.utils.Constants.UNSUBSCRIBE_FROM_EVENT;
import static vik.telegrambots.meetupbot.utils.Constants.UNSUBSCRIBE_FROM_EVENT_FROM_UPCOMING_EVENTS;
import static vik.telegrambots.meetupbot.utils.Constants.YES_OF_COURSE;
import static vik.telegrambots.meetupbot.utils.KeyboardFactory.getInlineGridForPairs;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_12_HOURS_NOTIFICATION;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_12_HOURS_NOTIFICATION_FROM_SETTINGS;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_1_DAY_NOTIFICATION;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_1_DAY_NOTIFICATION_FROM_SETTINGS;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_1_HOUR_NOTIFICATION;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_1_HOUR_NOTIFICATION_FROM_SETTINGS;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_1_WEEK_NOTIFICATION;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_1_WEEK_NOTIFICATION_FROM_SETTINGS;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_6_HOURS_NOTIFICATION;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_6_HOURS_NOTIFICATION_FROM_SETTINGS;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_INFO_NOTIFICATION;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_INFO_NOTIFICATION_FROM_SETTINGS;
import static vik.telegrambots.meetupbot.utils.UserNotificationToggle.TOGGLE_NEW_EVENTS_NOTIFICATION_FROM_SETTINGS;
import static vik.telegrambots.meetupbot.utils.Utils.writeDateTimeForInlineQuerySearch;

@Slf4j
@Component
public class MeetupCalendarBot extends AbilityBot {

    private final Map<Long, UserState> userStates;
    private final AtomicLong inlineQueryId;

    private final long creatorId;
    private final ActionsExecutor actionsExecutor;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsersJpaRepository usersRepository;
    @Autowired
    private UpdatesJpaRepository updatesRepository;
    @Autowired
    private EventSubscriptionsJpaRepository eventSubscriptionsRepository;
    @Autowired
    private EventsJpaRepository eventsRepository;
    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    public MeetupCalendarBot(Environment env, DBContext db) {
        super(env.getProperty("bot.token"), env.getProperty("bot.name"), db, getBotToggle(), getBotOptions());
        creatorId = Long.parseLong(Objects.requireNonNull(env.getProperty("bot.creator-id")));
        actionsExecutor = new ActionsExecutor(this);
        userStates = db.getMap(Constants.USER_STATES_MAPDB_KEY);
        AtomicLong id = (AtomicLong) db.getVar("InlineQueryId").get();
        inlineQueryId = Objects.requireNonNullElseGet(id, () -> new AtomicLong(10));
        log.info("Starting bot");
    }

    private static DefaultBotOptions getBotOptions() {
        var options = new DefaultBotOptions();
        var allowedUpdates = List.of(
                "update_id",
                "message",
                "inline_query",
                "chosen_inline_result",
                "callback_query",
                "edited_message",
                "channel_post",
                "edited_channel_post",
                "shipping_query",
                "pre_checkout_query",
                "poll",
                "poll_answer",
                "my_chat_member",
                "chat_member",
                "chat_join_request");
        options.setAllowedUpdates(allowedUpdates);
        return options;
    }

    private static AbilityToggle getBotToggle() {
        return new CustomToggle()
                .turnOff("backup")
                .turnOff("claim")
                .turnOff("recover")
                .turnOff("report")
                .turnOff("stats")
                .turnOff("ban")
                .turnOff("unban")
                .turnOff("promote")
                .turnOff("demote");
    }

    @PostConstruct
    public void initialize() {
        eventsRepository.findUpcomingEvents().forEach(this::scheduleNotifications);
    }

    @Override
    public long creatorId() {
        return creatorId;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Stream.of(update)
                .peek(this::saveUpdateToDatabase)
                .peek(this::saveUserToDatabase)
                .forEach(super::onUpdateReceived);
    }

    private void saveUpdateToDatabase(Update update) {
        try {
            updatesRepository.save(vik.telegrambots.meetupbot.dao.model.Update.builder()
                    .chatId(getChatId(update))
                    .fromUser(getUser(update).getId())
                    .javaClass(Update.class.getCanonicalName())
                    .updateRaw(objectMapper.writeValueAsString(update))
                    .build());
        } catch (JsonProcessingException e) {
            log.error("Couldn't persist update to database: {}", update);
        }
    }

    private void saveUserToDatabase(Update update) {
        var telegramUser = getUser(update);
        var userToSave = usersRepository.findById(telegramUser.getId())
                .map(user -> user.updateFieldsFromTelegramUser(telegramUser))
                .orElse(User.fromTelegramUser(telegramUser));
        usersRepository.save(userToSave);
    }

    @SneakyThrows
    public void replyToStart(MessageContext ctx) {
        var chatId = ctx.chatId();
        actionsExecutor.sendMessage(chatId, BOT_INFO_MESSAGE);
        actionsExecutor.sendMessage(chatId, "So let's setup behaviour. Do you want to receive notifications about events?",
                getInlineGridForPairs(List.of(Pair.of(YES_OF_COURSE, ENABLE_NEW_EVENT_NOTIFICATIONS), Pair.of(NAH_I_DONT_LIKE_SPAM, DISABLE_NEW_EVENT_NOTIFICATIONS)), 1));
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info("Start command")
                .locality(USER)
                .privacy(PUBLIC)
                .action(this::replyToStart)
                .enableStats()
                .build();
    }

    public Ability newEvent() {
        return Ability
                .builder()
                .name("new_event")
                .info("Create new event")
                .locality(USER)
                .privacy(ADMIN)
                .action(this::newEvent)
                .build();
    }

    public Ability info() {
        return Ability
                .builder()
                .name("help")
                .info("Available bot commands")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> actionsExecutor.sendMessage(ctx.chatId(), BOT_FULL_INFO_MESSAGE))
                .build();
    }

    public Ability turnOnInfoNotificationsForEveryone() {
        return Ability
                .builder()
                .name("turn_on_info_everyone")
                .info("Turn on info notifications for everyone")
                .locality(USER)
                .privacy(CREATOR)
                .action(ctx -> {
                    var allUsers = usersRepository.findAll();
                    allUsers.forEach(user -> user.setSendInfoNotifications(true));
                    usersRepository.saveAll(allUsers);
                    actionsExecutor.sendMessage(ctx.chatId(), "Done");
                })
                .build();
    }

    public Ability sendLatestNews() {
        return Ability
                .builder()
                .name("send_latest_news")
                .info("send latest news")
                .locality(USER)
                .privacy(CREATOR)
                .action(ctx -> {
                    var pathToFile = "classpath:gifs/20240911_inline_query_update.MP4";
                    try {
                        var inputFile = new InputFile(ResourceUtils.getFile(pathToFile));
                        var usersForNotifications = usersRepository.findAllBySendInfoNotifications(true);
                        usersForNotifications.forEach(user -> actionsExecutor.sendAnimation(user.getUserId(), inputFile, FEATURE_INLINE_QUERY_MESSAGE));
                        actionsExecutor.sendMessage(ctx.chatId(), "Latest news were sent");
                    } catch (FileNotFoundException e) {
                        log.error("File was not found: {}", pathToFile);
                        actionsExecutor.sendMessage(ctx.chatId(), "File was not found");
                    }
                })
                .build();
    }

    public Ability suggestEvent() {
        return Ability
                .builder()
                .name("suggest")
                .info("Suggest an event. Should be invoked as /suggest <your text>")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    var chatId = ctx.chatId();
                    if (creatorId() == chatId) {
                        newEvent(ctx);
                        return;
                    }
                    if (ctx.arguments().length == 0) {
                        actionsExecutor.sendMessage(chatId, "Please invoke command as /suggest <your text>");
                        return;
                    }
                    var name = ctx.user().getUserName() != null ? addTag(ctx.user().getUserName()) : fullName(ctx.user());
                    actionsExecutor.sendMessage(creatorId, "New suggestion:\n%s: %s".formatted(name, String.join(" ", ctx.arguments())));
                    actionsExecutor.sendMessage(chatId, "Thank you! Your message was sent to the owner");
                })
                .build();
    }

    public Ability usersList() {
        return Ability
                .builder()
                .name("users")
                .info("Get all users")
                .locality(USER)
                .privacy(CREATOR)
                .action(ctx -> {
                    var chatId = ctx.chatId();
                    Function<User, String> comparator = (User user) -> "%011d".formatted(user.getUserId());
                    var map = updatesRepository.findUsersToUpdateTimeAsMap();
                    if (ctx.arguments().length > 0) {
                        comparator = switch (ctx.firstArg()) {
                            case "name" -> (User user) -> fullName(user.toTelegramUser());
                            case "username" -> (User user) -> {
                                if (user.getUserName() == null) {
                                    return "null";
                                }
                                return user.getUserName();
                            };
                            case "activity" ->
                                    (User user) -> String.valueOf(map.getOrDefault(user.getUserId(), Instant.EPOCH));
                            default -> comparator;
                        };
                    }
                    var message = usersRepository.findAll().stream()
                            .sorted(Comparator.comparing(comparator))
                            .map((User user) -> {
                                var result = "%d: %s, %s".formatted(user.getUserId(), fullName(user.toTelegramUser()), user.getUserName());
                                if (ctx.arguments().length > 0 && "activity".equals(ctx.firstArg())) {
                                    result += ", %s".formatted(map.get(user.getUserId()));
                                }
                                return result;
                            })
                            .collect(Collectors.joining("\n"));
                    actionsExecutor.sendMessage(chatId, message);
                })
                .build();
    }

    public Ability sendPrivateMessage() {
        return Ability
                .builder()
                .name("send_private_message")
                .info("Send private message to user")
                .locality(USER)
                .privacy(CREATOR)
                .action(ctx -> {
                    var toUser = ctx.firstArg();
                    var message = Arrays.stream(ctx.arguments()).skip(1).collect(Collectors.joining(" "));
                    var result = actionsExecutor.sendMessage(Long.valueOf(toUser), message);
                    if (result != null) {
                        actionsExecutor.sendMessage(ctx.chatId(), "Your message was sent");
                    } else {
                        actionsExecutor.sendMessage(ctx.chatId(), "Message was not sent, there was an error");
                    }
                })
                .build();
    }

    public Ability dump() {
        return Ability
                .builder()
                .name("dump")
                .info("Dump all data")
                .locality(USER)
                .privacy(CREATOR)
                .action(ctx -> {
                    var chatId = ctx.chatId();
                    var objectMapper = new ObjectMapper().findAndRegisterModules();
                    var folder = new File(ctx.firstArg(), DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm").format(LocalDateTime.now()));
                    folder.mkdir();
                    actionsExecutor.sendMessage(chatId, "Dumping data to %s".formatted(folder.getAbsolutePath()));
                    Map<String, Supplier<List<?>>> map = Map.of(
                            "users", () -> usersRepository.findAll(),
                            "events", () -> eventsRepository.findAll(),
                            "eventSubscriptions", () -> eventSubscriptionsRepository.findAll(),
                            "updates", () -> updatesRepository.findAll()
                    );
                    map.forEach((name, supplier) -> {
                        var file = new File(folder, name + ".json");
                        try (PrintStream printStream = new PrintStream(file)) {
                            objectMapper.writeValue(printStream, supplier.get());
                            actionsExecutor.sendDocument(chatId, new InputFile(file));
                        } catch (IOException e) {
                            log.error("Exception caught", e);
                            actionsExecutor.sendMessage(chatId, "Error dumping data: %s".formatted(name));
                        }
                    });
                })
                .build();
    }

    public Ability load() {
        return Ability
                .builder()
                .name("load")
                .info("Load dumped data")
                .locality(USER)
                .privacy(CREATOR)
                .action(ctx -> {
                    var chatId = ctx.chatId();
                    var objectMapper = new ObjectMapper().findAndRegisterModules();
                    var folder = new File(ctx.firstArg());
                    try {
                        usersRepository.deleteAll();
                        var users = objectMapper.readValue(new File(folder, "users.json"), new TypeReference<List<User>>() {
                        });
                        usersRepository.saveAll(users);
                        log.info("Users loaded");
                        eventsRepository.deleteAll();
                        var events = objectMapper.readValue(new File(folder, "events.json"), new TypeReference<List<Event>>() {
                        });
                        eventsRepository.saveAll(events);
                        log.info("Events loaded");
                        eventSubscriptionsRepository.deleteAll();
                        var eventSubscriptions = objectMapper.readValue(new File(folder, "eventSubscriptions.json"), new TypeReference<List<EventSubscription>>() {
                        });
                        eventSubscriptionsRepository.saveAll(eventSubscriptions);
                        log.info("Event subscriptions loaded");
                        updatesRepository.deleteAll();
                        var updates = objectMapper.readValue(new File(folder, "updates.json"), new TypeReference<List<vik.telegrambots.meetupbot.dao.model.Update>>() {
                        });
                        updatesRepository.saveAll(updates);
                        log.info("Updates loaded");
                        actionsExecutor.sendMessage(chatId, "Data loaded");
                    } catch (IOException e) {
                        log.error("Exception caught", e);
                        actionsExecutor.sendMessage(chatId, "Error loading data: %s".formatted(e.getMessage()));
                    }
                })
                .build();
    }

    public Ability upcomingEvents() {
        return Ability
                .builder()
                .name("upcoming_events")
                .info("Upcoming events")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    var chatId = ctx.chatId();
                    var messageAndButtons = getMessageAndButtonsForUpcomingEvents(chatId);
                    if (messageAndButtons.b().isEmpty()) {
                        actionsExecutor.sendMessage(chatId, NO_UPCOMING_EVENTS);
                        return;
                    }
                    actionsExecutor.sendMessage(chatId, messageAndButtons.a(), getInlineGridForPairs(messageAndButtons.b(), 1));
                })
                .build();
    }

    private Pair<String, List<Pair<String, String>>> getMessageAndButtonsForUpcomingEvents(long userId) {
        var sb = new StringBuilder("All upcoming events:");
        var userSubscriptions = eventSubscriptionsRepository.findAllByUserIdAsMap(userId);
        var buttons = eventsRepository.findUpcomingEvents().stream()
                .map(event -> {
                    var isSubscribed = userSubscriptions.getOrDefault(event.getEventId(), false);
                    var text = Utils.writeDateTime(event.getEventTime()) + ": " + event.getName();
                    sb.append("\n").append("• ").append(text);
                    return Pair.of((isSubscribed ? GREEN_DOT_EMOJI : BLUE_DOT_EMOJI) + text, OPEN_EVENT_INFORMATION + " " + event.getEventId());
                }).toList();
        sb.append("\n\nYou can click on any button below to get more information and subscribe/unsubscribe to notifications");
        sb.append("\n" + GREEN_DOT_EMOJI + "- subscribed");
        sb.append("\n" + BLUE_DOT_EMOJI + "- not subscribed");
        return Pair.of(sb.toString(), buttons);
    }

    public Ability settings() {
        return Ability
                .builder()
                .name("settings")
                .info("Notification settings")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> actionsExecutor.sendMessage(ctx.chatId(), "Notification settings:\n" + NOTIFICATIONS_SETTINGS_TEXT,
                        getInlineGridForPairs(getPairsForUser(ctx.chatId(), true), 1)))
                .build();
    }

    public Ability eventsAdmin() {
        return Ability
                .builder()
                .name("upcoming_events_admin")
                .info("upcoming_events_admin")
                .locality(USER)
                .privacy(CREATOR)
                .action(ctx -> {
                    var chatId = ctx.chatId();
                    var message = eventsRepository.findUpcomingEvents().stream()
                            .map(event -> "%s: %s".formatted(event.getEventId(), event.getName()))
                            .collect(Collectors.joining("\n"));
                    actionsExecutor.sendMessage(chatId, message);
                })
                .build();
    }

    public Ability deleteEvent() {
        return Ability
                .builder()
                .name("delete_event")
                .info("delete_event")
                .locality(USER)
                .privacy(CREATOR)
                .action(ctx -> {
                    var chatId = ctx.chatId();
                    eventsRepository.deleteById(Long.valueOf(ctx.firstArg()));
                    actionsExecutor.sendMessage(chatId, "Event %s was deleted".formatted(ctx.firstArg()));
                })
                .build();
    }

    @SneakyThrows
    public void newEvent(MessageContext ctx) {
        var chatId = ctx.chatId();
        actionsExecutor.sendMessage(chatId, NEW_EVENT_MESSAGE);
        userStates.put(chatId, UserState.WAITING_NEW_EVENT);
    }

    public Reply replyToMessageInPrivateChat() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> {
            var chatId = getChatId(upd);
            var messageText = upd.getMessage().getText().replace("``", "`");
            if (Objects.requireNonNull(userStates.get(chatId)) == UserState.WAITING_NEW_EVENT) {
                var event = Event.fromMessageText(messageText);
                event.setFromUser(chatId);
                if (!ObjectUtils.allNotNull(event.getName(), event.getEventTime(), event.getDescription(), event.getLink())) {
                    actionsExecutor.sendMessage(chatId, "Can't parse message, please correct");
                    return;
                }
                var savedEvent = eventsRepository.save(event);
                log.info("Saved event: {}", savedEvent);
                actionsExecutor.sendMessage(chatId, "New event was saved");
                var buttonPairs = List.of(Pair.of("Send me notifications", SUBSCRIBE_TO_EVENT + " " + savedEvent.getEventId()),
                        Pair.of("I'll skip this one", UNSUBSCRIBE_FROM_EVENT + " " + savedEvent.getEventId()));
                getAllSubscribedUsers().forEach(id -> {
                    var result = actionsExecutor.sendMessage(id, savedEvent.toMessageText(), getInlineGridForPairs(buttonPairs, 1), ActionsExecutor.ParseMode.MARKDOWN);
                    if (result != null) {
                        log.info("Notification was sent to user {}", id);
                    }
                });
                actionsExecutor.sendMessage(chatId, "All users were notified");
                scheduleNotifications(savedEvent);
                actionsExecutor.sendMessage(chatId, "And notifications scheduled");
            } else {
                actionsExecutor.sendMessage(chatId, "Sorry, can't understand, you can start again at any time - /start");
            }
        };

        return Reply.of(action, Flag.TEXT, update -> !update.getMessage().isCommand(), AbilityUtils::isUserMessage);
    }

    public Reply replyToInlineQuery() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> {
            var searchQuery = upd.getInlineQuery().getQuery();
            List<Event> events;
            if (searchQuery.isBlank()) {
                events = eventsRepository.findUpcomingEvents();
            } else {
                var lowerCaseSearchQuery = searchQuery.toLowerCase();
                events = eventsRepository.findUpcomingEvents().stream()
                        .filter(event -> event.getName().toLowerCase().contains(lowerCaseSearchQuery)
                                || event.getDescription().toLowerCase().contains(lowerCaseSearchQuery)
                                || writeDateTimeForInlineQuerySearch(event.getEventTime()).contains(lowerCaseSearchQuery))
                        .toList();
            }
            var answer = AnswerInlineQuery.builder()
                    .inlineQueryId(upd.getInlineQuery().getId())
                    .results(events.stream().map(event -> InlineQueryResultArticle.builder()
                            .id(String.valueOf(inlineQueryId.getAndIncrement()))
                            .title(event.getName())
                            .description(Utils.writeDateTime(event.getEventTime()) + "\n" + event.getDescription())
                            .inputMessageContent(InputTextMessageContent.builder()
                                    .messageText(event.toMessageText() + "\n\n" + EXCLAMATION_MARK_EMOJI + "More events in @meetup\\_calendar\\_bot")
                                    .parseMode(ActionsExecutor.ParseMode.MARKDOWN.getAsString())
                                    .build())
                            .build()).toList())
                    .build();
            actionsExecutor.sendInlineQueryResult(answer);
        };

        return Reply.of(action, Flag.INLINE_QUERY);
    }

    private List<Pair<String, String>> getPairsForUser(Long userId, boolean isFromSettings) {
        List<Pair<String, String>> notificationPairs = new ArrayList<>();
        usersRepository.findById(userId).ifPresent(user -> {
            notificationPairs.add(getPairForOption(userId, user.getSendInfoNotifications(), isFromSettings ? TOGGLE_INFO_NOTIFICATION_FROM_SETTINGS : TOGGLE_INFO_NOTIFICATION));
            if (isFromSettings) {
                notificationPairs.add(getPairForOption(userId, user.getSendNotifications(), TOGGLE_NEW_EVENTS_NOTIFICATION_FROM_SETTINGS));
            }
            notificationPairs.add(getPairForOption(userId, user.getOneWeekNotification(), isFromSettings ? TOGGLE_1_WEEK_NOTIFICATION_FROM_SETTINGS : TOGGLE_1_WEEK_NOTIFICATION));
            notificationPairs.add(getPairForOption(userId, user.getOneDayNotification(), isFromSettings ? TOGGLE_1_DAY_NOTIFICATION_FROM_SETTINGS : TOGGLE_1_DAY_NOTIFICATION));
            notificationPairs.add(getPairForOption(userId, user.getTwelveHoursNotification(), isFromSettings ? TOGGLE_12_HOURS_NOTIFICATION_FROM_SETTINGS : TOGGLE_12_HOURS_NOTIFICATION));
            notificationPairs.add(getPairForOption(userId, user.getSixHoursNotification(), isFromSettings ? TOGGLE_6_HOURS_NOTIFICATION_FROM_SETTINGS : TOGGLE_6_HOURS_NOTIFICATION));
            notificationPairs.add(getPairForOption(userId, user.getOneHourNotification(), isFromSettings ? TOGGLE_1_HOUR_NOTIFICATION_FROM_SETTINGS : TOGGLE_1_HOUR_NOTIFICATION));
        });
        notificationPairs.add(Pair.of(IM_DONE_BUTTON_TEXT, isFromSettings ? IM_DONE_BUTTON_FROM_SETTINGS : IM_DONE_BUTTON));
        return notificationPairs;
    }

    private Pair<String, String> getPairForOption(Long userId, Boolean isEnabled, UserNotificationToggle userNotificationToggle) {
        var emoji = isEnabled ? CHECK_MARK_EMOJI : userNotificationToggle.isCrossMarkEmoji() ? CROSS_MARK_EMOJI : "";
        return Pair.of(emoji + userNotificationToggle.getButtonText(), userNotificationToggle.name() + " " + userId);
    }

    public Reply replyToCallbackQueryInPrivateChat() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> {
            var chatId = getChatId(upd);
            var callbackData = upd.getCallbackQuery().getData();
            var parts = callbackData.split(" ");
            var callbackQueryAction = parts[0];
            switch (callbackQueryAction) {
                case ENABLE_NEW_EVENT_NOTIFICATIONS, DISABLE_NEW_EVENT_NOTIFICATIONS -> {
                    usersRepository.findById(chatId).ifPresent(user -> {
                        user.setSendNotifications(callbackQueryAction.equals(ENABLE_NEW_EVENT_NOTIFICATIONS));
                        usersRepository.save(user);
                    });
                    actionsExecutor.updateMessageText(upd, "Thank you! Let's setup additional notifications.\n" + NOTIFICATIONS_SETTINGS_TEXT,
                            getInlineGridForPairs(getPairsForUser(chatId, false), 1));
                }
                case SUBSCRIBE_TO_EVENT, SUBSCRIBE_TO_EVENT_FROM_UPCOMING_EVENTS -> {
                    var eventId = Long.parseLong(parts[1]);
                    eventsRepository.findById(eventId).ifPresent(event -> {
                        var isFromUpcomingEvents = callbackQueryAction.equals(SUBSCRIBE_TO_EVENT_FROM_UPCOMING_EVENTS);
                        var eventSubscription = eventSubscriptionsRepository.findByEventIdAndUserId(eventId, chatId)
                                .orElse(EventSubscription.builder()
                                        .eventId(eventId)
                                        .userId(chatId)
                                        .build());
                        eventSubscription.setSubscribed(true);
                        eventSubscriptionsRepository.save(eventSubscription);
                        var buttons = new ArrayList<>(List.of(getSubscriptionButton(true, eventId, isFromUpcomingEvents)));
                        if (isFromUpcomingEvents) {
                            buttons.add(Pair.of(RETURN_BACK_BUTTON, OPEN_UPCOMING_EVENTS));
                        }
                        actionsExecutor.updateMessageText(upd, event.toMessageText()
                                        + "\n\nYour choice was saved.\n"
                                        + CHECK_MARK_EMOJI + "You're now receiving notifications for this event",
                                getInlineGridForPairs(buttons, 1),
                                ActionsExecutor.ParseMode.MARKDOWN);
                    });
                }
                case UNSUBSCRIBE_FROM_EVENT, UNSUBSCRIBE_FROM_EVENT_FROM_UPCOMING_EVENTS -> {
                    var isFromUpcomingEvents = callbackQueryAction.equals(UNSUBSCRIBE_FROM_EVENT_FROM_UPCOMING_EVENTS);
                    var eventId = Long.parseLong(parts[1]);
                    eventsRepository.findById(eventId).ifPresent(event -> {
                        var eventSubscription = eventSubscriptionsRepository.findByEventIdAndUserId(eventId, chatId)
                                .orElse(EventSubscription.builder()
                                        .eventId(eventId)
                                        .userId(chatId)
                                        .build());
                        eventSubscription.setSubscribed(false);
                        eventSubscriptionsRepository.save(eventSubscription);
                        var buttons = new ArrayList<>(List.of(getSubscriptionButton(false, eventId, isFromUpcomingEvents)));
                        if (isFromUpcomingEvents) {
                            buttons.add(Pair.of(RETURN_BACK_BUTTON, OPEN_UPCOMING_EVENTS));
                        }
                        actionsExecutor.updateMessageText(upd, event.toMessageText()
                                        + "\n\nYour choice was saved.\n"
                                        + CROSS_MARK_EMOJI + "You're no longer receiving notifications for this event",
                                getInlineGridForPairs(buttons, 1),
                                ActionsExecutor.ParseMode.MARKDOWN);
                    });
                }
                case IM_DONE_BUTTON -> {
                    actionsExecutor.updateMessageText(upd, "Thank you! Initial setup was completed.");
                    actionsExecutor.sendMessage(chatId, BOT_COMMANDS_MESSAGE);
                }
                case IM_DONE_BUTTON_FROM_SETTINGS -> actionsExecutor.updateMessageText(upd, "Preferences were updated");
                case OPEN_EVENT_INFORMATION -> {
                    var eventId = Long.parseLong(parts[1]);
                    eventsRepository.findById(eventId).ifPresent(event -> {
                        var subscribed = eventSubscriptionsRepository.findByEventIdAndUserId(eventId, chatId)
                                .map(EventSubscription::getSubscribed)
                                .orElse(false);
                        actionsExecutor.updateMessageText(upd, event.toMessageText()
                                        + "\n\n" + (subscribed
                                        ? CHECK_MARK_EMOJI + "You're receiving notifications for this event"
                                        : CROSS_MARK_EMOJI + "You're not receiving notifications for this event"),
                                getInlineGridForPairs(List.of(getSubscriptionButton(subscribed, eventId, true),
                                        Pair.of(RETURN_BACK_BUTTON, OPEN_UPCOMING_EVENTS)), 1),
                                ActionsExecutor.ParseMode.MARKDOWN);
                    });
                }
                case OPEN_UPCOMING_EVENTS -> {
                    var messageAndButtons = getMessageAndButtonsForUpcomingEvents(chatId);
                    if (messageAndButtons.b().isEmpty()) {
                        actionsExecutor.updateMessageText(upd, NO_UPCOMING_EVENTS);
                        return;
                    }
                    actionsExecutor.updateMessageText(upd, messageAndButtons.a(), getInlineGridForPairs(messageAndButtons.b(), 1));
                }
                default -> {
                    if (callbackQueryAction.startsWith("TOGGLE")) {
                        var userNotificationToggle = UserNotificationToggle.valueOf(callbackQueryAction);
                        var userId = Long.parseLong(parts[1]);
                        var includeNewEventNotificationsFlag = includeNewEventNotificationsFlag(userNotificationToggle);
                        usersRepository.findById(userId).ifPresent(user -> {
                            var getterAndSetter = getGetterAndSetter(userNotificationToggle, user);
                            toggleUserProperty(getterAndSetter.a(), getterAndSetter.b());
                            usersRepository.save(user);
                        });
                        actionsExecutor.updateMessageText(upd, getInlineGridForPairs(getPairsForUser(userId, includeNewEventNotificationsFlag), 1));
                    }
                }
            }
        };

        return Reply.of(action, Flag.CALLBACK_QUERY, AbilityUtils::isUserMessage);
    }

    private Pair<String, String> getSubscriptionButton(Boolean isAlreadySubscribed, Long eventId, boolean fromUpcomingEvents) {
        return isAlreadySubscribed
                ? Pair.of("Unsubscribe", (fromUpcomingEvents ? UNSUBSCRIBE_FROM_EVENT_FROM_UPCOMING_EVENTS : UNSUBSCRIBE_FROM_EVENT) + " " + eventId)
                : Pair.of("Subscribe", (fromUpcomingEvents ? SUBSCRIBE_TO_EVENT_FROM_UPCOMING_EVENTS : SUBSCRIBE_TO_EVENT) + " " + eventId);
    }

    private void toggleUserProperty(Consumer<Boolean> setter, Supplier<Boolean> getter) {
        setter.accept(!getter.get());
    }

    private Pair<Consumer<Boolean>, Supplier<Boolean>> getGetterAndSetter(UserNotificationToggle callbackAction, User user) {
        return switch (callbackAction) {
            case TOGGLE_NEW_EVENTS_NOTIFICATION, TOGGLE_NEW_EVENTS_NOTIFICATION_FROM_SETTINGS ->
                    Pair.of(user::setSendNotifications, user::getSendNotifications);
            case TOGGLE_INFO_NOTIFICATION, TOGGLE_INFO_NOTIFICATION_FROM_SETTINGS ->
                    Pair.of(user::setSendInfoNotifications, user::getSendInfoNotifications);
            case TOGGLE_1_WEEK_NOTIFICATION, TOGGLE_1_WEEK_NOTIFICATION_FROM_SETTINGS ->
                    Pair.of(user::setOneWeekNotification, user::getOneWeekNotification);
            case TOGGLE_1_DAY_NOTIFICATION, TOGGLE_1_DAY_NOTIFICATION_FROM_SETTINGS ->
                    Pair.of(user::setOneDayNotification, user::getOneDayNotification);
            case TOGGLE_12_HOURS_NOTIFICATION, TOGGLE_12_HOURS_NOTIFICATION_FROM_SETTINGS ->
                    Pair.of(user::setTwelveHoursNotification, user::getTwelveHoursNotification);
            case TOGGLE_6_HOURS_NOTIFICATION, TOGGLE_6_HOURS_NOTIFICATION_FROM_SETTINGS ->
                    Pair.of(user::setSixHoursNotification, user::getSixHoursNotification);
            case TOGGLE_1_HOUR_NOTIFICATION, TOGGLE_1_HOUR_NOTIFICATION_FROM_SETTINGS ->
                    Pair.of(user::setOneHourNotification, user::getOneHourNotification);
        };
    }

    private boolean includeNewEventNotificationsFlag(UserNotificationToggle callbackAction) {
        return switch (callbackAction) {
            case TOGGLE_NEW_EVENTS_NOTIFICATION, TOGGLE_1_WEEK_NOTIFICATION, TOGGLE_1_DAY_NOTIFICATION,
                 TOGGLE_12_HOURS_NOTIFICATION, TOGGLE_6_HOURS_NOTIFICATION, TOGGLE_1_HOUR_NOTIFICATION,
                 TOGGLE_INFO_NOTIFICATION -> false;
            case TOGGLE_NEW_EVENTS_NOTIFICATION_FROM_SETTINGS, TOGGLE_1_WEEK_NOTIFICATION_FROM_SETTINGS,
                 TOGGLE_1_DAY_NOTIFICATION_FROM_SETTINGS, TOGGLE_12_HOURS_NOTIFICATION_FROM_SETTINGS,
                 TOGGLE_6_HOURS_NOTIFICATION_FROM_SETTINGS, TOGGLE_1_HOUR_NOTIFICATION_FROM_SETTINGS,
                 TOGGLE_INFO_NOTIFICATION_FROM_SETTINGS -> true;
        };
    }

    private Set<Long> getAllSubscribedUsers() {
        var users = usersRepository.findAllBySendNotifications(true);
        log.info("Subscribed users: {}", users);
        return users.stream().map(User::getUserId).collect(Collectors.toSet());
    }

    private void scheduleNotifications(Event event) {
        scheduleNotifications(event, event.getEventTime().minus(7, ChronoUnit.DAYS), User::getOneDayNotification, "There is an event in 1 week: %s".formatted(event.getName()));
        scheduleNotifications(event, event.getEventTime().minus(1, ChronoUnit.DAYS), User::getOneDayNotification, "There is an event tomorrow: %s".formatted(event.getName()));
        scheduleNotifications(event, event.getEventTime().minus(12, ChronoUnit.HOURS), User::getTwelveHoursNotification, "%s starts in 12 hours".formatted(event.getName()));
        scheduleNotifications(event, event.getEventTime().minus(6, ChronoUnit.HOURS), User::getSixHoursNotification, "%s starts only in 6 hours!!!".formatted(event.getName()));
        scheduleNotifications(event, event.getEventTime().minus(1, ChronoUnit.HOURS), User::getOneHourNotification, "%s starts only in 1 hour!!!".formatted(event.getName()));
    }

    private void scheduleNotifications(Event event, Instant time, Predicate<User> predicate, String notificationText) {
        if (time.isAfter(Instant.now())) {
            log.info("Scheduling notifications for {} at {}", event.getName(), time);
            Runnable task = () -> getUsersForNotification(event.getEventId(), predicate)
                    .forEach(id -> actionsExecutor.sendMessage(id,
                            """
                                    %s
                                    
                                    Full info:
                                    %s
                                    """
                                    .formatted(notificationText, event.toMessageText()),
                            ActionsExecutor.ParseMode.MARKDOWN));
            taskScheduler.schedule(task, time);
            log.info("Notifications for {} were successfully scheduled", event.getName());
        }
    }

    private Set<Long> getUsersForNotification(Long eventId, Predicate<User> isSubscribedToNotification) {
        return eventSubscriptionsRepository.findAllByEventIdAndSubscribed(eventId, true).stream()
                .map(EventSubscription::getUserId)
                .map(userId -> usersRepository.findById(userId).orElse(null))
                .filter(Objects::nonNull)
                .filter(isSubscribedToNotification)
                .map(User::getUserId)
                .collect(Collectors.toSet());
    }

    @EventListener(ApplicationReadyEvent.class)
    private void notifyOnBotStart() {
        actionsExecutor.sendMessage(creatorId(), "Bot started");
    }
}