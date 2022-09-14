package uk.gov.pmrv.api.migration.permit.monitoringapproaches.n2o;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsN2OMonitoringApproachDetails {
    private String etsAccountId;
    private String approachDescription;

    private String emissionDeterminationProcedureDescription;
    private String emissionDeterminationProcedureTitle;
    private String emissionDeterminationProcedureReference;
    private String emissionDeterminationProcedureDiagramReference;
    private String emissionDeterminationProcedureResponsiblePostDepartment;
    private String emissionDeterminationProcedureLocation;
    private String emissionDeterminationProcedureItSystem;
    private String emissionDeterminationProcedureCenOrOtherStandardsApplied;

    private String referencePeriodsProcedureDescription;
    private String referencePeriodsProcedureTitle;
    private String referencePeriodsProcedureReference;
    private String referencePeriodsProcedureDiagramReference;
    private String referencePeriodsProcedureResponsiblePostDepartment;
    private String referencePeriodsProcedureLocation;
    private String referencePeriodsProcedureItSystem;
    private String referencePeriodsProcedureCenOrOtherStandardsApplied;

    private String operationalManagementProcedureDescription;
    private String operationalManagementProcedureTitle;
    private String operationalManagementProcedureReference;
    private String operationalManagementProcedureDiagramReference;
    private String operationalManagementProcedureResponsiblePostDepartment;
    private String operationalManagementProcedureLocation;
    private String operationalManagementProcedureItSystem;
    private String operationalManagementProcedureCenOrOtherStandardsApplied;

    private String n2oEmissionsProcedureDescription;
    private String n2oEmissionsProcedureTitle;
    private String n2oEmissionsProcedureReference;
    private String n2oEmissionsProcedureDiagramReference;
    private String n2oEmissionsProcedureResponsiblePostDepartment;
    private String n2oEmissionsProcedureLocation;
    private String n2oEmissionsProcedureItSystem;
    private String n2oEmissionsProcedureCenOrOtherStandardsApplied;

    private String n2oConcentrationProcedureDescription;
    private String n2oConcentrationProcedureTitle;
    private String n2oConcentrationProcedureReference;
    private String n2oConcentrationProcedureDiagramReference;
    private String n2oConcentrationProcedureResponsiblePostDepartment;
    private String n2oConcentrationProcedureLocation;
    private String n2oConcentrationProcedureItSystem;
    private String n2oConcentrationProcedureCenOrOtherStandardsApplied;

    private String quantityProductProcedureDescription;
    private String quantityProductProcedureTitle;
    private String quantityProductProcedureReference;
    private String quantityProductProcedureDiagramReference;
    private String quantityProductProcedureResponsiblePostDepartment;
    private String quantityProductProcedureLocation;
    private String quantityProductProcedureItSystem;
    private String quantityProductProcedureCenOrOtherStandardsApplied;

    private String quantityMaterialsProcedureDescription;
    private String quantityMaterialsProcedureTitle;
    private String quantityMaterialsProcedureReference;
    private String quantityMaterialsProcedureDiagramReference;
    private String quantityMaterialsProcedureResponsiblePostDepartment;
    private String quantityMaterialsProcedureLocation;
    private String quantityMaterialsProcedureItSystem;
    private String quantityMaterialsProcedureCenOrOtherStandardsApplied;

    private boolean gasFlowProcedure;
    private String gasFlowProcedureDescription;
    private String gasFlowProcedureTitle;
    private String gasFlowProcedureReference;
    private String gasFlowProcedureDiagramReference;
    private String gasFlowProcedureResponsiblePostDepartment;
    private String gasFlowProcedureLocation;
    private String gasFlowProcedureItSystem;
    private String gasFlowProcedureCenOrOtherStandardsApplied;
}
