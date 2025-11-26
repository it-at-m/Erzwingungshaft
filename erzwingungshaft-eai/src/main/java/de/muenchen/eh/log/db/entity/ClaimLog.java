package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import de.muenchen.eh.log.db.IClaimEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_log", schema = "eh")
public class ClaimLog extends BaseEntity implements IClaimEntity {

    @NotNull @Column(name = "claim_id", nullable = false)
    private Integer claimId;

    @NotNull @Column(name = "message_typ")
    @Enumerated(EnumType.STRING)
    private MessageType messageTyp;

    @NotEmpty private String message;

    private String comment;

}
