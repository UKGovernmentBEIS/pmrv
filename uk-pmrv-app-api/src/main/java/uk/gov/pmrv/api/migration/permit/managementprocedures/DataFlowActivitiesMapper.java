package uk.gov.pmrv.api.migration.permit.managementprocedures;

import uk.gov.pmrv.api.permit.domain.managementprocedures.DataFlowActivities;

public class DataFlowActivitiesMapper {

    static DataFlowActivities toDataFlowActivities(
        EtsDataFlowActivities etsManagementProceduresDefinition) {

        return DataFlowActivities.builder()
            .procedureDocumentName(etsManagementProceduresDefinition.getProcedureTitle())
            .procedureReference(etsManagementProceduresDefinition.getProcedureReference())
            .diagramReference(etsManagementProceduresDefinition.getDiagramReference())
            .procedureDescription(etsManagementProceduresDefinition.getProcedureDescription())
            .responsibleDepartmentOrRole(etsManagementProceduresDefinition.getResponsiblePostOrDepartment())
            .locationOfRecords(etsManagementProceduresDefinition.getProcedureLocation())
            .itSystemUsed(etsManagementProceduresDefinition.getProcedureItSystem())
            .appliedStandards(etsManagementProceduresDefinition.getEnOrOtherStandardsApplied())
            .primaryDataSources(etsManagementProceduresDefinition.getPrimaryDataSources())
            .processingSteps(etsManagementProceduresDefinition.getRelevantProcessingSteps())
            .build();
    }
}
