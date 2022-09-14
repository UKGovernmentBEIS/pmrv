package uk.gov.pmrv.api.migration.verification_body;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

public class VerificationBodyMapper implements RowMapper<VerificationBodyVO> {

    @Override
    public VerificationBodyVO mapRow(ResultSet rs, int i) throws SQLException {
        return VerificationBodyVO.builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .accreditationType(EmissionTradingScheme.valueOf(rs.getString("accreditation_type")))
                .referenceNumber(rs.getString("reference_number"))
                .country(rs.getString("addr_country"))
                .line1(rs.getString("addr_line1"))
                .line2(rs.getString("addr_line2"))
                .city(rs.getString("addr_city"))
                .postcode(rs.getString("addr_postcode"))
                .isDisabled(rs.getBoolean("isDisabled"))
                .build();
    }
}
