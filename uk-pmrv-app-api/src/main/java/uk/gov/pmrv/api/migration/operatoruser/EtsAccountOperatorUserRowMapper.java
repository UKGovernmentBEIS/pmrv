package uk.gov.pmrv.api.migration.operatoruser;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EtsAccountOperatorUserRowMapper implements RowMapper<EtsAccountOperatorUser> {

    @Override
    public EtsAccountOperatorUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsAccountOperatorUser.builder()
                .email(rs.getString("email"))
                .firstName(rs.getString("firstName"))
                .lastName(rs.getString("lastName"))
                .roleName(rs.getString("roleName"))
                .compAuth(rs.getString("compAuth"))
                .emitterName(rs.getString("emitterName"))
                .emitterId(rs.getString("emitterId"))
                .build();
    }

}
