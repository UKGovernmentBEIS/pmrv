package uk.gov.pmrv.api.common.domain.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Converts {@link LocalDateTime} to proper json representation.
 *
 * @see JsonSerializer
 */
public class LocalDateTimeSerializerConverter extends JsonSerializer<LocalDateTime> {

    /** {@inheritDoc} */
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(localDateTime.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}
