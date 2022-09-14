package uk.gov.pmrv.api.migration;

import java.util.List;

public abstract class MigrationBaseService implements MigrationService {
    
    @Override
    public void migrateDryRun(String ids) {
        List<String> errorsOccurred = migrate(ids);

        // rollback
        throw new DryRunException(errorsOccurred);
    }
}
