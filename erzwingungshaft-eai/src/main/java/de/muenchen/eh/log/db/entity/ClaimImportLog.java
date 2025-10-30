package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_import_log", schema = "eh_log")
public class ClaimImportLog extends BaseEntity {

    @NotEmpty @Column(name = "claim_import_id")
    private Integer claimImportId;

    @NotEmpty @Column(name = "message_typ")
    @Enumerated(EnumType.STRING)
    private MessageType messageTyp;

    @NotEmpty private String message;

    private String comment;

}
