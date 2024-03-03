package vik.telegrambots.meetupbot.dao.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.telegram.abilitybots.api.util.AbilityUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

@Data
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements Serializable {

    @Id
    @NonNull
    Long userId;
    @NonNull
    String firstName;
    @NonNull
    Boolean isBot;
    String lastName;
    String userName;
    String languageCode;
    Boolean canJoinGroups;
    Boolean canReadAllGroupMessages;
    Boolean supportInlineQueries;
    Boolean isPremium;
    Boolean addedToAttachmentMenu;

    @NonNull
    @Builder.Default
    Boolean sendNotifications = false;
    @NonNull
    @Builder.Default
    Boolean oneDayNotification = false;
    @NonNull
    @Builder.Default
    Boolean twelveHoursNotification = false;
    @NonNull
    @Builder.Default
    Boolean sixHoursNotification = false;
    @NonNull
    @Builder.Default
    Boolean oneHourNotification = false;

    @Embedded
    TechnicalFields technicalFields;

    public static User fromTelegramUser(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        return User.builder()
                .userId(telegramUser.getId())
                .firstName(telegramUser.getFirstName())
                .isBot(telegramUser.getIsBot())
                .lastName(telegramUser.getLastName())
                .userName(Optional.ofNullable(telegramUser.getUserName()).map(AbilityUtils::addTag).orElse(null))
                .languageCode(telegramUser.getLanguageCode())
                .canJoinGroups(telegramUser.getCanJoinGroups())
                .canReadAllGroupMessages(telegramUser.getCanReadAllGroupMessages())
                .supportInlineQueries(telegramUser.getSupportInlineQueries())
                .isPremium(telegramUser.getIsPremium())
                .addedToAttachmentMenu(telegramUser.getAddedToAttachmentMenu())
                .technicalFields(new TechnicalFields(Instant.now(), Instant.now()))
                .build();
    }

    public org.telegram.telegrambots.meta.api.objects.User toTelegramUser() {
        return new org.telegram.telegrambots.meta.api.objects.User(
                this.getUserId(),
                this.getFirstName(),
                this.getIsBot(),
                this.getLastName(),
                this.getUserName(),
                this.getLanguageCode(),
                this.getCanJoinGroups(),
                this.getCanReadAllGroupMessages(),
                this.getSupportInlineQueries(),
                this.getIsPremium(),
                this.getAddedToAttachmentMenu()
        );
    }

    public User updateFieldsFromTelegramUser(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        this.setFirstName(telegramUser.getFirstName());
        this.setIsBot(telegramUser.getIsBot());
        this.setLastName(telegramUser.getLastName());
        this.setUserName(Optional.ofNullable(telegramUser.getUserName()).map(AbilityUtils::addTag).orElse(null));
        this.setLanguageCode(telegramUser.getLanguageCode());
        this.setCanJoinGroups(telegramUser.getCanJoinGroups());
        this.setCanReadAllGroupMessages(telegramUser.getCanReadAllGroupMessages());
        this.setSupportInlineQueries(telegramUser.getSupportInlineQueries());
        this.setIsPremium(telegramUser.getIsPremium());
        this.setAddedToAttachmentMenu(telegramUser.getAddedToAttachmentMenu());
        if (this.getTechnicalFields() == null) {
            this.setTechnicalFields(new TechnicalFields(Instant.now(), Instant.now()));
        } else {
            this.getTechnicalFields().setLastUpdatedDate(Instant.now());
        }
        return this;
    }
}
