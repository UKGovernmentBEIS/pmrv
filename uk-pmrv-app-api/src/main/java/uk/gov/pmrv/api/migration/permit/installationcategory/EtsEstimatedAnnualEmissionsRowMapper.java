package uk.gov.pmrv.api.migration.permit.installationcategory;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EtsEstimatedAnnualEmissionsRowMapper implements RowMapper<EtsEstimatedAnnualEmissions>{

    @Override
    public EtsEstimatedAnnualEmissions mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsEstimatedAnnualEmissions.builder()
                    .emitterId(rs.getString("emitterId"))
                    .estimatedAnnualEmission(rs.getString("estimatedAnnualEmission"))
                    .build();
    }

}
