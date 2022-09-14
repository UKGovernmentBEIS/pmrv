package uk.gov.pmrv.api.migration.permit.envpermitsandlicences;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EnvPermitOrLicenceRowMapper implements RowMapper<EnvPermitOrLicence>{

    @Override
    public EnvPermitOrLicence mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EnvPermitOrLicence.builder()
                    .etsAccountId(rs.getString("emitterId"))
                    .permitNumber(rs.getString("permitNumber"))
                    .permitType(rs.getString("permitType"))
                    .permitHolder(rs.getString("permitHolder"))
                    .issuingAuthority(rs.getString("competentBody"))
                    .build();
    }

}
