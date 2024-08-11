package vik.telegrambots.meetupbot.dao.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import vik.telegrambots.meetupbot.dao.model.User;

import java.util.Set;

public interface UsersJpaRepository extends JpaRepository<User, Long> {

    Set<User> findAllBySendNotifications(Boolean sendNotifications);

    Set<User> findAllBySendInfoNotifications(Boolean sendNotifications);
}
