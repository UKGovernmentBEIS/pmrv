package uk.gov.pmrv.api.user.core.domain.dto.validation;

import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * The PhoneNumber validation that validates if country code or number are blank.
 */
public class PhoneNumberNotBlankValidator implements ConstraintValidator<PhoneNumberNotBlank, PhoneNumberDTO> {
    /** {@inheritDoc} */
    @Override
    public boolean isValid(PhoneNumberDTO phoneNumberDTO, ConstraintValidatorContext constraintValidatorContext) {
        return phoneNumberDTO != null && !ObjectUtils.isEmpty(phoneNumberDTO.getCountryCode()) && !ObjectUtils.isEmpty(phoneNumberDTO.getCountryCode().trim())
                && !ObjectUtils.isEmpty(phoneNumberDTO.getNumber()) && !ObjectUtils.isEmpty(phoneNumberDTO.getNumber().trim());
    }
}
