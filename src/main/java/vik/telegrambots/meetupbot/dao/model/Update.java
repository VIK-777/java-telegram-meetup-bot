package vik.telegrambots.meetupbot.dao.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Entity
@Table(name = "updates")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Update {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long rowId;
    Long chatId;
    Long fromUser;
    String javaClass;
    @Lob
    String updateRaw;
    @Embedded
    TechnicalFields technicalFields;
}
