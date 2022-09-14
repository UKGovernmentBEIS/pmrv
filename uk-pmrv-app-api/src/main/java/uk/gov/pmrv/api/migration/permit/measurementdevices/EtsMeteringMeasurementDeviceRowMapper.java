package uk.gov.pmrv.api.migration.permit.measurementdevices;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EtsMeteringMeasurementDeviceRowMapper implements RowMapper<EtsMeteringMeasurementDevice> {
    @Override
    public EtsMeteringMeasurementDevice mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return EtsMeteringMeasurementDevice.builder()
            .emitterId(resultSet.getString("emitterId"))
            .reference(resultSet.getString("reference").trim())
            .type(resultSet.getString("type").trim())
            .range(resultSet.getString("range").trim())
            .rangeUnits(resultSet.getString("rangeUnits").trim())
            .uncertainty(resultSet.getString("uncertainty").trim())
            .location(resultSet.getString("location").trim())
            .build();
    }
}
