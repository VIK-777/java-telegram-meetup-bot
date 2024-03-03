package vik.telegrambots.meetupbot.dao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalFields implements Serializable {
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdDate;
    @Setter
    @UpdateTimestamp
    private Instant lastUpdatedDate;
}
