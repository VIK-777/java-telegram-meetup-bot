package vik.telegrambots.meetupbot.dao.jparepository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import vik.telegrambots.meetupbot.dao.model.Update;

import java.time.Instant;
import java.util.Comparator;
import java.util.stream.Stream;

public interface UpdatesJpaRepository extends JpaRepository<Update, Long> {

    Stream<Update> findAllByFromUser(Long userId);

    @Transactional
    default Instant findLatestUpdateTimeFromUser(Long userId) {
        return findAllByFromUser(userId)
                .max(Comparator.comparing(Update::getRowId))
                .map(update -> update.getTechnicalFields().getLastUpdatedDate())
                .orElse(Instant.EPOCH);
    }
}
