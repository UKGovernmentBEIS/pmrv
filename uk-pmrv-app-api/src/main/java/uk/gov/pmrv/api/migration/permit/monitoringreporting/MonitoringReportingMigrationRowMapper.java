package uk.gov.pmrv.api.migration.permit.monitoringreporting;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MonitoringReportingMigrationRowMapper implements RowMapper<EtsMonitoringReporting> {

    @Override
    public EtsMonitoringReporting mapRow(ResultSet rs, int i) throws SQLException {
        return EtsMonitoringReporting.builder()
                .emitterId(rs.getString("emitterId"))
                .jobTitle(rs.getString("jobTitle"))
                .mainDuties(rs.getString("mainDuties"))
                .build();
    }
}
