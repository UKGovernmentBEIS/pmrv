package uk.gov.pmrv.api.common.config.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.config.jackson.exception.InvalidStringLengthException;

public class CustomStringDeserializer extends StringDeserializer {

    private static final long serialVersionUID = 1L;
    private static final int MAX_LENGTH_ALLOWED = 30000;

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String str = super.deserialize(p, ctxt).trim();
        if (str.length() > MAX_LENGTH_ALLOWED) {
            throw new InvalidStringLengthException(p, "Invalid string length: " + str.length());
        }
        return ObjectUtils.isEmpty(str) ? null : str;
    }
    
    @Override
    public String deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer) throws IOException {
        return deserialize(p, ctxt);
    }
}
