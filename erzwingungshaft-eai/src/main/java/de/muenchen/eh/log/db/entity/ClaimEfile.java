package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_efile", schema = "eh_log")
public class ClaimEfile extends BaseEntity {

    @NotEmpty
    @Column(name = "claim_id")
    private Integer claimId ;

    private String collection ;

    @Column(name = "case_file")
    private String caseFile ;

    private String fine;

    @Column(name = "antrag_document")
    private String antragDocument;

    @Column(name = "bescheid_document")
    private String bescheidDocument;

}
