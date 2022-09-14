package uk.gov.pmrv.api.migration.permit.activationdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EtsActivationDateRowMapper implements RowMapper<EtsActivationDate> {
    @Override
    public EtsActivationDate mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsActivationDate.builder()
            .etsAccountId(rs.getString("fldEmitterID"))
            .activationDate(rs.getDate("grantEffectiveDate") != null
                ? rs.getDate("grantEffectiveDate").toLocalDate()
                : null)
            .build();
    }
}
