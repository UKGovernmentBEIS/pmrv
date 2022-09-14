package uk.gov.pmrv.api.account.domain.dto.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * The grid reference validation.
 */
public class GridReferenceValidator implements ConstraintValidator<GridReference, String> {

    /** Grid reference digits min length */
    private static final Integer MIN_DIGIT_LENGTH = 4;

    /** Grid reference digits max length */
    private static final Integer MAX_DIGIT_LENGTH = 10;

    /** Grid reference letters length */
    private static final Integer LETTERS_LENGTH = 2;

    /** Grid reference max length */
    private static final Integer MAX_LENGTH = 255;

    /** {@inheritDoc} */
    @Override
    public boolean isValid(String gridReference, ConstraintValidatorContext constraintValidatorContext) {
        if(!ObjectUtils.isEmpty(gridReference)) {
            int letters = gridReference.replaceAll("[^a-zA-Z]", "").length();
            int digits = gridReference.replaceAll("\\D+", "").length();
            String trimmedGridReference = StringUtils.deleteWhitespace(gridReference);
            return digits >= MIN_DIGIT_LENGTH && digits <= MAX_DIGIT_LENGTH && letters == LETTERS_LENGTH
                && gridReference.length() <= MAX_LENGTH && trimmedGridReference.length()<=letters + digits;
        }
        return true;
    }
}
