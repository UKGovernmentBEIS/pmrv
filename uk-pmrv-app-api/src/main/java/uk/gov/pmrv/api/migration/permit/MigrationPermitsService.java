package uk.gov.pmrv.api.migration.permit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationPermitsService extends MigrationBaseService {

    private final PermitBuilderService permitBuilderService;
    private final MigrationPermitService migrationPermitService;

    @Override
    public List<String> migrate(String etsAccountIdsString) {
        List<String> migrationResults = new ArrayList<>();

        List<String> etsAccountIds = !StringUtils.isBlank(etsAccountIdsString)
            ? new ArrayList<>(Arrays.asList(etsAccountIdsString.split("\\s*,\\s*")))
            : new ArrayList<>();

        Map<Long, PermitMigrationContainer> permits =
            permitBuilderService.buildPermits(etsAccountIds, migrationResults);
        int failedCounter = 0;
        for (Map.Entry<Long, PermitMigrationContainer> entry : permits.entrySet()) {
            try {
                migrationPermitService.migratePermit(entry.getKey(), entry.getValue(), migrationResults);
            } catch (Exception e) {
                log.error(e.getMessage());
                failedCounter++;
            }
        }
        migrationResults.add("Migration of permits results: " + failedCounter + "/" + permits.size() + " failed");
        return migrationResults;
    }

    @Override
    public String getResource() {
        return "permits";
    }
}
