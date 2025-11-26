package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_document", schema = "eh")
public class ClaimDocument extends BaseEntity {

    @NotNull @Column(name = "claim_import_id", nullable = false)
    private Integer claimImportId;

    @NotNull @Column(name = "document_reference")
    private UUID documentReference;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type")
    private String fileTyp;

    @Column(name = "file_size")
    private Long file_size;

    @Column(name = "document_type")
    private String documentType;

    @Lob
    @Column(name = "document", columnDefinition = "BYTEA")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] document;

    @Column(name = "uploaded_on")
    private LocalDateTime uploadedOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "metadata")
    private String metadata;

    @Column(name = "awss3etag")
    private String awsS3ETag;

}
