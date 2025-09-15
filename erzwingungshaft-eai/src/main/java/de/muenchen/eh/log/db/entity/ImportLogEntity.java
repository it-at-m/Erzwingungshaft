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
@Table(name = "import_log", schema = "eh_log")
public class ImportLogEntity extends BaseEntity {

    @NotEmpty
    @Column(name = "import_id")
    private Integer importId ;

    @NotEmpty
    @Column(name = "message_typ")
    @Enumerated(EnumType.STRING)
    private MessageType messageTyp;

    @NotEmpty
    private String message;

    private String comment;

}
