package de.muenchen.eh.infrastructure.db.entity;

import de.muenchen.eh.infrastructure.db.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class EfileIdentifier extends BaseEntity {

    public EfileIdentifier(String geschaeftspartnerId, String kassenzeichen, String identifier, String fileCollectionCooAddress, String fileCooAddress,
            String fineCooAddress, String fineNameCooAddress, String sourceFileName, Integer fileLineIndex, String content, String outputFileName,
            MessageType messageType, String message, String comment) {
        this.geschaeftspartnerId = geschaeftspartnerId;
        setKassenzeichen(this.kassenzeichen);
        this.identifier = identifier;
        this.fileCollectionCooAddress = fileCollectionCooAddress;
        this.fileCooAddress = fileCooAddress;
        this.fineCooAddress = fineCooAddress;
        this.fineNameCooAddress = fineNameCooAddress;
        this.sourceFileName = sourceFileName;
        this.fileLineIndex = fileLineIndex;
        this.content = content;
        this.outputFileName = outputFileName;
        this.messageType = messageType;
        this.message = message;
        this.comment = comment;
    }

    @NotEmpty @Column(name = "geschaeftspartner_id")
    private String geschaeftspartnerId;

    @NotEmpty private String kassenzeichen;

    @Column(name = "kassenzeichen_efile")
    @NotEmpty private String kassenzeichenEfile;

    private String identifier;

    @Column(name = "file_collection_coo_address")
    private String fileCollectionCooAddress;

    @Column(name = "file_coo_address")
    private String fileCooAddress;

    @Column(name = "fine_coo_address")
    private String fineCooAddress;

    @Column(name = "fine_name_coo_address")
    private String fineNameCooAddress;

    @Column(name = "input_file_name")
    private String sourceFileName;

    @Column(name = "file_line_index")
    private Integer fileLineIndex = -1;

    @NotEmpty private String content;

    @Column(name = "output_file_name")
    private String outputFileName;

    @NotNull @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.ERROR;

    @NotBlank private String message = "";

    private String comment = "";

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant lastUpdate;

    @PrePersist
    protected void onCreate() {
        lastUpdate = Instant.now(); // Setzt updated_at beim ersten Speichern
    }

    public void setKassenzeichen(String kassenzeichen) {
        this.kassenzeichen = kassenzeichen;
        this.kassenzeichenEfile = (kassenzeichen == null) ? null : "5".concat(kassenzeichen);
    }

}
