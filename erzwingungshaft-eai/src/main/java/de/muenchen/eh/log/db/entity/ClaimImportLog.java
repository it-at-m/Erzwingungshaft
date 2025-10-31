package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_import_log", schema = "eh_log")
public class ClaimImportLog extends BaseEntity {

    @NotNull
    @Column(name = "claim_import_id")
    private Integer claimImportId ;

    @NotNull
    @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @NotBlank
    private String message;

    private String comment;

}
