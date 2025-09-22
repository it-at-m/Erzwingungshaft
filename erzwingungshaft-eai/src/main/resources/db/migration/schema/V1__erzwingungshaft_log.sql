CREATE SCHEMA IF NOT EXISTS eh_log;

CREATE TABLE eh_log.claim_import
(

    id                   SERIAL PRIMARY KEY,    -- INT PK
    geschaeftspartner_id VARCHAR(10),
    kassenzeichen        VARCHAR(20),
    storage_location     TEXT         NOT NULL,
    source_file_name     VARCHAR(100) NOT NULL,
    file_line_index      INTEGER,
    content              TEXT,
    output_directory     TEXT         NOT NULL, -- Directory name created for each case imported.
    output_file          TEXT         NOT NULL, -- File name created for each case imported.
    data_import          BOOLEAN DEFAULT FALSE, -- Data file is created in directory
    antrag_import        BOOLEAN DEFAULT FALSE, -- Antrag is imported and assigned to directory
    bescheid_import      BOOLEAN DEFAULT FALSE, -- Bescheid is imported and assigned to directory
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE INDEX idx_import_geschaeftspartner_kassenzeichen ON eh_log.claim_import (geschaeftspartner_id, kassenzeichen);

CREATE TABLE eh_log.claim_import_log
(

    id          SERIAL PRIMARY KEY, -- INT PK
    claim_import_id   INTEGER NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message_typ TEXT    NOT NULL,
    message     TEXT    NOT NULL,
    comment     TEXT      DEFAULT '',

    CONSTRAINT fk_claim_import FOREIGN KEY (claim_import_id) REFERENCES eh_log.claim_import (id) ON DELETE CASCADE

);

CREATE INDEX idx_import_log_claim_import_id ON eh_log.claim_import_log (claim_import_id);

CREATE TABLE eh_log.claim_document
(
    id                 SERIAL PRIMARY KEY, -- INT PK
    claim_import_id    INTEGER      NOT NULL,
    document_reference UUID,
    document_type      TEXT  NOT NULL,
    file_name          VARCHAR(255) NOT NULL,
    file_type          VARCHAR(50)  NOT NULL,
    file_size          BIGINT,
    document           BYTEA,
    uploaded_on        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_on         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    metadata           TEXT,               -- claim entity as json
    awss3etag          TEXT,                -- The hex encoded 128-bit MD5 digest of the associated object according to RFC 1864. This data is used as an integrity check to verify that the data received by the caller is the same data that was sent by Amazon S3.

    CONSTRAINT fk_claim_import FOREIGN KEY (claim_import_id) REFERENCES eh_log.claim_import (id) ON DELETE CASCADE

);
CREATE INDEX idx_claim_import_document_claim_id ON eh_log.claim_document (claim_import_id);
CREATE INDEX idx_claim_import_document_document_reference ON eh_log.claim_document (document_reference);

CREATE TABLE eh_log.claim
(

    id                   SERIAL PRIMARY KEY, -- INT PK
    claim_import_id      INTEGER      NOT NULL,
    eh_uuid              UUID,               -- Assigned during XML creation
    geschaeftspartner_id VARCHAR(10),
    kassenzeichen        VARCHAR(20),
    storage_location     TEXT         NOT NULL,
    source_file_name     VARCHAR(100) NOT NULL,
    file_line_index      INTEGER,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_claim_import FOREIGN KEY (claim_import_id) REFERENCES eh_log.claim_import (id) ON DELETE CASCADE

);
CREATE INDEX idx_claim_claim_import_id ON eh_log.claim (claim_import_id);
CREATE INDEX idx_claim_geschaeftspartner_kassenzeichen ON eh_log.claim (geschaeftspartner_id, kassenzeichen);

CREATE TABLE eh_log.claim_log
(

    id          SERIAL PRIMARY KEY, -- INT PK
    claim_id    INTEGER NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message_typ TEXT    NOT NULL,
    message     TEXT    NOT NULL,
    comment     TEXT      DEFAULT '',

    CONSTRAINT fk_claim FOREIGN KEY (claim_id) REFERENCES eh_log.claim (id) ON DELETE CASCADE

);

CREATE INDEX idx_claim_log_claim_id ON eh_log.claim_log (claim_id);

CREATE TABLE eh_log.claim_xml
(
    id         SERIAL PRIMARY KEY, -- INT PK
    claim_id   INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    content    TEXT,

    CONSTRAINT fk_claim FOREIGN KEY (claim_id) REFERENCES eh_log.claim (id) ON DELETE CASCADE

);

CREATE INDEX idx_claim_xml_claim_id ON eh_log.claim_xml (claim_id);

CREATE TABLE eh_log.claim_content
(
    id         SERIAL PRIMARY KEY, -- INT PK
    claim_id   INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    json       TEXT,

    CONSTRAINT fk_claim FOREIGN KEY (claim_id) REFERENCES eh_log.claim (id) ON DELETE CASCADE

);

CREATE INDEX idx_claim_content_claim_id ON eh_log.claim_content (claim_id);

CREATE TABLE eh_log.claim_data
(

    id              SERIAL PRIMARY KEY, -- INT PK
    claim_id        INTEGER NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ehsatzlaenge    VARCHAR(4),
    ehowigb         VARCHAR(1),
    ehowirga        VARCHAR(3),
    ehowiblank1     VARCHAR(1),
    ehowiowi        VARCHAR(3),
    ehowiblank2     VARCHAR(1),
    ehowilfnr       VARCHAR(5),
    ehowischr       VARCHAR(1),
    ehowijahr       VARCHAR(2),
    ehowirichter    VARCHAR(30),
    ehowititel      VARCHAR(24),
    ehkennz         VARCHAR(10),
    ehfzart         VARCHAR(70),
    ehfzmarke       VARCHAR(20),
    ehfzfarbe       VARCHAR(30),
    ehnation        VARCHAR(47),
    ehnatmverf      VARCHAR(1),
    ehtatort        VARCHAR(26),
    ehtatstr1       VARCHAR(25),
    ehtathnr1       VARCHAR(4),
    ehtatstr2       VARCHAR(25),
    ehtathnr2       VARCHAR(4),
    ehtatkzgeg      VARCHAR(1),
    ehtatpuhr       VARCHAR(1),
    ehtatlima       VARCHAR(1),
    ehtatlimanr     VARCHAR(5),
    ehtatdatv       VARCHAR(10),
    ehtatdatb       VARCHAR(10),
    ehtatstdv       VARCHAR(2),
    ehtatminv       VARCHAR(2),
    ehtatstdb       VARCHAR(2),
    ehtatminb       VARCHAR(2),
    ehbusskz1       VARCHAR(5),
    ehbusskz1txtt1  VARCHAR(54),
    ehbusskz1txtt2  VARCHAR(54),
    ehbusskz1txtt3  VARCHAR(54),
    ehbusskz2       VARCHAR(5),
    ehbusskz2txtt1  VARCHAR(54),
    ehbusskz2txtt2  VARCHAR(54),
    ehbusskz2txtt3  VARCHAR(54),
    ehtbsl          VARCHAR(3),
    ehtbsltxtt1     VARCHAR(61),
    ehtbsltxtt2     VARCHAR(61),
    ehtbsltxtt3     VARCHAR(61),
    ehtatbtxtt1     VARCHAR(70),
    ehtatbtxtt2     VARCHAR(70),
    ehtatbtxtt3     VARCHAR(70),
    ehzeuge         VARCHAR(59),
    ehverwbetrag    VARCHAR(6),
    ehkassz         VARCHAR(20),
    ehsollstbetrag  VARCHAR(6),
    ehsollausstxt1  VARCHAR(30),
    ehsollstgeb     VARCHAR(6),
    ehsollausstxt2  VARCHAR(30),
    ehsollstausl    VARCHAR(6),
    ehsollstgesamt  VARCHAR(6),
    ehauslkasta     VARCHAR(6),
    ehauslag        VARCHAR(6),
    ehgesbetr1      VARCHAR(6),
    ehgesbetr2      VARCHAR(6),
    ehazpol         VARCHAR(20),
    ehbeschart      VARCHAR(34),
    ehbeschanord    VARCHAR(65),
    ehanhdat        VARCHAR(20),
    ehanzdat        VARCHAR(20),
    ehmassn         VARCHAR(13),
    ehstatustxt     VARCHAR(65),
    ehgrund         VARCHAR(2),
    ehgrundtxtt1    VARCHAR(55),
    ehgrundtxtt2    VARCHAR(55),
    ehgrundtxtt3    VARCHAR(55),
    ehgrundtxtt4    VARCHAR(55),
    ehpzudat        VARCHAR(10),
    ehpzuart        VARCHAR(1),
    ehpzupagnr      VARCHAR(11),
    ehp1beschuldart VARCHAR(35),
    ehp1anrede      VARCHAR(15),
    ehp1schluessel  VARCHAR(1),
    ehp1name        VARCHAR(45),
    ehp1vorname     VARCHAR(40),
    ehp1nambest     VARCHAR(45),
    ehp1gebname     VARCHAR(45),
    ehp1gebnambest  VARCHAR(45),
    ehp1gebort      VARCHAR(40),
    ehp1gebdat      VARCHAR(10),
    ehp1geschl      VARCHAR(1),
    ehp1akad        VARCHAR(25),
    ehp1land        VARCHAR(3),
    ehp1plz         VARCHAR(7),
    ehp1ort         VARCHAR(26),
    ehp1strasse     VARCHAR(25),
    ehp1hausnr      VARCHAR(4),
    ehp1buchst      VARCHAR(2),
    ehp1zusatz      VARCHAR(7),
    ehp1teilnr      VARCHAR(5),
    ehp1postf       VARCHAR(10),
    ehp1whgeber     VARCHAR(26),
    ehp2art         VARCHAR(35),
    ehp2anrede      VARCHAR(15),
    ehp2perssl      VARCHAR(1),
    ehp2name        VARCHAR(45),
    ehp2vorname     VARCHAR(40),
    ehp2nambest     VARCHAR(45),
    ehp2gebname     VARCHAR(45),
    ehp2gebnambest  VARCHAR(45),
    ehp2gebort      VARCHAR(40),
    ehp2gebdat      VARCHAR(10),
    ehp2geschl      VARCHAR(1),
    ehp2akad        VARCHAR(25),
    ehp2land        VARCHAR(3),
    ehp2plz         VARCHAR(7),
    ehp2ort         VARCHAR(26),
    ehp2strasse     VARCHAR(25),
    ehp2hausnr      VARCHAR(4),
    ehp2buchst      VARCHAR(2),
    ehp2zusatz      VARCHAR(7),
    ehp2teilnr      VARCHAR(5),
    ehp2postf       VARCHAR(10),
    ehp2whgeber     VARCHAR(26),
    ehp3art         VARCHAR(35),
    ehp3anrede      VARCHAR(15),
    ehp3perssl      VARCHAR(1),
    ehp3name        VARCHAR(45),
    ehp3vorname     VARCHAR(40),
    ehp3nambest     VARCHAR(45),
    ehp3gebname     VARCHAR(45),
    ehp3gebnambest  VARCHAR(45),
    ehp3gebort      VARCHAR(40),
    ehp3gebdat      VARCHAR(10),
    ehp3geschl      VARCHAR(1),
    ehp3akad        VARCHAR(25),
    ehp3land        VARCHAR(3),
    ehp3plz         VARCHAR(7),
    ehp3ort         VARCHAR(26),
    ehp3strasse     VARCHAR(25),
    ehp3hausnr      VARCHAR(4),
    ehp3buchst      VARCHAR(2),
    ehp3zusatz      VARCHAR(7),
    ehp3teilnr      VARCHAR(5),
    ehp3postf       VARCHAR(10),
    ehp3whgeber     VARCHAR(26),
    ehbdat          VARCHAR(10),
    ehbzustdat      VARCHAR(10),
    ehbzustst       VARCHAR(1),
    ehbrkdat        VARCHAR(10),
    ehbrbehdat      VARCHAR(10),
    ehbwean         VARCHAR(10),
    ehbwegew        VARCHAR(10),
    ehbabju         VARCHAR(10),
    ehbruju         VARCHAR(10),
    ehbabslju       VARCHAR(10),
    ehb2dat         VARCHAR(10),
    ehb2zustdat     VARCHAR(10),
    ehb2zustst      VARCHAR(1),
    ehb2rkdat       VARCHAR(10),
    ehb2einspr      VARCHAR(10),
    ehb2wean        VARCHAR(10),
    ehb2wegew       VARCHAR(10),
    ehb2abju        VARCHAR(10),
    ehb2ruju        VARCHAR(10),
    ehb2abslju      VARCHAR(10),
    ehb3dat         VARCHAR(10),
    ehb3zustdat     VARCHAR(10),
    ehb3zustst      VARCHAR(1),
    ehb3rkdat       VARCHAR(10),
    ehb3einspr      VARCHAR(10),
    ehb3wean        VARCHAR(10),
    ehb3wegew       VARCHAR(10),
    ehb3abju        VARCHAR(10),
    ehb3ruju        VARCHAR(10),
    ehb3abslju      VARCHAR(10),
    ehgeschwmess    VARCHAR(3),
    ehgeschwzul     VARCHAR(3),
    ehmesstol       VARCHAR(3),
    ehgeschwmin     VARCHAR(3),
    ehgeschwueb     VARCHAR(3),
    ehfilmnr        VARCHAR(4),
    ehfilmdat       VARCHAR(10),
    ehfilmbildpos   VARCHAR(10),
    ehbewfoto       VARCHAR(1),
    ehbewfaschr     VARCHAR(1),
    ehbewlischr     VARCHAR(1),
    ehbewfrontf     VARCHAR(1),
    ehbewradar      VARCHAR(1),
    ehbewgutacht    VARCHAR(1),
    ehbewangbetr    VARCHAR(1),
    ehpunkte        VARCHAR(1),
    ehfahrverbot    VARCHAR(1),
    ehsbname        VARCHAR(25),
    ehsbzimmer      VARCHAR(10),
    ehsbtel         VARCHAR(10),
    ehlinefeed      VARCHAR(3),
    ehgpid          VARCHAR(10),

    CONSTRAINT fk_claim FOREIGN KEY (claim_id) REFERENCES eh_log.claim (id) ON DELETE CASCADE

);

CREATE INDEX idx_claim_data_claim_id ON eh_log.claim_data (claim_id);