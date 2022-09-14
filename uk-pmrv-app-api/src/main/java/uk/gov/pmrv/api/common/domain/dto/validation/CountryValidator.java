package uk.gov.pmrv.api.common.domain.dto.validation;

import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.referencedata.service.CountryService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * The country validator (validates against the country code (e.g. GR))
 *
 */
public class CountryValidator implements ConstraintValidator<Country, String> {

	private final CountryService countryService;

	public CountryValidator(CountryService countryService) {
		this.countryService = countryService;
	}

	@Override
	public boolean isValid(String countryCode, ConstraintValidatorContext constraintValidatorContext) {
		if (!ObjectUtils.isEmpty(countryCode)) {
			return countryService.getReferenceData().stream().anyMatch(country -> country.getCode().equals(countryCode));
		}
		return true;
	}
}
