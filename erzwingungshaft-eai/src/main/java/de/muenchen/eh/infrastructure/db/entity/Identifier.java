package de.muenchen.eh.infrastructure.db.entity;

import de.muenchen.eh.infrastructure.db.BaseEntity;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "identifier", schema = "eh")
@NoArgsConstructor
public class Identifier extends BaseEntity {

    @NotEmpty @Column(name = "geschaeftspartner_id")
    private String geschaeftspartnerId;

    @NotEmpty private String kassenzeichen;

    @NotEmpty private String identifier;

    @NotEmpty @Column(name = "coo_address_vorgang")
    private String cooAddressVorgang;

    @NotEmpty @Column(name = "coo_address_text_nr")
    private String cooAddressTextNr;

    @NotEmpty @Column(name = "input_file_name")
    private String sourceFileName;

    @Column(name = "file_line_index")
    private Integer fileLineIndex;

    @NotEmpty private String content;

    @NotEmpty @Column(name = "output_file_name")
    private String outputFileName;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant lastUpdate;

    @PrePersist
    protected void onCreate() {
        lastUpdate = Instant.now(); // Setzt updated_at beim ersten Speichern
    }

}
