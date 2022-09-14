package uk.gov.pmrv.api.migration.regulatoruser;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EtsRegulatorUserRowMapper implements RowMapper<EtsRegulatorUser> {

    @Override
    public EtsRegulatorUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsRegulatorUser.builder()
                .userId(rs.getString("userId"))
                .email(rs.getString("email"))
                .firstName(rs.getString("firstName"))
                .lastName(rs.getString("lastName"))
                .jobTitle(rs.getString("jobTitle"))
                .phoneNumber(rs.getString("phoneNumber"))
                .roleName(rs.getString("roleName"))
                .compAuth(rs.getString("compAuth"))
                .build();
    }

}
