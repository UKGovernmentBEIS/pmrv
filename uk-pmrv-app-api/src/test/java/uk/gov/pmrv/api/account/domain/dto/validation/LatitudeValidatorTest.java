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
class LatitudeValidatorTest {

    @InjectMocks
    private LatitudeValidator latitudeValidator;

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
    void testValidLatitude() {
        CoordinatesDTO coordinatesDTO = buildMockLatitudeCoordinatesDTO();
        assertTrue(latitudeValidator.isValid(coordinatesDTO, context));
    }

    @Test
    void testInValidLatitude() {
        CoordinatesDTO coordinatesDTO = buildMockLatitudeCoordinatesDTO();

        // Cardinal Direction
        coordinatesDTO.setCardinalDirection(CardinalDirection.WEST);
        assertFalse(latitudeValidator.isValid(coordinatesDTO, context));

        // Max Degrees
        coordinatesDTO = buildMockLatitudeCoordinatesDTO();
        coordinatesDTO.setSecond(1d);
        assertFalse(latitudeValidator.isValid(coordinatesDTO, context));

        coordinatesDTO = buildMockLatitudeCoordinatesDTO();
        coordinatesDTO.setDegree(91);
        assertFalse(latitudeValidator.isValid(coordinatesDTO, context));
    }

    private CoordinatesDTO buildMockLatitudeCoordinatesDTO() {
        return CoordinatesDTO.builder()
                .degree(90)
                .minute(0)
                .second(0d)
                .cardinalDirection(CardinalDirection.NORTH).build();
    }
}
