package de.muenchen.eh.db.entity;

import de.muenchen.eh.db.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_import", schema = "eh")
@NoArgsConstructor
public class ClaimImport extends BaseEntity {

    public ClaimImport(Claim claim) {
        this.claim = claim;
    }

    @Column(name = "geschaeftspartner_id")
    private String geschaeftspartnerId;

    @NotEmpty private String kassenzeichen;

    @NotEmpty @Column(name = "erstell_datum")
    private String erstellDatum;

    @NotEmpty @Column(name = "storage_location")
    private String storageLocation;

    @NotEmpty @Column(name = "source_file_name")
    private String sourceFileName;

    @Column(name = "file_line_index")
    private Integer fileLineIndex;

    @Column(name = "output_directory")
    private String outputDirectory;

    @Column(name = "output_file")
    private String outputFile;

    @Column(name = "content")
    private String content;

    @Column(name = "data_import")
    private Boolean isDataImport;

    @Column(name = "antrag_import")
    private Boolean isAntragImport;

    @Column(name = "bussgeld_bescheid_import")
    private Boolean isBescheidImport;

    @Column(name = "kosten_bescheid_import")
    private Boolean isKostenBescheidImport;

    @Column(name = "verwerfung_bescheid_import")
    private Boolean isVerwerfungBescheidImport;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant lastUpdate;

    @OneToOne(mappedBy = "claimImport", cascade = CascadeType.ALL, orphanRemoval = true)
    private Claim claim;

    @PrePersist
    protected void onCreate() {
        lastUpdate = Instant.now(); // Setzt updated_at beim ersten Speichern
    }

}
