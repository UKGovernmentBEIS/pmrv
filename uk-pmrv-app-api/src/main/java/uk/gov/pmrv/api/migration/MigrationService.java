package uk.gov.pmrv.api.migration;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MigrationService {
    
    @Transactional
    void migrateDryRun(String ids);
    
    List<String> migrate(String ids);
    
    String getResource();
}
