package uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.details;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsPFCMonitoringApproachDetails {
    private String etsAccountId;
    private String approachDescription;

    private String collectionEfficiencyProcedureDescription;
    private String collectionEfficiencyProcedureTitle;
    private String collectionEfficiencyProcedureReference;
    private String collectionEfficiencyProcedureDiagramReference;
    private String collectionEfficiencyProcedureResponsiblePostDepartment;
    private String collectionEfficiencyProcedureLocation;
    private String collectionEfficiencyProcedureItSystem;
    private String collectionEfficiencyProcedureCenOrOtherStandardsApplied;

    private boolean tier2EmissionFactor;

    private String determinationOfEmissionFactorsProcedureDescription;
    private String determinationOfEmissionFactorsProcedureTitle;
    private String determinationOfEmissionFactorsProcedureReference;
    private String determinationOfEmissionFactorsProcedureDiagramReference;
    private String determinationOfEmissionFactorsProcedureResponsiblePostDepartment;
    private String determinationOfEmissionFactorsProcedureLocation;
    private String determinationOfEmissionFactorsProcedureItSystem;
    private String determinationOfEmissionFactorsProcedureCenOrOtherStandardsApplied;

    private String scheduleOfMeasurementsProcedureDescription;
    private String scheduleOfMeasurementsProcedureTitle;
    private String scheduleOfMeasurementsProcedureReference;
    private String scheduleOfMeasurementsProcedureDiagramReference;
    private String scheduleOfMeasurementsProcedureResponsiblePostDepartment;
    private String scheduleOfMeasurementsProcedureLocation;
    private String scheduleOfMeasurementsProcedureItSystem;
    private String scheduleOfMeasurementsProcedureCenOrOtherStandardsApplied;
}
