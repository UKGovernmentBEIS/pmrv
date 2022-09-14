package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EtsMeasMonitoringApproachDetails {
    private String etsAccountId;
    private String approachDescription;

    private String emissionDeterminationProcedureTitle;
    private String emissionDeterminationProcedureReference;
    private String emissionDeterminationProcedureDiagramReference;
    private String emissionDeterminationProcedureDescription;
    private String emissionDeterminationProcedureResponsiblePostDepartment;
    private String emissionDeterminationProcedureLocation;
    private String emissionDeterminationProcedureItSystem;
    private String emissionDeterminationProcedureCenOrOtherStandardsApplied;

    private String referencePeriodDeterminationProcedureTitle;
    private String referencePeriodDeterminationProcedureReference;
    private String referencePeriodDeterminationProcedureDiagramReference;
    private String referencePeriodDeterminationProcedureDescription;
    private String referencePeriodDeterminationProcedureResponsiblePostDepartment;
    private String referencePeriodDeterminationProcedureLocation;
    private String referencePeriodDeterminationProcedureItSystem;
    private String referencePeriodDeterminationProcedureCenOrOtherStandardsApplied;

    private boolean gasFlowCalculation;
    private String gasFlowCalculationProcedureTitle;
    private String gasFlowCalculationProcedureReference;
    private String gasFlowCalculationProcedureDiagramReference;
    private String gasFlowCalculationProcedureDescription;
    private String gasFlowCalculationProcedureResponsiblePostDepartment;
    private String gasFlowCalculationProcedureLocation;
    private String gasFlowCalculationProcedureItSystem;
    private String gasFlowCalculationProcedureCenOrOtherStandardsApplied;

    private boolean biomassEmissionsCalculation;
    private String biomassEmissionsProcedureTitle;
    private String biomassEmissionsProcedureReference;
    private String biomassEmissionsProcedureDiagramReference;
    private String biomassEmissionsProcedureDescription;
    private String biomassEmissionsProcedureResponsiblePostDepartment;
    private String biomassEmissionsProcedureLocation;
    private String biomassEmissionsProcedureItSystem;
    private String biomassEmissionsProcedureCenOrOtherStandardsApplied;

    private String corroboratingCalculationsProcedureTitle;
    private String corroboratingCalculationsProcedureReference;
    private String corroboratingCalculationsProcedureDiagramReference;
    private String corroboratingCalculationsProcedureDescription;
    private String corroboratingCalculationsProcedureResponsiblePostDepartment;
    private String corroboratingCalculationsProcedureLocation;
    private String corroboratingCalculationsProcedureItSystem;
    private String corroboratingCalculationsProcedureCenOrOtherStandardsApplied;

}
