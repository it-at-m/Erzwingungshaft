package de.muenchen.eh.log.db.entity;

import de.muenchen.eh.log.db.BaseEntity;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "claim_import", schema = "eh_log")
public class ClaimImport extends BaseEntity {

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

    @Column(name = "output_directory")
    private String outputDirectory;

    @Column(name = "output_file")
    private String outputFile;

    @Column(name = "content")
    private String content;

    @Column(name = "data_import")
    private Boolean isDataImport;

    @Column(name = "antrag_import")
    private Boolean isAntragImport;

    @Column(name = "bescheid_import")
    private Boolean isBescheidImport;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "claim_import_id")
    private ClaimEntity claim;

}
