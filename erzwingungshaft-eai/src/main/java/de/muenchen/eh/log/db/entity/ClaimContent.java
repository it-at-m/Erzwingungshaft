package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import de.muenchen.eh.log.db.IClaimEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_content", schema = "eh_log")
public class ClaimContent extends BaseEntity implements IClaimEntity {

    @NotEmpty @Column(name = "claim_id")
    private Integer claimId;

    @NotEmpty private String json;

}
