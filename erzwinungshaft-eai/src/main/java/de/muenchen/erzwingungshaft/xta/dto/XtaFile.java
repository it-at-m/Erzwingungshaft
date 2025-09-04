package de.muenchen.erzwingungshaft.xta.dto;

import com.google.common.io.CountingOutputStream;
import jakarta.activation.DataHandler;

import java.math.BigInteger;

public record XtaFile(
        String id,
        DataHandler content,
        String lang,
        String name,
        String contentType,
        String description,
        BigInteger size
) {

}
