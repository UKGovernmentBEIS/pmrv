package uk.gov.pmrv.api.migration.permit.emissionpoints;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmissionPointMigrationServiceRowMapper implements RowMapper<EtsEmissionPoint> {

    @Override
    public EtsEmissionPoint mapRow(ResultSet rs, int i) throws SQLException {
        return EtsEmissionPoint.builder()
                .emitterId(rs.getString("emitterId"))
                .reference(rs.getString("reference").trim())
                .description(rs.getString("description").trim())
                .build();
    }
}
