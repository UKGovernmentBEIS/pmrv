package uk.gov.pmrv.api.reporting.domain.regulatedactivities;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.CapacityUnit;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;

class AerRegulatedActivitiesTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenTypesAreUnique_thenValid() {

        final AerRegulatedActivities activities = AerRegulatedActivities.builder()
            .regulatedActivities(List.of(
                    AerRegulatedActivity.builder()
                        .id("id1")
                        .type(RegulatedActivityType.COMBUSTION)
                        .capacity(BigDecimal.TEN)
                        .capacityUnit(CapacityUnit.KG_PER_DAY)
                        .hasEnergyCrf(true)
                        .hasIndustrialCrf(false)
                        .energyCrf(CrfCode._1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES)
                        .build(),
                    AerRegulatedActivity.builder()
                        .id("id2")
                        .type(RegulatedActivityType.CEMENT_CLINKER_PRODUCTION)
                        .capacity(BigDecimal.TEN)
                        .capacityUnit(CapacityUnit.KW)
                        .hasEnergyCrf(true)
                        .hasIndustrialCrf(false)
                        .energyCrf(CrfCode._1_A_2_B_NON_FERROUS_METALS)
                        .build(),
                    AerRegulatedActivity.builder()
                        .id("id3")
                        .type(RegulatedActivityType.COKE_PRODUCTION)
                        .capacity(BigDecimal.ONE)
                        .capacityUnit(CapacityUnit.TONNES_PER_HOUR)
                        .hasEnergyCrf(true)
                        .hasIndustrialCrf(false)
                        .energyCrf(CrfCode._2_B_10_OTHER)
                        .build()
                )
            )
            .build();

        final Set<ConstraintViolation<AerRegulatedActivities>> violations = validator.validate(activities);

        assertThat(violations).isEqualTo(Collections.emptySet());
    }

    @Test
    void whenTypesAreNotUnique_thenNotValid() {

        final AerRegulatedActivities activities = AerRegulatedActivities.builder()
            .regulatedActivities(List.of(
                    AerRegulatedActivity.builder()
                        .id("id1")
                        .type(RegulatedActivityType.COMBUSTION)
                        .capacity(BigDecimal.TEN)
                        .capacityUnit(CapacityUnit.KG_PER_DAY)
                        .hasEnergyCrf(true)
                        .hasIndustrialCrf(false)
                        .energyCrf(CrfCode._1_A_1_C_MANUFACTURE_OF_SOLID_FUELS_AND_OTHER_ENERGY_INDUSTRIES)
                        .build(),
                    AerRegulatedActivity.builder()
                        .id("id2")
                        .type(RegulatedActivityType.COMBUSTION)
                        .capacity(BigDecimal.TEN)
                        .capacityUnit(CapacityUnit.KW)
                        .hasEnergyCrf(true)
                        .hasIndustrialCrf(false)
                        .energyCrf(CrfCode._1_A_2_B_NON_FERROUS_METALS)
                        .build(),
                    AerRegulatedActivity.builder()
                        .id("id3")
                        .type(RegulatedActivityType.COKE_PRODUCTION)
                        .capacity(BigDecimal.ONE)
                        .capacityUnit(CapacityUnit.TONNES_PER_HOUR)
                        .hasEnergyCrf(true)
                        .hasIndustrialCrf(false)
                        .energyCrf(CrfCode._2_B_10_OTHER)
                        .build()
                )
            )
            .build();

        final Set<ConstraintViolation<AerRegulatedActivities>> violations = validator.validate(activities);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("{aer.regulated.activities.unique.type}");
    }
}
