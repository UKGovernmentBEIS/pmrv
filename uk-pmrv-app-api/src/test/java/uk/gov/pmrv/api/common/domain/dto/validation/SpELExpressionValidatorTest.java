package uk.gov.pmrv.api.common.domain.dto.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummary;
import uk.gov.pmrv.api.permit.domain.envmanagementsystem.EnvironmentalManagementSystem;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvPermitOrLicence;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.measuredemissions.MeasMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.measuredemissions.MeasMeasuredEmissionsTier;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.DateOfNonCompliance;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.OtherFactor;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.ReportingType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationModification;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationModificationType;

@ExtendWith(MockitoExtension.class)
class SpELExpressionValidatorTest {
    
    @InjectMocks
    private SpELExpressionValidator validator;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    
    @Mock
    private ConstraintViolationBuilder constraintViolationBuilder;
    
    private final String message = "message";
    
    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(validator, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(validator, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(validator, "message", message);
    }
    
    @Test
    void isValid_xnor_boolean_object_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{#exist == (#procedureForm != null)}");
        
        boolean valid = validator.isValid(ProcedureOptionalForm.builder()
                .exist(true).procedureForm(ProcedureForm.builder().build())
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(ProcedureOptionalForm.builder()
                .exist(false).procedureForm(null)
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_xnor_boolean_object_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{#exist == (#procedureForm != null)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
            .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(ProcedureOptionalForm.builder().exist(true).build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(ProcedureOptionalForm.builder()
                .exist(false).procedureForm(ProcedureForm.builder().build()).build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_xnor_boolean_collection_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{#exist == (#envPermitOrLicences?.size() gt 0)}");
        
        boolean valid = validator.isValid(EnvironmentalPermitsAndLicences.builder().exist(false).envPermitOrLicences(List.of()).build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(
                EnvironmentalPermitsAndLicences.builder().exist(true)
                        .envPermitOrLicences(List.of(EnvPermitOrLicence.builder().build())).build(),
                constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_xnor_boolean_collection_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{#exist == (#envPermitOrLicences?.size() gt 0)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(EnvironmentalPermitsAndLicences.builder().exist(true).envPermitOrLicences(List.of()).build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(
                EnvironmentalPermitsAndLicences.builder().exist(false)
                        .envPermitOrLicences(List.of(EnvPermitOrLicence.builder().build())).build(),
                constraintValidatorContext);
        assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_xor_boolean_and_object_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{#excludedRegulatedActivity != (#regulatedActivity != null)}");
        boolean valid = validator.isValid(EmissionSummary.builder()
                .excludedRegulatedActivity(true).build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(EmissionSummary.builder()
                .excludedRegulatedActivity(false)
                .regulatedActivity("dsd").build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_xor_boolean_and_object_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{#excludedRegulatedActivity != (#regulatedActivity != null)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
            .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(EmissionSummary.builder()
                .excludedRegulatedActivity(true)
                .regulatedActivity("dsd").build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(EmissionSummary.builder()
                .excludedRegulatedActivity(false).build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_xnor_boolean_and_Boolean_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{#exist == (#certified != null)}");
        boolean valid = validator.isValid(EnvironmentalManagementSystem.builder()
                .exist(false).build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(EnvironmentalManagementSystem.builder()
                .exist(true).certified(Boolean.TRUE).build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(EnvironmentalManagementSystem.builder()
                .exist(true).certified(Boolean.FALSE).build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_xnor_boolean_and_Boolean_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{#exist == (#certified != null)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(EnvironmentalManagementSystem.builder()
                .exist(false).certified(Boolean.TRUE).build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(EnvironmentalManagementSystem.builder()
                .exist(true).build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_Boolean_and_string_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{T(java.lang.Boolean).TRUE.equals(#certified) == (#certificationStandard != null)}");
        boolean valid = validator.isValid(EnvironmentalManagementSystem.builder()
                .exist(true).certified(Boolean.FALSE).build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_Boolean_and_string_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{T(java.lang.Boolean).TRUE.equals(#certified) == (#certificationStandard != null)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(EnvironmentalManagementSystem.builder()
                .exist(true)
                .certified(Boolean.TRUE)
                .certificationStandard(null).build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        verify(constraintValidatorContext, times(1)).disableDefaultConstraintViolation();
        verify(constraintValidatorContext, times(1)).buildConstraintViolationWithTemplate("{" + message + "}");
    }
    
    @Test
    void isValid_string_equal_and_string_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{(#type eq 'OTHER') == (#otherTypeName != null)}");
        boolean valid = validator.isValid(MeasurementDeviceOrMethod.builder()
                .type(MeasurementDeviceType.BALANCE).otherTypeName(null).build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(MeasurementDeviceOrMethod.builder()
                .type(MeasurementDeviceType.OTHER).otherTypeName("Sdsd").build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(MeasurementDeviceOrMethod.builder()
                .type(null).otherTypeName(null).build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_string_equal_and_string_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{(#type eq 'OTHER') == (#otherTypeName != null)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(MeasurementDeviceOrMethod.builder()
                .type(MeasurementDeviceType.BALANCE).otherTypeName("fdfD").build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(MeasurementDeviceOrMethod.builder()
                .type(MeasurementDeviceType.OTHER).otherTypeName(null).build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_string_equal_with_or_and_boolean_not_null_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{(#tier eq 'TIER_1' or #tier eq 'TIER_2' or #tier eq 'TIER_3') == (#isHighestRequiredTier != null)}");
        boolean valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.TIER_1)
                .highestRequiredTier(HighestRequiredTier.builder().isHighestRequiredTier(Boolean.TRUE).build())
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.TIER_1)
                .highestRequiredTier(HighestRequiredTier.builder().isHighestRequiredTier(Boolean.FALSE).build())
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.NO_TIER)
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_string_equal_with_or_and_boolean_not_null_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{(#tier eq 'TIER_1' or #tier eq 'TIER_2' or #tier eq 'TIER_3') == (#isHighestRequiredTier != null)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.TIER_1)
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.TIER_2)
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.TIER_4)
                .highestRequiredTier(HighestRequiredTier.builder().isHighestRequiredTier(Boolean.TRUE).build())
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.NO_TIER)
                .highestRequiredTier(HighestRequiredTier.builder().isHighestRequiredTier(Boolean.FALSE).build())
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_string_equal_with_or_and_boolean_null_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{(#tier eq 'TIER_4' or #tier eq 'NO_TIER') == (#isHighestRequiredTier == null)}");
        boolean valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.TIER_4)
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.NO_TIER)
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.TIER_1)
                .highestRequiredTier(HighestRequiredTier.builder().isHighestRequiredTier(Boolean.FALSE).build())
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_string_equal_with_or_and_boolean_null_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{(#tier eq 'TIER_4' or #tier eq 'NO_TIER') == (#isHighestRequiredTier == null)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.TIER_1)
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.NO_TIER)
                .highestRequiredTier(HighestRequiredTier.builder().isHighestRequiredTier(Boolean.FALSE).build())
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
        
        valid = validator.isValid(MeasMeasuredEmissions.builder()
                .tier(MeasMeasuredEmissionsTier.TIER_4)
                .highestRequiredTier(HighestRequiredTier.builder().isHighestRequiredTier(Boolean.FALSE).build())
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_Boolean_equal_false_and_object_not_null_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{T(java.lang.Boolean).FALSE.equals(#isHighestRequiredTier) == (#noHighestRequiredTierJustification != null)}");
        boolean valid = validator.isValid(HighestRequiredTier.builder()
                .isHighestRequiredTier(Boolean.TRUE)
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(HighestRequiredTier.builder().build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(HighestRequiredTier.builder()
                    .isHighestRequiredTier(Boolean.FALSE)
                    .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder().build())
                    .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_Boolean_equal_false_and_object_not_null_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{T(java.lang.Boolean).FALSE.equals(#isHighestRequiredTier) == (#noHighestRequiredTierJustification != null)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(HighestRequiredTier.builder()
                .isHighestRequiredTier(Boolean.FALSE)
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_Boolean_at_least_one_not_null_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{T(java.lang.Boolean).TRUE.equals(#isCostUnreasonable) || T(java.lang.Boolean).TRUE.equals(#isTechnicallyInfeasible)}");
        boolean valid = validator.isValid(NoHighestRequiredTierJustification.builder()
                .isCostUnreasonable(Boolean.TRUE)
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        valid = validator.isValid(NoHighestRequiredTierJustification.builder()
                .isCostUnreasonable(Boolean.TRUE)
                .isTechnicallyInfeasible(Boolean.TRUE)
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_Boolean_at_least_one_not_null_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{T(java.lang.Boolean).TRUE.equals(#isCostUnreasonable) || T(java.lang.Boolean).TRUE.equals(#isTechnicallyInfeasible)}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(NoHighestRequiredTierJustification.builder()
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_date_compare_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{(#noticeDate == null) || T(java.time.LocalDate).now().plusDays(28).isBefore(T(java.time.LocalDate).parse(#noticeDate))}");
        
        boolean valid = validator.isValid(PermitSurrenderReviewDeterminationGrant.builder()
                .noticeDate(LocalDate.now().plusDays(29))
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_date_compare_valid_when_null() {
        ReflectionTestUtils.setField(validator, "expression", "{(#noticeDate == null) || T(java.time.LocalDate).now().plusDays(28).isBefore(T(java.time.LocalDate).parse(#noticeDate))}");
        
        boolean valid = validator.isValid(PermitSurrenderReviewDeterminationGrant.builder()
                .noticeDate(null)
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
        
        verifyNoInteractions(constraintValidatorContext, constraintViolationBuilder);
    }
    
    @Test
    void isValid_date_compare_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{(#noticeDate == null) || T(java.time.LocalDate).now().plusDays(28).isBefore(T(java.time.LocalDate).parse(#noticeDate))}");
        
        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);
        
        boolean valid = validator.isValid(PermitSurrenderReviewDeterminationGrant.builder()
                .noticeDate(LocalDate.now().plusDays(27))
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }

    @Test
    void isValid_type_not_in_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{{'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT','EXCEEDED_THRESHOLD_STATED_HSE_PERMIT'}.?[#this == #reportingType].empty || (#startDateOfNonCompliance != null && #endDateOfNonCompliance == null)}");

        boolean valid = validator.isValid(OtherFactor.builder()
                .reportingType(ReportingType.EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT)
                .dateOfNonCompliance(DateOfNonCompliance.builder().startDateOfNonCompliance(LocalDate.now()).build())
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
    }

    @Test
    void isValid_type_not_in_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{{'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT','EXCEEDED_THRESHOLD_STATED_HSE_PERMIT'}.?[#this == #reportingType].empty || (#startDateOfNonCompliance != null && #endDateOfNonCompliance == null)}");

        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
                .thenReturn(constraintViolationBuilder);

        boolean valid = validator.isValid(OtherFactor.builder()
                .reportingType(ReportingType.EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT)
                .build(), constraintValidatorContext);
        assertThat(valid).isFalse();
    }

    @Test
    void isValid_type_not_in_with_null_type_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{{'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT','EXCEEDED_THRESHOLD_STATED_HSE_PERMIT'}.?[#this == #reportingType].empty || (#startDateOfNonCompliance != null && #endDateOfNonCompliance == null)}");

        boolean valid = validator.isValid(OtherFactor.builder()
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
    }
    
    @Test
    void isValid_enum_value_check_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{(T(uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationModificationType).valueOf(#type).otherSummaryApplies) == (#otherSummary != null)}");

        boolean valid = validator.isValid(PermitVariationModification.builder()
        		.type(PermitVariationModificationType.CALCULATION_TO_MEASUREMENT_METHODOLOGIES)
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
    }
    
    @Test
    void isValid_enum_value_check_other_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{(T(uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationModificationType).valueOf(#type).otherSummaryApplies) == (#otherSummary != null)}");

        boolean valid = validator.isValid(PermitVariationModification.builder()
        		.type(PermitVariationModificationType.OTHER_MONITORING_METHODOLOGY_PLAN)
        		.otherSummary("fdf")
                .build(), constraintValidatorContext);
        assertThat(valid).isTrue();
    }
    
    @Test
    void isValid_enum_value_check_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{(T(uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationModificationType).valueOf(#type).otherSummaryApplies) == (#otherSummary != null)}");

        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);

		boolean valid = validator.isValid(PermitVariationModification.builder()
        		.type(PermitVariationModificationType.CALCULATION_TO_MEASUREMENT_METHODOLOGIES)
        		.otherSummary("other")
                .build(), constraintValidatorContext);
		assertThat(valid).isFalse();
    }
    
    @Test
    void isValid_enum_value_check_other_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{(T(uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationModificationType).valueOf(#type).otherSummaryApplies) == (#otherSummary != null)}");

        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
        .thenReturn(constraintViolationBuilder);

		boolean valid = validator.isValid(PermitVariationModification.builder()
        		.type(PermitVariationModificationType.OTHER_MONITORING_METHODOLOGY_PLAN)
                .build(), constraintValidatorContext);
		assertThat(valid).isFalse();
    }

    @Test
    void isValid_collection_contains_value_and_object_not_null_valid() {
        ReflectionTestUtils.setField(validator, "expression", "{#monitoringApproachTypes.contains('MEASUREMENT') == (#emissionPoints != null)}");

        Aer aer = Aer.builder()
            .monitoringApproachTypes(Set.of(MonitoringApproachType.CALCULATION, MonitoringApproachType.MEASUREMENT))
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(EmissionPoint.builder().build())).build())
            .build();

        assertTrue(validator.isValid(aer, constraintValidatorContext));

        aer = Aer.builder()
            .monitoringApproachTypes(Set.of(MonitoringApproachType.CALCULATION, MonitoringApproachType.PFC))
            .build();

        assertTrue(validator.isValid(aer, constraintValidatorContext));
    }

    @Test
    void isValid_collection_contains_value_and_object_not_null_invalid() {
        ReflectionTestUtils.setField(validator, "expression", "{#monitoringApproachTypes.contains('MEASUREMENT') == (#emissionPoints != null)}");

        when(constraintValidatorContext.buildConstraintViolationWithTemplate("{" + this.message + "}"))
            .thenReturn(constraintViolationBuilder);

        Aer aer = Aer.builder()
            .monitoringApproachTypes(Set.of(MonitoringApproachType.CALCULATION, MonitoringApproachType.PFC))
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(EmissionPoint.builder().build())).build())
            .build();

        assertFalse(validator.isValid(aer, constraintValidatorContext));

        aer = Aer.builder()
            .monitoringApproachTypes(Set.of(MonitoringApproachType.CALCULATION, MonitoringApproachType.MEASUREMENT))
            .build();

        assertFalse(validator.isValid(aer, constraintValidatorContext));
    }
}