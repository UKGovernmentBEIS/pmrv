package uk.gov.pmrv.api.migration.permit.monitoringapproaches.inherentco2;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsInherentCO2MonitoringApproachDescriptionRowMapper implements RowMapper<EtsInherentCO2MonitoringApproachDescription> {

    @Override
    public EtsInherentCO2MonitoringApproachDescription mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsInherentCO2MonitoringApproachDescription.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .approachDescription(rs.getString("Mtico2_monitoring_of_transferred_inherent_co2_description"))
                .build();
    }
}
