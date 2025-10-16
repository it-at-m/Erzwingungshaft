package de.muenchen.eh.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    @Override
    public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

            if (offsetDateTime == null) {
                throw new IOException("OffsetDateTime argument is null.");
            }
            log.debug("OffsetDateTime: {}", offsetDateTime);
            jsonGenerator.writeString(offsetDateTime.format(DATE_TIME_FORMATTER));
    }
}
