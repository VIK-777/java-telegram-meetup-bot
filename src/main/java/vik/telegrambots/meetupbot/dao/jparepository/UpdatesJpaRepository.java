package vik.telegrambots.meetupbot.dao.jparepository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vik.telegrambots.meetupbot.dao.model.Update;
import vik.telegrambots.meetupbot.dao.model.UserToInstantProjection;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
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

    @Query("SELECT new vik.telegrambots.meetupbot.dao.model.UserToInstantProjection(fromUser, MAX(technicalFields.lastUpdatedDate)) FROM Update GROUP BY fromUser")
    Stream<UserToInstantProjection> findUsersToUpdateTime();

    @Transactional
    default Map<Long, Instant> findUsersToUpdateTimeAsMap() {
        return findUsersToUpdateTime().collect(Collectors.toMap(UserToInstantProjection::userId, UserToInstantProjection::lastUpdatedDate));
    }
}
