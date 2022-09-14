package uk.gov.pmrv.api.migration.permit.monitoringapproaches.n2o;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;

@Log4j2
@UtilityClass
public final class N2OApproachDetailsMapper {
    public static String constructN2OApproachDescription(EtsN2OMonitoringApproachDetails etsN2OMonitoringApproachDetails) {
        return etsN2OMonitoringApproachDetails.getApproachDescription();
    }

    public static ProcedureForm constructEmissionDeterminationProcedure(EtsN2OMonitoringApproachDetails etsN2OMonitoringApproachDetails) {
        if (etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureDescription() != null ||
                etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureTitle() != null||
                etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureReference() != null||
                etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureDiagramReference() != null ||
                etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureResponsiblePostDepartment() != null ||
                etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureLocation() != null ||
                etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureItSystem() != null ||
                etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureDescription())
                    .procedureDocumentName(etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureTitle())
                    .procedureReference(etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureReference())
                    .diagramReference(etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureLocation())
                    .itSystemUsed(etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureItSystem())
                    .appliedStandards(etsN2OMonitoringApproachDetails.getEmissionDeterminationProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static ProcedureForm constructReferencePeriodsProcedure(EtsN2OMonitoringApproachDetails etsN2OMonitoringApproachDetails) {
        if (etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureDescription() != null ||
                etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureTitle() != null||
                etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureReference() != null||
                etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureDiagramReference() != null ||
                etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureResponsiblePostDepartment() != null ||
                etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureLocation() != null ||
                etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureItSystem() != null ||
                etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureDescription())
                    .procedureDocumentName(etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureTitle())
                    .procedureReference(etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureReference())
                    .diagramReference(etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureLocation())
                    .itSystemUsed(etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureItSystem())
                    .appliedStandards(etsN2OMonitoringApproachDetails.getReferencePeriodsProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static ProcedureForm constructOperationalManagementProcedure(EtsN2OMonitoringApproachDetails etsN2OMonitoringApproachDetails) {
        if (etsN2OMonitoringApproachDetails.getOperationalManagementProcedureDescription() != null ||
                etsN2OMonitoringApproachDetails.getOperationalManagementProcedureTitle() != null||
                etsN2OMonitoringApproachDetails.getOperationalManagementProcedureReference() != null||
                etsN2OMonitoringApproachDetails.getOperationalManagementProcedureDiagramReference() != null ||
                etsN2OMonitoringApproachDetails.getOperationalManagementProcedureResponsiblePostDepartment() != null ||
                etsN2OMonitoringApproachDetails.getOperationalManagementProcedureLocation() != null ||
                etsN2OMonitoringApproachDetails.getOperationalManagementProcedureItSystem() != null ||
                etsN2OMonitoringApproachDetails.getOperationalManagementProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsN2OMonitoringApproachDetails.getOperationalManagementProcedureDescription())
                    .procedureDocumentName(etsN2OMonitoringApproachDetails.getOperationalManagementProcedureTitle())
                    .procedureReference(etsN2OMonitoringApproachDetails.getOperationalManagementProcedureReference())
                    .diagramReference(etsN2OMonitoringApproachDetails.getOperationalManagementProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsN2OMonitoringApproachDetails.getOperationalManagementProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsN2OMonitoringApproachDetails.getOperationalManagementProcedureLocation())
                    .itSystemUsed(etsN2OMonitoringApproachDetails.getOperationalManagementProcedureItSystem())
                    .appliedStandards(etsN2OMonitoringApproachDetails.getOperationalManagementProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static ProcedureForm constructN2oEmissionsProcedure(EtsN2OMonitoringApproachDetails etsN2OMonitoringApproachDetails) {
        if (etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureDescription() != null ||
                etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureTitle() != null||
                etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureReference() != null||
                etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureDiagramReference() != null ||
                etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureResponsiblePostDepartment() != null ||
                etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureLocation() != null ||
                etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureItSystem() != null ||
                etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureDescription())
                    .procedureDocumentName(etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureTitle())
                    .procedureReference(etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureReference())
                    .diagramReference(etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureLocation())
                    .itSystemUsed(etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureItSystem())
                    .appliedStandards(etsN2OMonitoringApproachDetails.getN2oEmissionsProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static ProcedureForm constructN2oConcentrationProcedure(EtsN2OMonitoringApproachDetails etsN2OMonitoringApproachDetails) {
        if (etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureDescription() != null ||
                etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureTitle() != null||
                etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureReference() != null||
                etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureDiagramReference() != null ||
                etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureResponsiblePostDepartment() != null ||
                etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureLocation() != null ||
                etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureItSystem() != null ||
                etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureDescription())
                    .procedureDocumentName(etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureTitle())
                    .procedureReference(etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureReference())
                    .diagramReference(etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureLocation())
                    .itSystemUsed(etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureItSystem())
                    .appliedStandards(etsN2OMonitoringApproachDetails.getN2oConcentrationProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static ProcedureForm constructQuantityProductProcedure(EtsN2OMonitoringApproachDetails etsN2OMonitoringApproachDetails) {
        if (etsN2OMonitoringApproachDetails.getQuantityProductProcedureDescription() != null ||
                etsN2OMonitoringApproachDetails.getQuantityProductProcedureTitle() != null||
                etsN2OMonitoringApproachDetails.getQuantityProductProcedureReference() != null||
                etsN2OMonitoringApproachDetails.getQuantityProductProcedureDiagramReference() != null ||
                etsN2OMonitoringApproachDetails.getQuantityProductProcedureResponsiblePostDepartment() != null ||
                etsN2OMonitoringApproachDetails.getQuantityProductProcedureLocation() != null ||
                etsN2OMonitoringApproachDetails.getQuantityProductProcedureItSystem() != null ||
                etsN2OMonitoringApproachDetails.getQuantityProductProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsN2OMonitoringApproachDetails.getQuantityProductProcedureDescription())
                    .procedureDocumentName(etsN2OMonitoringApproachDetails.getQuantityProductProcedureTitle())
                    .procedureReference(etsN2OMonitoringApproachDetails.getQuantityProductProcedureReference())
                    .diagramReference(etsN2OMonitoringApproachDetails.getQuantityProductProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsN2OMonitoringApproachDetails.getQuantityProductProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsN2OMonitoringApproachDetails.getQuantityProductProcedureLocation())
                    .itSystemUsed(etsN2OMonitoringApproachDetails.getQuantityProductProcedureItSystem())
                    .appliedStandards(etsN2OMonitoringApproachDetails.getQuantityProductProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static ProcedureForm constructQuantityMaterialsProcedure(EtsN2OMonitoringApproachDetails etsN2OMonitoringApproachDetails) {
        if (etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureDescription() != null ||
                etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureTitle() != null||
                etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureReference() != null||
                etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureDiagramReference() != null ||
                etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureResponsiblePostDepartment() != null ||
                etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureLocation() != null ||
                etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureItSystem() != null ||
                etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureCenOrOtherStandardsApplied()!= null) {

            return ProcedureForm.builder()
                    .procedureDescription(etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureDescription())
                    .procedureDocumentName(etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureTitle())
                    .procedureReference(etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureReference())
                    .diagramReference(etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureLocation())
                    .itSystemUsed(etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureItSystem())
                    .appliedStandards(etsN2OMonitoringApproachDetails.getQuantityMaterialsProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return null;
    }

    public static ProcedureOptionalForm constructGasFlowProcedureProcedure(EtsN2OMonitoringApproachDetails etsN2OMonitoringApproachDetails) {
        ProcedureForm gasFlowProcedureForm = null;

        //if no is selected we discard the data even if they exist
        if (!etsN2OMonitoringApproachDetails.isGasFlowProcedure()) {
            return ProcedureOptionalForm.builder()
                    .exist(etsN2OMonitoringApproachDetails.isGasFlowProcedure())
                    .build();
        }

        if (etsN2OMonitoringApproachDetails.getGasFlowProcedureDescription() != null ||
                etsN2OMonitoringApproachDetails.getGasFlowProcedureTitle() != null||
                etsN2OMonitoringApproachDetails.getGasFlowProcedureReference() != null||
                etsN2OMonitoringApproachDetails.getGasFlowProcedureDiagramReference() != null ||
                etsN2OMonitoringApproachDetails.getGasFlowProcedureResponsiblePostDepartment() != null ||
                etsN2OMonitoringApproachDetails.getGasFlowProcedureLocation() != null ||
                etsN2OMonitoringApproachDetails.getGasFlowProcedureItSystem() != null ||
                etsN2OMonitoringApproachDetails.getGasFlowProcedureCenOrOtherStandardsApplied()!= null) {

            gasFlowProcedureForm = ProcedureForm.builder()
                    .procedureDescription(etsN2OMonitoringApproachDetails.getGasFlowProcedureDescription())
                    .procedureDocumentName(etsN2OMonitoringApproachDetails.getGasFlowProcedureTitle())
                    .procedureReference(etsN2OMonitoringApproachDetails.getGasFlowProcedureReference())
                    .diagramReference(etsN2OMonitoringApproachDetails.getGasFlowProcedureDiagramReference())
                    .responsibleDepartmentOrRole(etsN2OMonitoringApproachDetails.getGasFlowProcedureResponsiblePostDepartment())
                    .locationOfRecords(etsN2OMonitoringApproachDetails.getGasFlowProcedureLocation())
                    .itSystemUsed(etsN2OMonitoringApproachDetails.getGasFlowProcedureItSystem())
                    .appliedStandards(etsN2OMonitoringApproachDetails.getGasFlowProcedureCenOrOtherStandardsApplied())
                    .build();
        }
        return ProcedureOptionalForm.builder()
                .exist(etsN2OMonitoringApproachDetails.isGasFlowProcedure())
                .procedureForm(gasFlowProcedureForm)
                .build();
    }
}
