package uk.gov.pmrv.api.user.core.domain.dto.validation;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * The country code validation.
 */
public class CountryCodeValidator implements ConstraintValidator<CountryCode, String> {

    /** The {@link PhoneNumberUtil} */
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    /** {@inheritDoc} */
    @Override
    public boolean isValid(String countryCode, ConstraintValidatorContext constraintValidatorContext) {
        if(!ObjectUtils.isEmpty(countryCode)){
            if(!countryCode.matches("^[0-9]+$")){
                return false;
            }
            else return !phoneUtil.getRegionCodesForCountryCode(Integer.parseInt(countryCode)).isEmpty();
        }
        return true;
    }
}
