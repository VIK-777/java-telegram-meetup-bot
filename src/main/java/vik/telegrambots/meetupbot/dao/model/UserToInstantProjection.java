package vik.telegrambots.meetupbot.dao.model;

import java.time.Instant;

public record UserToInstantProjection(Long userId, Instant lastUpdatedDate) {
    
}
