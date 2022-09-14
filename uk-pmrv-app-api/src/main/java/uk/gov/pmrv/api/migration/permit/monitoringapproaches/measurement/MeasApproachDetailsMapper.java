package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurement;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;

@UtilityClass
public class MeasApproachDetailsMapper {
    public static String constructMeasApproachDescription(EtsMeasMonitoringApproachDetails etsMeasMonitoringApproachDetails) {
        return etsMeasMonitoringApproachDetails.getApproachDescription();
    }

    public static ProcedureForm constructEmissionDetermination(EtsMeasMonitoringApproachDetails etsMeasMonitoringApproachDetails) {
        if (etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureTitle() != null ||
                etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureReference() != null||
                etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureDiagramReference() != null||
                etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureDescription() != null ||
                etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureResponsiblePostDepartment() != null ||
                etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureLocation() != null ||
                etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureItSystem() != null ||
                etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureDescription())
                    .procedureDocumentName(etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureTitle())
                    .procedureReference(etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureReference())
                    .diagramReference(etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureLocation())
                    .itSystemUsed(etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureItSystem())
                    .appliedStandards(etsMeasMonitoringApproachDetails.getEmissionDeterminationProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static ProcedureForm constructReferencePeriodDetermination(EtsMeasMonitoringApproachDetails etsMeasMonitoringApproachDetails) {
        if (etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureTitle() != null ||
                etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureReference() != null||
                etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureDiagramReference() != null||
                etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureDescription() != null ||
                etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureResponsiblePostDepartment() != null ||
                etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureLocation() != null ||
                etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureItSystem() != null ||
                etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureDescription())
                    .procedureDocumentName(etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureTitle())
                    .procedureReference(etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureReference())
                    .diagramReference(etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureLocation())
                    .itSystemUsed(etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureItSystem())
                    .appliedStandards(etsMeasMonitoringApproachDetails.getReferencePeriodDeterminationProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static ProcedureOptionalForm constructGasFlowCalculation(EtsMeasMonitoringApproachDetails etsMeasMonitoringApproachDetails) {
        ProcedureForm gasFlowCalculationProcedureForm = null;

        //if no is selected we discard the data even if they exist
        if (!etsMeasMonitoringApproachDetails.isGasFlowCalculation()) {
            return ProcedureOptionalForm.builder()
                    .exist(etsMeasMonitoringApproachDetails.isGasFlowCalculation())
                    .build();
        }
        if (etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureTitle() != null ||
                etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureReference() != null||
                etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureDiagramReference() != null||
                etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureDescription() != null ||
                etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureResponsiblePostDepartment() != null ||
                etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureLocation() != null ||
                etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureItSystem() != null ||
                etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureCenOrOtherStandardsApplied()!= null) {

            gasFlowCalculationProcedureForm = ProcedureForm.builder()
                    .procedureDescription(etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureDescription())
                    .procedureDocumentName(etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureTitle())
                    .procedureReference(etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureReference())
                    .diagramReference(etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureLocation())
                    .itSystemUsed(etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureItSystem())
                    .appliedStandards(etsMeasMonitoringApproachDetails.getGasFlowCalculationProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return ProcedureOptionalForm.builder()
                .exist(etsMeasMonitoringApproachDetails.isGasFlowCalculation())
                .procedureForm(gasFlowCalculationProcedureForm)
                .build();
    }

    public static ProcedureOptionalForm constructBiomassEmissions(EtsMeasMonitoringApproachDetails etsMeasMonitoringApproachDetails) {
        ProcedureForm biomassEmissionsProcedureForm = null;

        //if no is selected we discard the data even if they exist
        if (!etsMeasMonitoringApproachDetails.isBiomassEmissionsCalculation()) {
            return ProcedureOptionalForm.builder()
                    .exist(etsMeasMonitoringApproachDetails.isBiomassEmissionsCalculation())
                    .build();
        }

        if (etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureTitle() != null ||
                etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureReference() != null||
                etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureDiagramReference() != null||
                etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureDescription() != null ||
                etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureResponsiblePostDepartment() != null ||
                etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureLocation() != null ||
                etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureItSystem() != null ||
                etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureCenOrOtherStandardsApplied()!= null) {

            biomassEmissionsProcedureForm = ProcedureForm.builder()
                    .procedureDescription(etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureDescription())
                    .procedureDocumentName(etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureTitle())
                    .procedureReference(etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureReference())
                    .diagramReference(etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureLocation())
                    .itSystemUsed(etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureItSystem())
                    .appliedStandards(etsMeasMonitoringApproachDetails.getBiomassEmissionsProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return ProcedureOptionalForm.builder()
                .exist(etsMeasMonitoringApproachDetails.isBiomassEmissionsCalculation())
                .procedureForm(biomassEmissionsProcedureForm)
                .build();
    }

    public static ProcedureForm constructCorroboratingCalculations(EtsMeasMonitoringApproachDetails etsMeasMonitoringApproachDetails) {
        if (etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureTitle() != null ||
                etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureReference() != null||
                etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureDiagramReference() != null||
                etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureDescription() != null ||
                etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureResponsiblePostDepartment() != null ||
                etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureLocation() != null ||
                etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureItSystem() != null ||
                etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureDescription())
                    .procedureDocumentName(etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureTitle())
                    .procedureReference(etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureReference())
                    .diagramReference(etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureLocation())
                    .itSystemUsed(etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureItSystem())
                    .appliedStandards(etsMeasMonitoringApproachDetails.getCorroboratingCalculationsProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }
}

