package vik.telegrambots.meetupbot.dao.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import vik.telegrambots.meetupbot.dao.model.Update;

import java.util.List;

public interface UpdatesJpaRepository extends JpaRepository<Update, Long> {

    List<Update> findAllByFromUser(Long userId);
}
