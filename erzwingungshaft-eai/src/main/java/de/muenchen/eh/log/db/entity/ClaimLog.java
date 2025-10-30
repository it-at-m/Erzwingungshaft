package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import de.muenchen.eh.log.db.IClaimEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_log", schema = "eh_log")
public class ClaimLog extends BaseEntity implements IClaimEntity {

    @NotEmpty @Column(name = "claim_id")
    private Integer claimId;

    @NotEmpty @Column(name = "message_typ")
    @Enumerated(EnumType.STRING)
    private MessageType messageTyp;

    @NotEmpty private String message;

    private String comment;

}
