package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Entity
@Table(name = "claim_efile", schema = "eh_log")
public class ClaimEfile extends BaseEntity {

    @NotEmpty @Column(name = "claim_id")
    private Integer claimId;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant lastUpdate;

    private String collection;

    private String file;

    private String fine;

    private String outgoing;

    @Column(name = "antrag_document")
    private String antragDocument;

    @Column(name = "bescheid_document")
    private String bescheidDocument;

    @PrePersist
    protected void onCreate() {
        lastUpdate = Instant.now();
    }
}
