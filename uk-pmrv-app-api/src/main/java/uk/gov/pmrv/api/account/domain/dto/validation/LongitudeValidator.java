package uk.gov.pmrv.api.account.domain.dto.validation;

import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.account.domain.dto.CoordinatesDTO;
import uk.gov.pmrv.api.account.domain.enumeration.CardinalDirection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * The longitude validation.
 */
public class LongitudeValidator implements ConstraintValidator<Longitude, CoordinatesDTO> {

    /** Max Longitude degree */
    private static final Integer MAX_DEGREE = 180;

    /** {@inheritDoc} */
    @Override
    public boolean isValid(CoordinatesDTO coordinatesDTO, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        if(ObjectUtils.isEmpty(coordinatesDTO) || ObjectUtils.isEmpty(coordinatesDTO.getDegree()) || ObjectUtils.isEmpty(coordinatesDTO.getMinute()) ||
                ObjectUtils.isEmpty(coordinatesDTO.getSecond()) || ObjectUtils.isEmpty(coordinatesDTO.getCardinalDirection())){
            // Let annotations validation to confront the error
            return true;
        }
        else if(!coordinatesDTO.getCardinalDirection().equals(CardinalDirection.WEST)
                && !coordinatesDTO.getCardinalDirection().equals(CardinalDirection.EAST)){
            constraintValidatorContext.buildConstraintViolationWithTemplate("{coordinates.cardinalDirection.typeMismatch}")
                    .addPropertyNode("cardinalDirection").addBeanNode().addConstraintViolation();
            return false;
        }
        else if(coordinatesDTO.getDegree() > MAX_DEGREE ||
                (coordinatesDTO.getDegree().equals(MAX_DEGREE) && (coordinatesDTO.getMinute() > 0 || coordinatesDTO.getSecond() > 0))){
            constraintValidatorContext.buildConstraintViolationWithTemplate("{coordinates.degree.typeMismatch}")
                    .addPropertyNode("degree").addBeanNode().addConstraintViolation();
            return false;
        }

        return true;
    }
}
