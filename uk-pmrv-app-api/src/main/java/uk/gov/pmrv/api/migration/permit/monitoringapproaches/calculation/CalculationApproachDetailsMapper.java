package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.ProcedurePlan;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.SamplingPlan;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.SamplingPlanDetails;

@Log4j2
@UtilityClass
public final class CalculationApproachDetailsMapper {
    public static String constructCalculationApproachDescription(EtsCalculationMonitoringApproachDetails etsCalculationMonitoringApproachDetails) {
        return etsCalculationMonitoringApproachDetails.getApproachDescription();
    }

    public static SamplingPlan constructCalculationSamplingPlan(EtsCalculationMonitoringApproachDetails etsCalculationMonitoringApproachDetails) {
        //if no is selected we discard the data even if they exist
        if (!etsCalculationMonitoringApproachDetails.isUndertakeSamplingAnalysis()) {
            return SamplingPlan.builder()
                    .exist(etsCalculationMonitoringApproachDetails.isUndertakeSamplingAnalysis())
                    .build();
        }

        ProcedureForm analysis = getAnalysis(etsCalculationMonitoringApproachDetails);
        ProcedurePlan procedurePlan = getProcedurePlan(etsCalculationMonitoringApproachDetails);
        ProcedureForm appropriateness = getAppropriateness(etsCalculationMonitoringApproachDetails);
        ProcedureOptionalForm yearEndReconciliation = getYearEndReconciliation(etsCalculationMonitoringApproachDetails);
        SamplingPlanDetails samplingPlanDetails = null;

        if (analysis != null || procedurePlan != null || appropriateness != null || yearEndReconciliation != null) {
            samplingPlanDetails = SamplingPlanDetails.builder()
                    .analysis(analysis)
                    .procedurePlan(procedurePlan)
                    .appropriateness(appropriateness)
                    .yearEndReconciliation(yearEndReconciliation)
                    .build();
        }
        return SamplingPlan.builder()
                .exist(etsCalculationMonitoringApproachDetails.isUndertakeSamplingAnalysis())
                .details(samplingPlanDetails)
                .build();
    }

