package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import de.muenchen.eh.log.db.IClaimEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_xml", schema = "eh_log")
public class ClaimXml extends BaseEntity implements IClaimEntity {

    @NotEmpty
    @Column(name = "claim_id")
    private Integer claimId ;

    @NotEmpty
    @Column(columnDefinition = "xml")
    private String content;

}
