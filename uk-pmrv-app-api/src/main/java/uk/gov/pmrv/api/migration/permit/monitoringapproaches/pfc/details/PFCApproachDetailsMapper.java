package uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.details;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.details.EtsPFCMonitoringApproachDetails;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCTier2EmissionFactor;

@Log4j2
@UtilityClass
public final class PFCApproachDetailsMapper {
    public static String constructPFCApproachDescription(EtsPFCMonitoringApproachDetails etsPFCMonitoringApproachDetails) {
        return etsPFCMonitoringApproachDetails.getApproachDescription();
    }

    public static ProcedureForm constructCollectionEfficiencyProcedure(EtsPFCMonitoringApproachDetails etsPFCMonitoringApproachDetails) {
        if (etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureDescription() != null ||
                etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureTitle() != null||
                etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureReference() != null||
                etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureDiagramReference() != null ||
                etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureResponsiblePostDepartment() != null ||
                etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureLocation() != null ||
                etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureItSystem() != null ||
                etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureDescription())
                    .procedureDocumentName(etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureTitle())
                    .procedureReference(etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureReference())
                    .diagramReference(etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureLocation())
                    .itSystemUsed(etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureItSystem())
                    .appliedStandards(etsPFCMonitoringApproachDetails.getCollectionEfficiencyProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static PFCTier2EmissionFactor constructTier2EmissionFactor(EtsPFCMonitoringApproachDetails etsPFCMonitoringApproachDetails) {
        //if no is selected we discard the data even if they exist
        if (!etsPFCMonitoringApproachDetails.isTier2EmissionFactor()) {
            return PFCTier2EmissionFactor.builder()
                    .exist(etsPFCMonitoringApproachDetails.isTier2EmissionFactor())
                    .build();
        }
        ProcedureForm determinationInstallation = getDeterminationInstallation(etsPFCMonitoringApproachDetails);
        ProcedureForm scheduleMeasurements = getScheduleMeasurements(etsPFCMonitoringApproachDetails);
        return PFCTier2EmissionFactor.builder()
                .exist(etsPFCMonitoringApproachDetails.isTier2EmissionFactor())
                .determinationInstallation(determinationInstallation)
                .scheduleMeasurements(scheduleMeasurements)
                .build();
    }



    private static ProcedureForm getDeterminationInstallation(EtsPFCMonitoringApproachDetails etsPFCMonitoringApproachDetails) {
        if (etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureDescription() != null ||
                etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureTitle() != null||
                etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureReference() != null||
                etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureDiagramReference() != null ||
                etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureResponsiblePostDepartment() != null ||
                etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureLocation() != null ||
                etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureItSystem() != null ||
                etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureDescription())
                    .procedureDocumentName(etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureTitle())
                    .procedureReference(etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureReference())
                    .diagramReference(etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureLocation())
                    .itSystemUsed(etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureItSystem())
                    .appliedStandards(etsPFCMonitoringApproachDetails.getDeterminationOfEmissionFactorsProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    private static ProcedureForm getScheduleMeasurements(EtsPFCMonitoringApproachDetails etsPFCMonitoringApproachDetails) {
        if (etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureDescription() != null ||
                etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureTitle() != null||
                etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureReference() != null||
                etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureDiagramReference() != null ||
                etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureResponsiblePostDepartment() != null ||
                etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureLocation() != null ||
                etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureItSystem() != null ||
                etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureDescription())
                    .procedureDocumentName(etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureTitle())
                    .procedureReference(etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureReference())
                    .diagramReference(etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureLocation())
                    .itSystemUsed(etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureItSystem())
                    .appliedStandards(etsPFCMonitoringApproachDetails.getScheduleOfMeasurementsProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }


}