    private static ProcedureForm getAnalysis(EtsCalculationMonitoringApproachDetails etsCalculationMonitoringApproachDetails) {
        if (etsCalculationMonitoringApproachDetails.getAnalysesProcedureDescription() != null ||
                etsCalculationMonitoringApproachDetails.getAnalysesProcedureTitle() != null||
                etsCalculationMonitoringApproachDetails.getAnalysesProcedureReference() != null||
                etsCalculationMonitoringApproachDetails.getAnalysesProcedureDiagramReference() != null ||
                etsCalculationMonitoringApproachDetails.getAnalysesProcedureResponsiblePostDepartment() != null ||
                etsCalculationMonitoringApproachDetails.getAnalysesProcedureLocation() != null ||
                etsCalculationMonitoringApproachDetails.getAnalysesProcedureItSystem() != null ||
                etsCalculationMonitoringApproachDetails.getAnalysesProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsCalculationMonitoringApproachDetails.getAnalysesProcedureDescription())
                    .procedureDocumentName(etsCalculationMonitoringApproachDetails.getAnalysesProcedureTitle())
                    .procedureReference(etsCalculationMonitoringApproachDetails.getAnalysesProcedureReference())
                    .diagramReference(etsCalculationMonitoringApproachDetails.getAnalysesProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsCalculationMonitoringApproachDetails.getAnalysesProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsCalculationMonitoringApproachDetails.getAnalysesProcedureLocation())
                    .itSystemUsed(etsCalculationMonitoringApproachDetails.getAnalysesProcedureItSystem())
                    .appliedStandards(etsCalculationMonitoringApproachDetails.getAnalysesProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    private static ProcedureForm getAppropriateness(EtsCalculationMonitoringApproachDetails etsCalculationMonitoringApproachDetails) {
        if (etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureDescription() != null ||
                etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureTitle() != null||
                etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureReference() != null||
                etsCalculationMonitoringApproachDetails.getAppropriatenessDiagramReference() != null ||
                etsCalculationMonitoringApproachDetails.getAppropriatenessResponsiblePostDepartment() != null ||
                etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureLocation() != null ||
                etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureItSystem() != null ||
                etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureCenOrOtherStandardsApplied() != null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureDescription())
                    .procedureDocumentName(etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureTitle())
                    .procedureReference(etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureReference())
                    .diagramReference(etsCalculationMonitoringApproachDetails.getAppropriatenessDiagramReference())
                    .responsibleDepartmentOrRole(etsCalculationMonitoringApproachDetails.getAppropriatenessResponsiblePostDepartment())
                    .locationOfRecords(etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureLocation())
                    .itSystemUsed(etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureItSystem())
                    .appliedStandards(etsCalculationMonitoringApproachDetails.getAppropriatenessProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    private static ProcedurePlan getProcedurePlan(EtsCalculationMonitoringApproachDetails etsCalculationMonitoringApproachDetails) {
        if (etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureDescription() != null ||
                etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureTitle() != null||
                etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureReference() != null||
                etsCalculationMonitoringApproachDetails.getSamplingPlanDiagramReference() != null ||
                etsCalculationMonitoringApproachDetails.getAppropriatenessResponsiblePostDepartment() != null ||
                etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureLocation() != null ||
                etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureItSystem() != null ||
                etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedurePlan.builder()
                    .procedureDescription(etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureDescription())
                    .procedureDocumentName(etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureTitle())
                    .procedureReference(etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureReference())
                    .diagramReference(etsCalculationMonitoringApproachDetails.getSamplingPlanDiagramReference())
                    .responsibleDepartmentOrRole(etsCalculationMonitoringApproachDetails.getAppropriatenessResponsiblePostDepartment())
                    .locationOfRecords(etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureLocation())
                    .itSystemUsed(etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureItSystem())
                    .appliedStandards(etsCalculationMonitoringApproachDetails.getSamplingPlanProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    private static ProcedureOptionalForm getYearEndReconciliation(EtsCalculationMonitoringApproachDetails etsCalculationMonitoringApproachDetails) {
        ProcedureForm yearEndReconciliationProcedureForm = null;

        //if no is selected we discard the data even if they exist
        if (!etsCalculationMonitoringApproachDetails.isYearEndReconciliation()) {
            return ProcedureOptionalForm.builder()
                    .exist(etsCalculationMonitoringApproachDetails.isYearEndReconciliation())
                    .build();
        }

        if (etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureDescription() != null ||
                etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureTitle() != null||
                etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureReference() != null||
                etsCalculationMonitoringApproachDetails.getYearEndReconciliationDiagramReference() != null ||
                etsCalculationMonitoringApproachDetails.getYearEndReconciliationResponsiblePostDepartment() != null ||
                etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureLocation() != null ||
                etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureItSystem() != null ||
                etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureCenOrOtherStandardsApplied() != null) {

            yearEndReconciliationProcedureForm = ProcedureForm.builder()
                    .procedureDescription(etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureDescription())
                    .procedureDocumentName(etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureTitle())
                    .procedureReference(etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureReference())
                    .diagramReference(etsCalculationMonitoringApproachDetails.getYearEndReconciliationDiagramReference())
                    .responsibleDepartmentOrRole(etsCalculationMonitoringApproachDetails.getYearEndReconciliationResponsiblePostDepartment())
                    .locationOfRecords(etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureLocation())
                    .itSystemUsed(etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureItSystem())
                    .appliedStandards(etsCalculationMonitoringApproachDetails.getYearEndReconciliationProcedureCenOrOtherStandardsApplied())
                    .build();
        }

        return ProcedureOptionalForm.builder()
                .exist(etsCalculationMonitoringApproachDetails.isYearEndReconciliation())
                .procedureForm(yearEndReconciliationProcedureForm)
                .build();
    }
}
