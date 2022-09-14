package uk.gov.pmrv.api.common.domain.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Converts json of type {@link ZonedDateTime} to {@link LocalDateTime} representation.
 *
 * @see JsonDeserializer
 */
public class LocalDateTimeDeserializerConverter extends JsonDeserializer<LocalDateTime> {

    /** {@inheritDoc} */
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return ObjectUtils.isEmpty(jsonParser.getText()) ? null : ZonedDateTime.parse(jsonParser.getText()).toLocalDateTime();
    }
}
