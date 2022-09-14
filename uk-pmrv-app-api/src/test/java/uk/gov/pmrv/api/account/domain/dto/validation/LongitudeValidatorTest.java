package uk.gov.pmrv.api.account.domain.dto.validation;

import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class LongitudeValidatorTest {

    @InjectMocks
    private LongitudeValidator longitudeValidator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.LeafNodeBuilderCustomizableContext leafNodeContext;

    @BeforeEach
    public void setUp() {
        lenient().when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(builder);
        lenient().when(context.buildConstraintViolationWithTemplate(anyString()).addPropertyNode(anyString()))
                .thenReturn(nodeContext);
        lenient().when(context.buildConstraintViolationWithTemplate(anyString()).addPropertyNode(anyString()).addBeanNode())
                .thenReturn(leafNodeContext);
    }

    @Test
    void testValidLongitude() {
        CoordinatesDTO coordinatesDTO = buildMockLongitudeCoordinatesDTO();
        assertTrue(longitudeValidator.isValid(coordinatesDTO, context));
    }

    @Test
    void testInValidLongitude() {
        CoordinatesDTO coordinatesDTO = buildMockLongitudeCoordinatesDTO();

        // Cardinal Direction
        coordinatesDTO.setCardinalDirection(CardinalDirection.NORTH);
        assertFalse(longitudeValidator.isValid(coordinatesDTO, context));

        // Max Degrees
        coordinatesDTO = buildMockLongitudeCoordinatesDTO();
        coordinatesDTO.setSecond(1d);
        assertFalse(longitudeValidator.isValid(coordinatesDTO, context));

        coordinatesDTO = buildMockLongitudeCoordinatesDTO();
        coordinatesDTO.setDegree(181);
        assertFalse(longitudeValidator.isValid(coordinatesDTO, context));
    }

    private CoordinatesDTO buildMockLongitudeCoordinatesDTO() {
        return CoordinatesDTO.builder()
                .degree(180)
                .minute(0)
                .second(0d)
                .cardinalDirection(CardinalDirection.WEST).build();
    }
}
