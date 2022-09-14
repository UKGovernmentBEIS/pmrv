package uk.gov.pmrv.api.user.core.domain.dto.validation;

import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * The PhoneNumberIntegrity validation that validates if both country code and number have any value or both are blank.
 */
public class PhoneNumberIntegrityValidator implements ConstraintValidator<PhoneNumberIntegrity, PhoneNumberDTO> {

    /** {@inheritDoc} */
    @Override
    public boolean isValid(PhoneNumberDTO phoneNumberDTO, ConstraintValidatorContext constraintValidatorContext) {
        if(phoneNumberDTO != null){
            return !(
                    (ObjectUtils.isEmpty(phoneNumberDTO.getCountryCode()) && !ObjectUtils.isEmpty(phoneNumberDTO.getNumber())) ||
                    (!ObjectUtils.isEmpty(phoneNumberDTO.getCountryCode()) && (ObjectUtils.isEmpty(phoneNumberDTO.getNumber()) || ObjectUtils.isEmpty(phoneNumberDTO.getNumber().trim())))
                    );
        }
        return true;
    }
}
