package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "erzwingungshaft_entry", schema = "eh_log")
public class EntryEntity extends BaseEntity {

    @Column(name = "eh_uuid")
    private UUID ehUuid;

    @Column(name = "geschaeftspartner_id")
    private String geschaeftspartnerId;

    @NotEmpty
    private String kassenzeichen;

    @NotEmpty
    @Column(name = "storage_location")
    private String storageLocation;

    @NotEmpty
    @Column(name = "source_file_name")
    private String sourceFileName;

    @Column(name = "file_line_index")
    private Integer fileLineIndex;

    @Override
    public Integer getEntryId() {
        return getId();
    }

    @Override
    public void setEntryId(Integer entryId) {
        this.setId(entryId);
    }
}
