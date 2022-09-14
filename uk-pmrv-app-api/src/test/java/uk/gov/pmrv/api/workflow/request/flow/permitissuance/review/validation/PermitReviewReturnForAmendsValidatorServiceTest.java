package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.common.SourceStreamCategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Laboratory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.measuredemissions.MeasMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.measuredemissions.MeasMeasuredEmissionsTier;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitReviewReturnForAmendsValidatorServiceTest {

    @InjectMocks
    private PermitReviewReturnForAmendsValidatorService serviceValidator;

    @Test
    void validate() {
        PermitIssuanceApplicationReviewRequestTaskPayload payload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permit(buildPermit())
                .reviewGroupDecisions(Map.of(
                        PermitReviewGroup.CONFIDENTIALITY_STATEMENT, buildReviewDecision(ReviewDecisionType.ACCEPTED),
                        PermitReviewGroup.FUELS_AND_EQUIPMENT, buildReviewDecision(ReviewDecisionType.REJECTED),
                        PermitReviewGroup.INSTALLATION_DETAILS, buildReviewDecision(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                ))
                .build();

        // Invoke
        assertDoesNotThrow(() -> serviceValidator.validate(payload));
    }

    @Test
    void validate_no_amends() {
        PermitIssuanceApplicationReviewRequestTaskPayload payload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permit(buildPermit())
                .reviewGroupDecisions(Map.of(
                        PermitReviewGroup.CONFIDENTIALITY_STATEMENT, buildReviewDecision(ReviewDecisionType.ACCEPTED),
                        PermitReviewGroup.FUELS_AND_EQUIPMENT, buildReviewDecision(ReviewDecisionType.ACCEPTED),
                        PermitReviewGroup.INSTALLATION_DETAILS, buildReviewDecision(ReviewDecisionType.ACCEPTED),
                        PermitReviewGroup.MANAGEMENT_PROCEDURES, buildReviewDecision(ReviewDecisionType.ACCEPTED),
                        PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, buildReviewDecision(ReviewDecisionType.ACCEPTED),
                        PermitReviewGroup.ADDITIONAL_INFORMATION, buildReviewDecision(ReviewDecisionType.ACCEPTED),
                        PermitReviewGroup.DEFINE_MONITORING_APPROACHES, buildReviewDecision(ReviewDecisionType.ACCEPTED),
                        PermitReviewGroup.UNCERTAINTY_ANALYSIS, buildReviewDecision(ReviewDecisionType.ACCEPTED),
                        PermitReviewGroup.MEASUREMENT, buildReviewDecision(ReviewDecisionType.ACCEPTED)
                ))
                .build();

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> serviceValidator.validate(payload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PERMIT_REVIEW);
    }

    private PermitIssuanceReviewDecision buildReviewDecision(ReviewDecisionType type) {
        return PermitIssuanceReviewDecision.builder()
                .type(type)
                .notes("notes")
                .build();
    }

    private Permit buildPermit() {
        return Permit.builder()
                .monitoringApproaches(buildMonitoringApproaches())
                .build();
    }

    private MonitoringApproaches buildMonitoringApproaches() {
        EnumMap<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches = new EnumMap<>(MonitoringApproachType.class);

        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT, buildMeasurementMonitoringApproach());

        return MonitoringApproaches.builder().monitoringApproaches(monitoringApproaches).build();
    }

    private MeasMonitoringApproach buildMeasurementMonitoringApproach() {
        return MeasMonitoringApproach.builder()
                .type(MonitoringApproachType.MEASUREMENT)
                .approachDescription("measurementApproachDescription")
                .emissionDetermination(buildProcedureForm("emissionDetermination"))
                .referencePeriodDetermination(buildProcedureForm("referencePeriodDetermination"))
                .gasFlowCalculation(ProcedureOptionalForm.builder().exist(false).build())
                .biomassEmissions(ProcedureOptionalForm.builder().exist(false).build())
                .corroboratingCalculations(buildProcedureForm("corroboratingCalculations"))
                .sourceStreamCategoryAppliedTiers(List.of(MeasSourceStreamCategoryAppliedTier.builder()
                        .sourceStreamCategory(MeasSourceStreamCategory.builder()
                                .sourceStream(UUID.randomUUID().toString())
                                .emissionSources(Set.of(UUID.randomUUID().toString()))
                                .emissionPoints(Set.of(UUID.randomUUID().toString()))
                                .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.123))
                                .categoryType(SourceStreamCategoryType.MAJOR).build())
                        .measuredEmissions(buildMeasMeasuredEmissions())
                        .appliedStandard(buildAppliedStandard())
                        .build()))
                .build();
    }

    private ProcedureForm buildProcedureForm(String value) {
        return ProcedureForm.builder()
                .procedureDescription("procedureDescription" + value)
                .procedureDocumentName("procedureDocumentName" + value)
                .procedureReference("procedureReference" + value)
                .diagramReference("diagramReference" + value)
                .responsibleDepartmentOrRole("responsibleDepartmentOrRole" + value)
                .locationOfRecords("locationOfRecords" + value)
                .itSystemUsed("itSystemUsed" + value)
                .appliedStandards("appliedStandards" + value)
                .build();
    }

    private MeasMeasuredEmissions buildMeasMeasuredEmissions() {
        return MeasMeasuredEmissions.builder()
                .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
                .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
                .tier(MeasMeasuredEmissionsTier.TIER_4)
                .highestRequiredTier(HighestRequiredTier.builder().build())
                .build();
    }

    private AppliedStandard buildAppliedStandard() {
        return AppliedStandard.builder()
                .parameter("parameter")
                .appliedStandard("applied standard")
                .deviationFromAppliedStandardExist(true)
                .deviationFromAppliedStandardDetails("deviation details")
                .laboratory(Laboratory.builder().laboratoryName("lab").laboratoryAccredited(true).build())
                .build();
    }
}
