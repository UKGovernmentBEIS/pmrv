package uk.gov.pmrv.api.migration.permit.monitoringapproaches.inherentco2;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;

import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class InherentCO2MonitoringApproachesMigrationService {

    private final InherentCO2MonitoringApproachDescriptionMigrationService inherentCO2MonitoringApproachDescriptionMigrationService;

    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        inherentCO2MonitoringApproachDescriptionMigrationService.populateSection(accountsToMigratePermit, permits);
    }
}
