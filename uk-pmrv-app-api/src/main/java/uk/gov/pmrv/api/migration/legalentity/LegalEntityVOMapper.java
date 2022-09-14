package uk.gov.pmrv.api.migration.legalentity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LegalEntityVOMapper implements RowMapper<LegalEntityVO> {

    @Override
    public LegalEntityVO mapRow(ResultSet rs, int i) throws SQLException {
        return LegalEntityVO.builder()
                .operatorId(rs.getString("operatorId"))
                .emitterId(rs.getString("emitterId"))
                .competentAuthority(rs.getString("competent_authority"))
                .type(rs.getString("type"))
                .name(rs.getString("name"))
                .referenceNumber(rs.getString("reference_number"))
                .line1(rs.getString("line1"))
                .line2(rs.getString("line2"))
                .city(rs.getString("city"))
                .postcode(rs.getString("postcode"))
                .country(rs.getString("country"))
                .emitterDisplayId(rs.getString("emitterDisplayId"))
                .emitterName(rs.getString("emitterName"))
                .build();
    }
}
