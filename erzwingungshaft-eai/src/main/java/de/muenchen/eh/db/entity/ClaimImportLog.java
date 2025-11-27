package de.muenchen.eh.db.entity;

import de.muenchen.eh.db.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_import_log", schema = "eh")
public class ClaimImportLog extends BaseEntity {

    @NotNull @Column(name = "claim_import_id")
    private Integer claimImportId;

    @NotNull @Column(name = "message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @NotBlank private String message;

    private String comment;

}
