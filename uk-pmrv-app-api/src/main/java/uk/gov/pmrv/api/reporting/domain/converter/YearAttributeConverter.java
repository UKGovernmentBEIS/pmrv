package uk.gov.pmrv.api.reporting.domain.converter;

import javax.persistence.AttributeConverter;
import java.time.Year;

public class YearAttributeConverter implements AttributeConverter<Year, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Year year) {
        return year.getValue();
    }

    @Override
    public Year convertToEntityAttribute(Integer value) {
        return Year.of(value);
    }
}
