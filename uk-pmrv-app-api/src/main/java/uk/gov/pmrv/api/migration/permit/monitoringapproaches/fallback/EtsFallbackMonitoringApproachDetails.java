package uk.gov.pmrv.api.migration.permit.monitoringapproaches.fallback;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsFallbackMonitoringApproachDetails {
    private String etsAccountId;
    private String approachDescription;
    private String approachJustification;

    private String annualUncertaintyAnalysisProcedureDescription;
    private String annualUncertaintyAnalysisProcedureTitle;
    private String annualUncertaintyAnalysisProcedureReference;
    private String annualUncertaintyAnalysisProcedureDiagramReference;
    private String annualUncertaintyAnalysisProcedureResponsiblePostDepartment;
    private String annualUncertaintyAnalysisProcedureLocation;
    private String annualUncertaintyAnalysisProcedureItSystem;
    private String annualUncertaintyAnalysisProcedureCenOrOtherStandardsApplied;
}
