package uk.gov.pmrv.api.migration.verifier.domain;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VerifierUserVOMapper implements RowMapper<VerifierUserVO> {

    @Override
    public VerifierUserVO mapRow(ResultSet rs, int i) throws SQLException {
        return VerifierUserVO.builder()
                .roleCode(rs.getString("code"))
                .email(rs.getString("email"))
                .firstName(rs.getString("firstName"))
                .lastName(rs.getString("lastName"))
                .phoneNumber(rs.getString("phoneNumber"))
                .verificationBodyId(rs.getString("verification_body_id"))
                .verificationBodyName(rs.getString("verification_body_name"))
                .userId(rs.getString("user_id"))
                .build();
    }
}
