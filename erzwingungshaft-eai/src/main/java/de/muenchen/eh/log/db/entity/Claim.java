package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim", schema = "eh_log")
public class Claim extends BaseEntity {

    @Column(name = "eh_uuid")
    private UUID ehUuid;

    @Column(name = "geschaeftspartner_id")
    private String geschaeftspartnerId;

    @NotEmpty
    private String kassenzeichen;

    @NotEmpty
    @Column(name = "storage_location")
    private String storageLocation;

    @NotEmpty
    @Column(name = "source_file_name")
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
