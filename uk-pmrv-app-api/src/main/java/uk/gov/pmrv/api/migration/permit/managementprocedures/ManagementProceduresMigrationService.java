package uk.gov.pmrv.api.migration.permit.managementprocedures;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.envmanagementsystem.EnvironmentalManagementSystemSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringreporting.MonitoringReportingMigrationService;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;

import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class ManagementProceduresMigrationService {

    private final MonitoringReportingMigrationService monitoringReportingMigrationService;
    private final DataFlowActivitiesMigrationService dataFlowActivitiesMigrationService;
    private final EnvironmentalManagementSystemSectionMigrationService environmentalManagementSystemSectionMigrationService;
    private final ManagementProceduresDefinitionMigrationService managementProceduresDefinitionMigrationService;

    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {

        initializePermitManagementProcedures(permits);

        monitoringReportingMigrationService.populateSection(accountsToMigratePermit, permits);
        environmentalManagementSystemSectionMigrationService.populateSection(accountsToMigratePermit, permits);
        dataFlowActivitiesMigrationService.populateSection(accountsToMigratePermit, permits);
        managementProceduresDefinitionMigrationService.populateSection(accountsToMigratePermit, permits);

        calculateManagementProceduresExistFlag(permits);
    }

    private void initializePermitManagementProcedures(Map<Long, PermitMigrationContainer> permits) {
        permits.forEach((aLong, permitMigrationContainer) -> {
            ManagementProcedures managementProcedures = ManagementProcedures.builder().managementProceduresExist(false).build();
            permitMigrationContainer.getPermitContainer().getPermit().setManagementProcedures(managementProcedures);
        });
    }

    private void calculateManagementProceduresExistFlag(Map<Long, PermitMigrationContainer> permits) {
        permits.values().forEach(permitMigrationContainer -> {
            ManagementProcedures managementProcedures = permitMigrationContainer.getPermitContainer().getPermit().getManagementProcedures();
            managementProcedures.setManagementProceduresExist(areManagementProceduresDefined(managementProcedures));
        } );
    }

    private boolean areManagementProceduresDefined(ManagementProcedures managementProcedures) {
        return managementProcedures.getMonitoringReporting() != null
        || managementProcedures.getAssignmentOfResponsibilities() != null
        || managementProcedures.getMonitoringPlanAppropriateness() != null
        || managementProcedures.getDataFlowActivities() != null
        || managementProcedures.getQaDataFlowActivities() != null
        || managementProcedures.getReviewAndValidationOfData() != null
        || managementProcedures.getAssessAndControlRisk() != null
        || managementProcedures.getQaMeteringAndMeasuringEquipment() != null
        || managementProcedures.getCorrectionsAndCorrectiveActions() != null
        || managementProcedures.getControlOfOutsourcedActivities() != null
        || managementProcedures.getRecordKeepingAndDocumentation() != null
        || managementProcedures.getEnvironmentalManagementSystem() != null;
    }
}
