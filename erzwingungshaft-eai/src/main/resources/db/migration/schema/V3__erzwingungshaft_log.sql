CREATE TABLE eh.identifier
(

    id          SERIAL PRIMARY KEY, -- INT PK
    geschaeftspartner_id VARCHAR(10),
    kassenzeichen        VARCHAR(20),
    identifier           TEXT,  -- <kassenzeichen>-SKA<basenr><coo_address_text>
    coo_address_vorgang  TEXT,
    coo_address_text_nr  TEXT,  -- <nr> aus : Bußgeldverfahren (<nr>), Bsp. Bußgeldverfahren (9123.0-1-0002)
    input_file_name      VARCHAR(100) NOT NULL,
    file_line_index      INTEGER,
    content              TEXT,
    output_file_name     VARCHAR(100) NOT NULL, -- File name for new file contain new generated identifier
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMPTZ NOT NULL

);

CREATE INDEX idx_identifier_geschaeftspartner_kassenzeichen ON eh.identifier (geschaeftspartner_id, kassenzeichen);