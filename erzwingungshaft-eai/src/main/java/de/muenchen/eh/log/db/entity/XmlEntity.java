package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "erzwingungshaft_xml", schema = "eh_log")
public class XmlEntity extends BaseEntity {

    @NotEmpty
    @Column(name = "entry_id")
    private Integer entryId ;

    @NotEmpty
    @Column(columnDefinition = "xml")
    private String content;

}
