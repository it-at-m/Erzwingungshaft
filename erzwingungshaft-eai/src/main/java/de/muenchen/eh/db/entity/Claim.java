package de.muenchen.eh.db.entity;

import de.muenchen.eh.db.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim", schema = "eh")
public class Claim extends BaseEntity {

    @Column(name = "eh_uuid")
    private UUID ehUuid;

    @NotEmpty @Column(name = "storage_location")
    private String storageLocation;

    @NotEmpty @Column(name = "source_file_name")
    private String sourceFileName;

    @Column(name = "file_line_index")
    private Integer fileLineIndex;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant lastUpdate;

    @OneToOne(optional = true)
    @JoinColumn(name = "claim_import_id", unique = true)
    private ClaimImport claimImport;

    @PrePersist
    protected void onCreate() {
        lastUpdate = Instant.now();
    }

    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClaimEfile claimEfile;
}
