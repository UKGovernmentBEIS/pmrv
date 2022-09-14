package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsCalculationMonitoringApproachDetails {
    private String etsAccountId;
    private String approachDescription;
    private boolean undertakeSamplingAnalysis;

    private String analysesProcedureDescription;
    private String analysesProcedureTitle;
    private String analysesProcedureReference;
    private String analysesProcedureDiagramReference;
    private String analysesProcedureResponsiblePostDepartment;
    private String analysesProcedureLocation;
    private String analysesProcedureItSystem;
    private String analysesProcedureCenOrOtherStandardsApplied;

    private String samplingPlanProcedureTitle;
    private String samplingPlanProcedureReference;
    private String samplingPlanDiagramReference;
    private String samplingPlanProcedureDescription;
    private String samplingPlanResponsiblePostDepartment;
    private String samplingPlanProcedureLocation;
    private String samplingPlanProcedureItSystem;
    private String samplingPlanProcedureCenOrOtherStandardsApplied;

    private String appropriatenessProcedureTitle;
    private String appropriatenessProcedureReference;
    private String appropriatenessDiagramReference;
    private String appropriatenessProcedureDescription;
    private String appropriatenessResponsiblePostDepartment;
    private String appropriatenessProcedureLocation;
    private String appropriatenessProcedureItSystem;
    private String appropriatenessProcedureCenOrOtherStandardsApplied;

    private boolean yearEndReconciliation;
    private String yearEndReconciliationProcedureTitle;
    private String yearEndReconciliationProcedureReference;
    private String yearEndReconciliationDiagramReference;
    private String yearEndReconciliationProcedureDescription;
    private String yearEndReconciliationResponsiblePostDepartment;
    private String yearEndReconciliationProcedureLocation;
    private String yearEndReconciliationProcedureItSystem;
    private String yearEndReconciliationProcedureCenOrOtherStandardsApplied;

}
