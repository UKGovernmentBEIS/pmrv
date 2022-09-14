package uk.gov.pmrv.api.migration.accountidentification;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import java.util.List;

@Log4j2
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
public class MigrationAccountIdentificationService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final InstallationAccountIdentifierService installationAccountIdentifierService;

    private static final String QUERY_BASE = "select MAX(e.fldEmitterDisplayID) from tblEmitter e";

    @Override
    public List<String> migrate(String ids) {
        Long maxAccountId = migrationJdbcTemplate.queryForObject(QUERY_BASE, Long.class);

        // Update account identifier to max account id
        installationAccountIdentifierService.updateAccountIdentifier(maxAccountId);

        return List.of();
    }

    @Override
    public String getResource() {
        return "account-id";
    }
}
