package vik.telegrambots.meetupbot.dao.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import vik.telegrambots.meetupbot.dao.model.EventSubscription;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface EventSubscriptionsJpaRepository extends JpaRepository<EventSubscription, Long> {

    Optional<EventSubscription> findByEventIdAndUserId(Long eventId, Long userId);

    List<EventSubscription> findAllByEventIdAndSubscribed(Long eventId, Boolean subscribed);

    List<EventSubscription> findAllByUserId(Long userId);

    default Map<Long, Boolean> findAllByUserIdAsMap(Long userId) {
        return findAllByUserId(userId)
                .stream()
                .collect(Collectors.toMap(EventSubscription::getEventId, EventSubscription::getSubscribed));
    }
}
