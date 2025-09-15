package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import de.muenchen.eh.log.db.IClaimEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_document", schema = "eh_log")
public class ClaimDocumentEntity extends BaseEntity  implements IClaimEntity {

    @NotEmpty
    @Column(name = "claim_id")
    private Integer claimId;

    @NotEmpty
    @Column(name = "document_reference")
    private UUID documentReference;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type")
    private String fileTyp;

    @Column(name = "file_size")
    private Long file_size;

    @Lob
    @Column(name = "document", columnDefinition = "BYTEA")
    private byte[] document;

    @Column(name = "uploaded_on")
    private LocalDateTime uploadedOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "metadata")
    private String metadata;

}
