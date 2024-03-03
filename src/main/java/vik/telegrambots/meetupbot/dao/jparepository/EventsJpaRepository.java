package vik.telegrambots.meetupbot.dao.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import vik.telegrambots.meetupbot.dao.model.Event;

import java.time.Instant;
import java.util.List;

public interface EventsJpaRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByEventTimeGreaterThanOrderByEventTimeAsc(Instant time);

    default List<Event> findUpcomingEvents() {
        return findAllByEventTimeGreaterThanOrderByEventTimeAsc(Instant.now());
    }
}
