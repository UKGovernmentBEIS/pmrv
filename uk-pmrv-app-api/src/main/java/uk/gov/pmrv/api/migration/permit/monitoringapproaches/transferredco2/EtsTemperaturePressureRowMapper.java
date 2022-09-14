package uk.gov.pmrv.api.migration.permit.monitoringapproaches.transferredco2;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsTemperaturePressureRowMapper implements RowMapper<EtsTemperaturePressure> {

    @Override
    public EtsTemperaturePressure mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsTemperaturePressure.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .reference(rs.getString("Mtico2_measurement_device_ref"))
                .type(rs.getString("Mtico2_type_of_measurement_device"))
                .location(rs.getString("Mtico2_device_location"))
                .build();
    }
}
