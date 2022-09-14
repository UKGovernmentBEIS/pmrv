package uk.gov.pmrv.api.account.domain.dto.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.dto.CoordinatesDTO;
import uk.gov.pmrv.api.account.domain.enumeration.CardinalDirection;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MaxSecondsValidatorTest {

    @InjectMocks
    private MaxSecondsValidator maxSecondsValidator;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void testValidMaxSeconds() {
        CoordinatesDTO coordinatesDTO = buildMockLongitudeCoordinatesDTO();
        assertTrue(maxSecondsValidator.isValid(coordinatesDTO, context));
    }

    @Test
    void testNull() {
        assertTrue(maxSecondsValidator.isValid(null, context));
    }

    @Test
    void testInValidMaxSeconds() {
        CoordinatesDTO coordinatesDTO = buildMockLongitudeCoordinatesDTO();
        coordinatesDTO.setMinute(60);
        coordinatesDTO.setSecond(1d);
        assertFalse(maxSecondsValidator.isValid(coordinatesDTO, context));
    }

    public static CoordinatesDTO buildMockLongitudeCoordinatesDTO() {
        return CoordinatesDTO.builder()
                .degree(180)
                .minute(0)
                .second(0d)
                .cardinalDirection(CardinalDirection.WEST).build();
    }
}
