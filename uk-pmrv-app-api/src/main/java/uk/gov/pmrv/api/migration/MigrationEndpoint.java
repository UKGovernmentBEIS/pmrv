package uk.gov.pmrv.api.migration;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@WebEndpoint(id = "migration")
@RequiredArgsConstructor
public class MigrationEndpoint {
    private final List<MigrationService> migrationServices;

    @WriteOperation
    public List<String> migrate(@Selector String resource, @Selector ExecutionMode mode, @Nullable String ids) {
        MigrationService migrationService = migrationServices.stream()
            .filter(service -> service.getResource().equals(resource))
            .findAny()
            .orElseThrow(() -> new UnsupportedOperationException("resource not supported"));

        switch (mode) {
            case DRY:
                try {
                    migrationService.migrateDryRun(ids);
                } catch (DryRunException e) {
                    return e.getErrors();
                }

                return Collections.emptyList();
            case COMMIT:
                return migrationService.migrate(ids);
            default:
                break;
        }

        return Collections.emptyList();
    }
}
