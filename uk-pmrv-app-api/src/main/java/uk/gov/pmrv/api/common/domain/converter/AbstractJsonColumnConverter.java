package uk.gov.pmrv.api.common.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Converts object of type T to json string and vice versa
 *
 * @param <T> the object type
 */
@Log4j2
public abstract class AbstractJsonColumnConverter<T> implements AttributeConverter<T, String> {

    private final ObjectMapper mapper;

    private Type type;

	protected AbstractJsonColumnConverter(ObjectMapper mapper) {
		this.mapper = mapper;
		initType();
	}
	
	protected void initType() {
		ParameterizedType paramType = (ParameterizedType) getClass().getGenericSuperclass();
		type = paramType.getActualTypeArguments()[0];
	}
	
    @Override
    public String convertToDatabaseColumn(T o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Exception during JSON serialization:", e);
            return null;
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData != null) {
            try {
                return mapper.readValue(dbData, mapper.getTypeFactory().constructType(type));
            } catch (JsonProcessingException e) {
                log.error("Exception during JSON deserialization:", e);
                return null;
            }
        }

        return null;
    }

}
