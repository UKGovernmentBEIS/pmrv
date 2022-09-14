package uk.gov.pmrv.api.migration.permit.monitoringapproaches.fallback;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;

@Log4j2
@UtilityClass
public final class FallbackApproachDetailsMapper {
    public static String constructFallbackApproachDescription(EtsFallbackMonitoringApproachDetails etsFallbackMonitoringApproachDetails) {
        return etsFallbackMonitoringApproachDetails.getApproachDescription();
    }

    public static String constructFallbackApproachJustification(EtsFallbackMonitoringApproachDetails etsFallbackMonitoringApproachDetails) {
        return etsFallbackMonitoringApproachDetails.getApproachJustification();
    }

    public static ProcedureForm constructAnnualUncertaintyAnalysis(EtsFallbackMonitoringApproachDetails etsFallbackMonitoringApproachDetails) {
        if (etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureDescription() != null ||
                etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureTitle() != null||
                etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureReference() != null||
                etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureDiagramReference() != null ||
                etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureResponsiblePostDepartment() != null ||
                etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureLocation() != null ||
                etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureItSystem() != null ||
                etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureDescription())
                    .procedureDocumentName(etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureTitle())
                    .procedureReference(etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureReference())
                    .diagramReference(etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureLocation())
                    .itSystemUsed(etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureItSystem())
                    .appliedStandards(etsFallbackMonitoringApproachDetails.getAnnualUncertaintyAnalysisProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }
}
