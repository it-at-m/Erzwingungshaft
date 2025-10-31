package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import de.muenchen.eh.log.db.IClaimEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_xml", schema = "eh_log")
public class ClaimXml extends BaseEntity implements IClaimEntity {

    @NotNull
    @Column(name = "claim_id", nullable = false)
    private Integer claimId ;

    @Column(columnDefinition = "xjustiz_version")
    private String xjustizVersion;

    @NotEmpty
    @Column(columnDefinition = "xml")
    private String content;

}
