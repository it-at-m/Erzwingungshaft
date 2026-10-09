ALTER TABLE eh.xta
    ADD COLUMN eh_antrag_laufzettel_eingangs_datum DATE,  -- Fuer die Rueckantwort, nicht relevant fuer den Versand
    ADD COLUMN eh_antrag_laufzettel_eakte_status INTEGER; -- Fuer die Rueckantwort, nicht relevant fuer den Versand