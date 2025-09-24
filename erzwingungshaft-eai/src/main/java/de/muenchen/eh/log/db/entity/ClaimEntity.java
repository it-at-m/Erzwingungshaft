package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim", schema = "eh_log")
public class ClaimEntity extends BaseEntity {

    @Column(name = "eh_uuid")
    private UUID ehUuid;

    @NotEmpty
    @Column(name = "claim_import_id")
    private Integer claimImportId;

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

}
