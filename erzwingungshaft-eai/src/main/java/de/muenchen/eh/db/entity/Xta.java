package de.muenchen.eh.db.entity;

import de.muenchen.eh.db.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "xta", schema = "eh")
public class Xta extends BaseEntity {

    @NotNull @Column(name = "claim_import_id", nullable = false)
    private Integer claimImportId;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "send_http_response_code")
    private Integer sendHttpResponseCode;

    @Column(name = "transport_message_status")
    private Integer transportMessageStatus;

}
