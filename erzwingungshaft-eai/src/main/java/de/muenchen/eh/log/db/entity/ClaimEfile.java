package de.muenchen.eh.log.db.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "claim_efile", schema = "eh_log")
public class ClaimEfile {

    @Id
    @Column(name = "geschaeftspartner_id")
    private String geschaeftspartnerId;

    private String collection ;

    private String file;

    private String fine;

    private String outgoing;

    @Column(name = "antrag_document")
    private String antragDocument;

    @Column(name = "bescheid_document")
    private String bescheidDocument;

}
