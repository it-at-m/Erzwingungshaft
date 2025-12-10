package de.muenchen.eh.db.entity;

import de.muenchen.eh.db.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "claim_efile", schema = "eh")
public class ClaimEfile extends BaseEntity {

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

    private String kostendokument;

    private String verwerfung;

    @PrePersist
    protected void onCreate() {
        lastUpdate = Instant.now();
    }

    @OneToOne(optional = true)
    @JoinColumn(name = "claim_id", unique = true)
    private Claim claim;

}
