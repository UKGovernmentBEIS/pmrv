package uk.gov.pmrv.api.migration.permit.regulatedactivities;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class RegulatedActivityRowMapper implements RowMapper<RegulatedActivity>{

    @Override
    public RegulatedActivity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return RegulatedActivity.builder()
                    .etsAccountId(rs.getString("emitterId"))
                    .type(rs.getString("type"))
                    .quantity(rs.getString("capacity"))
                    .quantityUnit(rs.getString("capacityUnit"))
                    .greenHousesCategory(rs.getString("greenHousesCategory"))
                    .build();
    }

}
