package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "erzwingungshaft_log", schema = "eh_log")
public class LogEntity extends BaseEntity {

    @NotEmpty
    @Column(name = "entry_id")
    private Integer entryId ;

    @NotEmpty
    @Column(name = "message_typ")
    @Enumerated(EnumType.STRING)
    private MessageType messageTyp;

    @NotEmpty
    private String message;

    private String comment;

}
