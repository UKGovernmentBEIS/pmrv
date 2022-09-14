package uk.gov.pmrv.api.migration.permit.managementprocedures;

import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProceduresDefinition;

public class ManagementProceduresMapper {

    static ManagementProceduresDefinition toManagementProceduresProcessDefinition(
        EtsManagementProceduresDefinition etsManagementProceduresDefinition) {

        return ManagementProceduresDefinition.builder()
            .procedureDocumentName(etsManagementProceduresDefinition.getProcedureTitle())
            .procedureReference(etsManagementProceduresDefinition.getProcedureReference())
            .diagramReference(etsManagementProceduresDefinition.getDiagramReference())
            .procedureDescription(etsManagementProceduresDefinition.getProcedureDescription())
            .responsibleDepartmentOrRole(etsManagementProceduresDefinition.getResponsiblePostOrDepartment())
            .locationOfRecords(etsManagementProceduresDefinition.getProcedureLocation())
            .itSystemUsed(etsManagementProceduresDefinition.getProcedureItSystem())
            .appliedStandards(etsManagementProceduresDefinition.getEnOrOtherStandardsApplied())
            .build();

    }
}
