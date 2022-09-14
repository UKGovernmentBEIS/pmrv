package uk.gov.pmrv.api.migration.permit.monitoringapproaches;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsMonitoringApproachesListRowMapper implements RowMapper<EtsMonitoringApproachesList>{

    @Override
    public EtsMonitoringApproachesList mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsMonitoringApproachesList.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .calculation("Yes".equals(rs.getString("Ma_calculation")))
                .fallback("Yes".equals(rs.getString("Ma_fallback_approach")))
                .measurement("Yes".equals(rs.getString("Ma_measurement")))
                .pfc("Yes".equals(rs.getString("Ma_monitoring_of_pfc")))
                .n20("Yes".equals(rs.getString("Ma_monitoring_of_n2o")))
                .co2("Yes".equals(rs.getString("Ma_monitoring_of_transferred_inherent_co2")))
                .build();
    }

}