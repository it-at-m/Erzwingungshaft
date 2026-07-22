CREATE TABLE eh.identifier
(

    id                          SERIAL PRIMARY KEY, -- INT PK
    geschaeftspartner_id        VARCHAR(10),
    kassenzeichen               VARCHAR(20),
    kassenzeichen_efile         VARCHAR(20),
    identifier                  TEXT,  -- <kassenzeichen>-SKA<basenr><coo_address_text>
    file_collection_coo_address TEXT,
    file_coo_address            TEXT,
    fine_coo_address            TEXT,
    fine_name_coo_address       TEXT,  -- Bsp. Bußgeldverfahren (9123.0-1-0002)
    input_file_name             VARCHAR(100),
    file_line_index             INTEGER,
    content                     TEXT,
    output_file_name            VARCHAR(100), -- File name for new file contain new generated identifier
    message_type                TEXT,
    message                     TEXT,
    comment                     TEXT,
    created_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ NOT NULL

);

CREATE INDEX idx_identifier_geschaeftspartner_kassenzeichen ON eh.identifier (geschaeftspartner_id, kassenzeichen);