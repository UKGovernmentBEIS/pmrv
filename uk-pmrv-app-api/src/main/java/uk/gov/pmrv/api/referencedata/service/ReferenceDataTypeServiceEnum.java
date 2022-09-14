package uk.gov.pmrv.api.referencedata.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.referencedata.domain.ReferenceData;
import uk.gov.pmrv.api.referencedata.domain.dto.ReferenceDataDTO;
import uk.gov.pmrv.api.referencedata.domain.enumeration.ReferenceDataType;
import uk.gov.pmrv.api.referencedata.transform.CountryMapper;
import uk.gov.pmrv.api.referencedata.transform.ReferenceDataMapper;

import java.util.Arrays;

/**
 * Enum that relates a reference data type with a reference data service and a dto mapper
 *
 */
@AllArgsConstructor
@Getter
public enum ReferenceDataTypeServiceEnum {
	
	COUNTRY(ReferenceDataType.COUNTRIES, CountryService.class, Mappers.getMapper(CountryMapper.class))
	//add more reference data types
	;
	
	private final ReferenceDataType referenceDataType;
	private final Class<? extends ReferenceDataService<? extends ReferenceData>> referenceDataService;
	private final ReferenceDataMapper<? extends ReferenceData, ? extends ReferenceDataDTO> referenceDataMapper;
	
	/**
	 * Resolve the reference data type service enum for the given reference data type
	 * @param referenceDataType
	 * @return
	 */
	public static ReferenceDataTypeServiceEnum resolve(ReferenceDataType referenceDataType) {
		return Arrays.stream(values())
				.filter(e -> e.getReferenceDataType() == referenceDataType)
				.findFirst()
				.orElse(null);
	}

}
