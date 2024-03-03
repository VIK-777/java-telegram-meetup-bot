package vik.telegrambots.meetupbot.dao.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import vik.telegrambots.meetupbot.dao.model.EventSubscription;

import java.util.List;
import java.util.Optional;

public interface EventSubscriptionsJpaRepository extends JpaRepository<EventSubscription, Long> {

    Optional<EventSubscription> findByEventIdAndUserId(Long eventId, Long userId);

    List<EventSubscription> findAllByEventId(Long eventId);
}
