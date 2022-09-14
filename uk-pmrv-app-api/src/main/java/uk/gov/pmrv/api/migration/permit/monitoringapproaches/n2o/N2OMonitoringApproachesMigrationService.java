package uk.gov.pmrv.api.migration.permit.monitoringapproaches.n2o;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;

import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class N2OMonitoringApproachesMigrationService {

    private final N2OMonitoringApproachesDetailsMigrationService n2OMonitoringApproachesDetailsMigrationService;


    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        n2OMonitoringApproachesDetailsMigrationService.populateSection(accountsToMigratePermit, permits);
    }
}
