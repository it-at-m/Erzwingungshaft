package de.muenchen.eh.db.entity;

import de.muenchen.eh.db.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "unassignable_error", schema = "eh")
public class UnassignableError extends BaseEntity {

    @NotEmpty private String message;

    private String comment;

}
