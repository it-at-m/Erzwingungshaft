package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "erzwingungshaft_content", schema = "eh_log")
public class ContentEntity extends BaseEntity {

    @NotEmpty
    @Column(name = "entry_id")
    private Integer entryId ;

    @NotEmpty
    private String json;

}
