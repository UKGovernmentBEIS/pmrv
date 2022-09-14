package uk.gov.pmrv.api.permit.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;

class AnnualEmissionsTargetValidatorTest {

    private final AnnualEmissionsTargetValidator validator = new AnnualEmissionsTargetValidator();

    @Test
    void validate_hse_no_annual_emission_targets_return_violation() {
        PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.HSE)
            .build();

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> violations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(violations.isEmpty());

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations).containsExactly(
            new PermitViolation(PermitViolation.PermitViolationMessage.ANNUAL_EMISSIONS_TARGET_NOT_FOUND));
    }

    @Test
    void validate_hse_invalid_year_for_annual_emission_targets_return_violation() {
        TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2022", BigDecimal.valueOf(25000.9));
        annualEmissionsTargets.put("000123", BigDecimal.valueOf(25000.9));
        PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.HSE)
            .annualEmissionsTargets(annualEmissionsTargets)
            .build();

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> violations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(violations.isEmpty());

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations).containsExactly(
            new PermitViolation(PermitViolation.PermitViolationMessage.ANNUAL_EMISSIONS_TARGET_INVALID_YEAR));
    }

    @Test
    void validate_hse_invalid_precision_for_annual_emission_targets_return_violation() {
        TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2024", BigDecimal.valueOf(25000.15));
        annualEmissionsTargets.put("2022", BigDecimal.valueOf(25000.9));
        PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.HSE)
            .annualEmissionsTargets(annualEmissionsTargets)
            .build();

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> violations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(violations.isEmpty());

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations).containsExactly(
            new PermitViolation(PermitViolation.PermitViolationMessage.ANNUAL_EMISSIONS_TARGET_INVALID_PRECISION));
    }

    @Test
    void validate_hse() {
        TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2010", BigDecimal.valueOf(25000.1));
        annualEmissionsTargets.put("2030", BigDecimal.valueOf(24500));
        PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.HSE)
            .annualEmissionsTargets(annualEmissionsTargets)
            .build();

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> violations = result.getPermitViolations();

        assertTrue(result.isValid());
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_ghge_with_annual_emission_targets_return_violation() {
        TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2022", BigDecimal.valueOf(25000.9));
        PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .annualEmissionsTargets(annualEmissionsTargets)
            .build();

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> violations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(violations.isEmpty());

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations).containsExactly(
            new PermitViolation(PermitViolation.PermitViolationMessage.INVALID_ANNUAL_EMISSIONS_TARGET));
    }

    @Test
    void validate_ghge() {
        PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .build();

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> violations = result.getPermitViolations();

        assertTrue(result.isValid());
        assertTrue(violations.isEmpty());
    }
}